package gr.aegean.icsd.newspaperapp.model.service;

import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.model.repository.StoryRepository;
import gr.aegean.icsd.newspaperapp.model.repository.TopicRepository;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Validated
public class TopicService {

    private final StoryRepository storyRepository;
    private final TopicRepository topicRepository;

    private final Set<TopicState> allowedCuratorStates;
    private final Set <TopicState> allowedJournalistStates;
    private final Set <TopicState> allowedVisitorStates;


    public TopicService(StoryRepository storyRepository, TopicRepository topicRepository) {

        this.storyRepository = storyRepository;
        this.topicRepository = topicRepository;

        allowedCuratorStates = new HashSet<>();
        allowedCuratorStates.add(TopicState.SUBMITTED);
        allowedCuratorStates.add(TopicState.APPROVED);

        allowedVisitorStates = new HashSet<>();
        allowedCuratorStates.add(TopicState.APPROVED);

        allowedJournalistStates = new HashSet<>();
        allowedCuratorStates.add(TopicState.APPROVED);

    }



    @PreAuthorize("hasAnyAuthority('ROLE_CURATOR', 'ROLE_JOURNALIST')")
    public void createTopic(@NotBlank String name, Integer parentTopicID) {

        String authorID = SecurityContextHolder.getContext().getAuthentication().getName();

        if (parentTopicID == null) {

            User author = new User(authorID);
            Topic newTopic = new Topic(name, author);

            topicRepository.save(newTopic);
        }
        else {

            Optional<Topic> requestedParentTopic = topicRepository.findById(parentTopicID.longValue());

            if (requestedParentTopic.isPresent() &&
                requestedParentTopic.get().getState().equals(TopicState.APPROVED)) {

                User author = new User(authorID);
                Topic newTopic = new Topic(name, author, requestedParentTopic.get());

                topicRepository.save(newTopic);
            }

        }

    }


    @PreAuthorize("hasAnyAuthority('ROLE_CURATOR', 'ROLE_JOURNALIST')")
    public void updateTopic(@Positive long id, String newName, Integer parentTopicID) {

        Optional<Topic> requestedTopic = topicRepository.findById(id);
        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        // Requested Topic does not exist, or requested topic does not belong to this Journalist
        if (requestedTopic.isEmpty() ||
            (userRole.equals("[ROLE_JOURNALIST]") && !requestedTopic.get().getAuthor().getUsername().equals(userName)) ) {
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


    @Transactional(readOnly = true)
    public List<Topic> showAllTopics() {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return topicRepository.findAllTopics(allowedVisitorStates);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return topicRepository.findAllTopicsForJournalist(allowedJournalistStates, username);
            }
            case "[ROLE_CURATOR]" -> {
                return topicRepository.findAllTopics(allowedCuratorStates);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }


    @Transactional(readOnly = true)
    public List<Topic> searchTopicByName(@NotBlank String name) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return topicRepository.findByNameContainingIgnoreCaseAndStateIn(name, allowedVisitorStates);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return topicRepository.findTopicsByNameForJouralist(name, allowedJournalistStates, username);
            }
            case "[ROLE_CURATOR]" -> {
                return topicRepository.findByNameContainingIgnoreCaseAndStateIn(name, allowedCuratorStates);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }


}
