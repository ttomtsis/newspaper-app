package gr.aegean.icsd.newspaperapp.model.service;

import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.model.repository.TopicRepository;
import gr.aegean.icsd.newspaperapp.security.UserUtils;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Class servicing controller requests about
 * the Topic entity
 */
@Service
@Transactional
@Validated
public class TopicService {

    private final TopicRepository topicRepository;

    // Allowed Topic states per User, a User cannot access a
    // Topic whose state is not in this List.
    // ( Except the Journalist, in case he owns the Topic )
    private final Set<TopicState> allowedCuratorStates;
    private final Set <TopicState> allowedJournalistStates;
    private final Set <TopicState> allowedVisitorStates;


    public TopicService(TopicRepository topicRepository) {

        this.topicRepository = topicRepository;

        allowedCuratorStates = new HashSet<>();
        allowedCuratorStates.add(TopicState.SUBMITTED);
        allowedCuratorStates.add(TopicState.APPROVED);

        allowedVisitorStates = new HashSet<>();
        allowedCuratorStates.add(TopicState.APPROVED);

        allowedJournalistStates = new HashSet<>();
        allowedCuratorStates.add(TopicState.APPROVED);

    }



    /**
     * Create a new Topic entity and persist it in the database
     *
     * @param name Name of the new Topic
     * @param parentTopicID (Optional) Parent Topic of the new Topic
     */
    @PreAuthorize("hasAnyAuthority('ROLE_CURATOR', 'ROLE_JOURNALIST')")
    public Topic createTopic(@NotBlank String name, Integer parentTopicID) {

        String authorID = UserUtils.getUsername();

        if (parentTopicID == null) {

            User author = new User(authorID);
            Topic newTopic = new Topic(name, author);

            topicRepository.save(newTopic);
            return newTopic;
        }
        else {

            Optional<Topic> requestedParentTopic = topicRepository.findById(parentTopicID.longValue());

            if (requestedParentTopic.isPresent() &&
                requestedParentTopic.get().getState().equals(TopicState.APPROVED)) {

                User author = new User(authorID);
                Topic newTopic = new Topic(name, author, requestedParentTopic.get());

                topicRepository.save(newTopic);
                return newTopic;
            }

            throw new RuntimeException("Invalid parent Topic provided");
        }

    }



    /**
     * Updates the name and/or parent topic of the specified Topic
     *
     * @param id ID of the Topic to be updated
     * @param newName New name of the Topic
     * @param parentTopicID New parent Topic of the Topic
     */
    @PreAuthorize("hasAnyAuthority('ROLE_CURATOR', 'ROLE_JOURNALIST')")
    public void updateTopic(@Positive long id, String newName, Integer parentTopicID) {

        if (newName.isBlank() && parentTopicID == null) { throw new RuntimeException("No arguments provided"); }

        Optional<Topic> requestedTopic = topicRepository.findById(id);
        String userName = UserUtils.getUsername();

        // Requested Topic does not exist, or requested topic does not belong to this Journalist
        if (requestedTopic.isEmpty() ||
            (UserUtils.isJournalist() && !requestedTopic.get().getAuthor().getUsername().equals(userName)) ) {
            throw new RuntimeException("Requested Topic was not found");
        }

        // Requested Topic is not in a valid state
        if(requestedTopic.get().getState().equals(TopicState.APPROVED)) {
            throw new RuntimeException("Requested Topic cannot be modified");
        }

        if (!newName.isBlank()) {requestedTopic.get().setName(newName);}

        if (parentTopicID != null){

            Optional<Topic> newParentTopic = topicRepository.findById(parentTopicID.longValue());

            if (newParentTopic.isPresent() && newParentTopic.get().getState().equals(TopicState.APPROVED)) {
                requestedTopic.get().setParent(newParentTopic.get());
            }

        }

        topicRepository.save(requestedTopic.get());

    }



    /**
     * Approve the specified Topic, and set its status to {@link TopicState#APPROVED APPROVED}
     * IF AND ONLY IF its status had been {@link TopicState#SUBMITTED}
     *
     * @param id ID of the Topic to be approved
     */
    @PreAuthorize("hasAuthority('ROLE_CURATOR')")
    public void approveTopic(@Positive long id) {

        Optional<Topic> requestedTopic = topicRepository.findById(id);

        if (requestedTopic.isEmpty()) {
            throw new RuntimeException("Requested Topic was not found");
        }

        if (!requestedTopic.get().getState().equals(TopicState.SUBMITTED)) {
            throw new RuntimeException("Requested Topic is in an invalid state and cannot be approved");
        }

        requestedTopic.get().setState(TopicState.APPROVED);

        topicRepository.save(requestedTopic.get());

    }



    /**
     * Reject a Topic and delete it from the database,
     * IF AND ONLY IF it's state had been {@link TopicState#SUBMITTED}
     *
     * @param id ID of the Topic to be deleted
     */
    @PreAuthorize("hasAuthority('ROLE_CURATOR')")
    public void rejectTopic(@Positive long id) {

        Optional<Topic> requestedTopic = topicRepository.findById(id);

        if (requestedTopic.isEmpty()) {
            throw new RuntimeException("Requested Topic was not found");
        }

        if (!requestedTopic.get().getState().equals(TopicState.SUBMITTED)) {
            throw new RuntimeException("Requested Topic is in an invalid state and cannot be approved");
        }

        topicRepository.deleteById(id);

    }



    /**
     * Show details about a specific Topic
     *
     * @param topicID ID of the specified Topic
     *
     * @return Requested Topic entity
     */
    @Transactional(readOnly = true)
    public Topic showTopic(@Positive long topicID) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return topicRepository.findByIdAndStateIn(topicID, allowedVisitorStates)
                        .orElseThrow(() -> new RuntimeException("Requested Topic was not found"));            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return topicRepository.findTopicByIDForJournalist(topicID, allowedJournalistStates, username)
                        .orElseThrow(() -> new RuntimeException("Requested Topic was not found"));
            }
            case "[ROLE_CURATOR]" -> {
                return topicRepository.findByIdAndStateIn(topicID, allowedCuratorStates)
                        .orElseThrow(() -> new RuntimeException("Requested Topic was not found"));
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }



    /**
     * Show all Topics currently persisted in the database
     *
     * @return A list of all Topics currently in the database
     */
    @Transactional(readOnly = true)
    public Page<Topic> showAllTopics(@NotNull Pageable pageable) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        if (UserUtils.isVisitor()) {
            return topicRepository.findAllTopics(allowedVisitorStates, pageable);
        }
        else if ( UserUtils.isJournalist()) {
            String username = UserUtils.getUsername();
            return topicRepository.findAllTopicsForJournalist(allowedJournalistStates, username, pageable);
        }
        else if (UserUtils.isCurator()) {
            return topicRepository.findAllTopics(allowedCuratorStates, pageable);
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }



    /**
     * Show all Topics whose name matches the provided name
     *
     * @param name Provided name
     *
     * @return List of all Topics matching the provided name
     */
    @Transactional(readOnly = true)
    public Page<Topic> searchTopicByName(@NotBlank String name, @NotNull Pageable pageable) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        if (UserUtils.isVisitor()) {
            return topicRepository.findByNameContainingIgnoreCaseAndStateIn(name, allowedVisitorStates, pageable);
        }
        else if (UserUtils.isJournalist()) {
            String username = UserUtils.getUsername();
            return topicRepository.findTopicsByNameForJournalist(name, allowedJournalistStates, username, pageable);
        }
        else if (UserUtils.isCurator()) {
            return topicRepository.findByNameContainingIgnoreCaseAndStateIn(name, allowedCuratorStates, pageable);
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }


}
