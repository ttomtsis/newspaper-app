package gr.aegean.icsd.newspaperapp.model.service;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.model.repository.StoryRepository;
import gr.aegean.icsd.newspaperapp.model.repository.TopicRepository;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

/**
 * Class servicing controller requests about
 * the Story entity
 */
@Service
@Transactional
@Validated
public class StoryService {

    private final StoryRepository storyRepository;
    private final TopicRepository topicRepository;

    // Allowed Story states per User, a User cannot access a
    // Story whose state is not in this List.
    // ( Except the Journalist, in case he owns the Story )
    private final Set <StoryState> allowedCuratorStates;
    private final Set <StoryState> allowedJournalistStates;
    private final Set <StoryState> allowedVisitorStates;


    public StoryService(StoryRepository storyRepository, TopicRepository topicRepository) {

        this.storyRepository = storyRepository;
        this.topicRepository = topicRepository;

        allowedCuratorStates = new HashSet<>();
        allowedCuratorStates.add(StoryState.SUBMITTED);
        allowedCuratorStates.add(StoryState.APPROVED);
        allowedCuratorStates.add(StoryState.PUBLISHED);

        allowedVisitorStates = new HashSet<>();
        allowedVisitorStates.add(StoryState.PUBLISHED);

        allowedJournalistStates = new HashSet<>();
        allowedJournalistStates.add(StoryState.PUBLISHED);

    }


    /**
     * Takes as input a List of topic id's then, queries the database
     * and transforms the input list into a list of Topic entities
     *
     * @param topicIDs Set of Topic id's
     *
     * @return Set of Topic entities
     */
    private Set<Topic> createTopicsListFromIDs(List<Integer> topicIDs) {

        Set<Topic> topicsList = new HashSet<>();

        for (Integer id : topicIDs) {

            Optional<Topic> savedTopic = topicRepository.findById(Long.valueOf(id));

            if (savedTopic.isPresent() && savedTopic.get().getState().equals(TopicState.APPROVED)) {

                topicsList.add(savedTopic.get());

            }

        }

        return topicsList;

    }



    /**
     * Create a new Story entity and persist it in the database
     *
     * @param storyName Name of the new Story
     * @param storyContent Content of the new Story
     * @param topicIDs (Optional) List of Topics this Story will belong to
     */
    @PreAuthorize("hasAuthority('ROLE_JOURNALIST')")
    public void createStory(@NotBlank String storyName, @NotBlank String storyContent, List<Integer> topicIDs) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (topicIDs == null)  {

            Story newStory = new Story(storyName, new User(username), storyContent);

            storyRepository.save(newStory);

        }
        else {

            Set<Topic> topicsList = createTopicsListFromIDs(topicIDs);
            Story newStory = new Story(storyName, new User(username), storyContent, topicsList);

            storyRepository.save(newStory);

        }

    }



    /**
     * Update a Story's name, content, and/or Topics
     *
     * @param storyID ID of the Story to be updated
     * @param newName New name of the Story
     * @param newContent New content of the Story
     * @param topicIDs New Topics this Story will belong to
     */
    @PreAuthorize("hasAuthority('ROLE_JOURNALIST')")
    public void updateStory(@Positive Long storyID, String newName, String newContent, List<Integer> topicIDs) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Story> savedStory = storyRepository.findById(storyID);

        // Does the Story exist ? If so does it belong to the User ?
        if (savedStory.isEmpty() || !savedStory.get().getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("Story not found");
        }

        // Is the Story in the correct State ?
        if (!savedStory.get().getState().equals(StoryState.CREATED)) {
            throw new RuntimeException("The Story cannot be modified in this state");
        }

        Story updatedStory = savedStory.get();

        if (newName != null && !newName.isBlank()) { updatedStory.setName(newName); }

        if (newContent != null && !newContent.isBlank()) { updatedStory.setContent(newContent); }

        if (topicIDs != null) {
            Set<Topic> newTopicsList = createTopicsListFromIDs(topicIDs);
            updatedStory.updateTopics(newTopicsList);
        }

        storyRepository.save(updatedStory);

    }


    /**
     * Search Stories matching the provided name
     *
     * @param name Provided name
     *
     * @return List of Stories matching the provided name
     */
    @Transactional(readOnly = true)
    public List<Story> findStoriesByName(@NotBlank String name) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return storyRepository.findByNameContainingIgnoreCaseAndStateIn(name, allowedVisitorStates);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return storyRepository.findByNameForJouralist(name, allowedJournalistStates, username);
            }
            case "[ROLE_CURATOR]" -> {
                return storyRepository.findByNameContainingIgnoreCaseAndStateIn(name, allowedCuratorStates);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }



    /**
     * Search Stories matching the provided content
     *
     * @param content Provided content
     *
     * @return List of Stories matching the provided content
     */
    @Transactional(readOnly = true)
    public List<Story> findStoriesByContent(@NotBlank String content) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return storyRepository.findByContentContainingIgnoreCaseAndStateIn(content, allowedVisitorStates);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return storyRepository.findByContentForJouralist(content, allowedJournalistStates, username);
            }
            case "[ROLE_CURATOR]" -> {
                return storyRepository.findByContentContainingIgnoreCaseAndStateIn(content, allowedCuratorStates);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }



    /**
     * Show all Stories currently persisted in the database
     *
     * @return List of all Stories currently persisted in the database
     */
    @Transactional(readOnly = true)
    public List<Story> findAllStories() {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return storyRepository.findAllStories(allowedVisitorStates);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return storyRepository.findAllStoriesForJournalist(allowedJournalistStates, username);
            }
            case "[ROLE_CURATOR]" -> {
                return storyRepository.findAllStories(allowedCuratorStates);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }



    /**
     * Search Stories matching the provided name and content
     *
     * @param name Provided name
     * @param content Provided content
     *
     * @return List of Stories matching the provided name and content
     */
    @Transactional(readOnly = true)
    public List<Story> findStoriesByContentAndName(@NotBlank String name, @NotBlank String content) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return storyRepository.findByNameContainingIgnoreCaseAndStateInAndContentIgnoreCase(name, allowedVisitorStates, content);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return storyRepository.findByNameAndContentForJouralist(name, allowedJournalistStates, username, content);
            }
            case "[ROLE_CURATOR]" -> {
                return storyRepository.findByNameContainingIgnoreCaseAndStateInAndContentIgnoreCase(name, allowedCuratorStates, content);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }



    /**
     * Searches for stories whose creation date falls within the specified date range.
     *
     * @param minDate the start of the date range (inclusive)
     * @param maxDate the end of the date range (inclusive)
     *
     * @return a list of stories that were created within the specified date range
     */

    @Transactional(readOnly = true)
    public List<Story> findStoriesByDateRange(@NotNull Date minDate, @NotNull Date maxDate) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return storyRepository.findByCreationDateBetweenAndStateIn(minDate, maxDate, allowedVisitorStates);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return storyRepository.findByDateRangeForJouralist(minDate, maxDate, allowedJournalistStates, username);
            }
            case "[ROLE_CURATOR]" -> {
                return storyRepository.findByCreationDateBetweenAndStateIn(minDate, maxDate, allowedCuratorStates);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }



    /**
     * Search Stories whose state matches the provided state
     *
     * @param state Provided state
     *
     * @return List of Stories whose state matches the provided state
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_CURATOR', 'ROLE_JOURNALIST')")
    public List<Story> findStoriesByState(@NotBlank StoryState state) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                if (allowedJournalistStates.contains(state)) {return storyRepository.findByState(state);}
                else {return storyRepository.findByStateForJouralist(state, username);}
            }
            case "[ROLE_CURATOR]" -> {
                if (allowedCuratorStates.contains(state)) {return storyRepository.findByState(state);}
            }
        }

        return null;
    }



    /**
     * Search Stories associated with the specified Topic
     *
     * @param topicID ID of the specified Topic
     *
     * @return List of Stories associated with the Topic
     */
    @Transactional(readOnly = true)
    public List<Story> findStoriesByTopicID(@Positive long topicID) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return storyRepository.findByTopicID(topicID, allowedVisitorStates);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return storyRepository.findByTopicIDForJournalist(topicID, allowedJournalistStates, username);
            }
            case "[ROLE_CURATOR]" -> {
                return storyRepository.findByTopicID(topicID, allowedCuratorStates);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }



    /**
     * Submit the specified Story, set it's state to {@link StoryState#SUBMITTED SUBMITTED}
     * IF AND ONLY IF it's state had been {@link StoryState#CREATED CREATED} <br>
     *
     * Usable only by Journalist
     *
     * @param id ID of the specified Story
     */
    @PreAuthorize("hasAuthority('ROLE_JOURNALIST')")
    public void submitStory(@Positive long id) {

        Optional<Story> requestedStory = storyRepository.findById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (requestedStory.isPresent() && requestedStory.get().getAuthor().getUsername().equals(username)) {

            Story savedStory = requestedStory.get();

            if (savedStory.getState().equals(StoryState.CREATED)) {
                savedStory.setState(StoryState.SUBMITTED);
            }

            else {
                throw new RuntimeException("Story cannot be submitted since it's current state is: " +
                        savedStory.getState());
            }

        }
        else {
            throw new RuntimeException("Story with id: " + id + " was not found");
        }

    }



    /**
     * Reject the specified Story, set it's state to {@link StoryState#CREATED CREATED}
     * IF AND ONLY IF it's state had been {@link StoryState#SUBMITTED SUBMITTED} <br>
     *
     * Usable only by Curators
     *
     * @param id ID of the specified Story
     * @param rejectionReason Reason the specified Story was rejected
     */
    @PreAuthorize("hasAuthority('ROLE_CURATOR')")
    public void rejectStory(@Positive long id, @NotBlank  String rejectionReason) {

        Optional<Story> requestedStory = storyRepository.findById(id);

        if (requestedStory.isPresent()) {

            Story savedStory = requestedStory.get();

            if (savedStory.getState().equals(StoryState.SUBMITTED)) {

                savedStory.setState(StoryState.CREATED);
                savedStory.setRejectionReason(rejectionReason);

                storyRepository.save(savedStory);

            }

            else {
                throw new RuntimeException("Story cannot be rejected since it's current state is: " +
                        savedStory.getState());
            }

        }
        else {
            throw new RuntimeException("Story with id: " + id + " was not found");
        }

    }



    /**
     * Approve the specified Story, set it's state to {@link StoryState#APPROVED APPROVED}
     * IF AND ONLY IF it's state had been {@link StoryState#SUBMITTED SUBMITTED} <br>
     *
     * Usable only by Curators
     *
     * @param id ID of the specified Story
     */
    @PreAuthorize("hasAuthority('ROLE_CURATOR')")
    public void approveStory(@Positive long id) {

        Optional<Story> requestedStory = storyRepository.findById(id);

        if (requestedStory.isPresent()) {

            Story savedStory = requestedStory.get();

            if (savedStory.getState().equals(StoryState.SUBMITTED)) {

                savedStory.setState(StoryState.APPROVED);
                savedStory.removeRejectionReason();

                storyRepository.save(savedStory);

            }

            else {
                throw new RuntimeException("Story cannot be approved since it's current state is: " +
                        savedStory.getState());
            }

        }
        else {
            throw new RuntimeException("Story with id: " + id + " was not found");
        }

    }



    /**
     * Publish the specified Story, set it's state to {@link StoryState#PUBLISHED PUBLISHED}
     * IF AND ONLY IF it's state had been {@link StoryState#APPROVED APPROVED} <br>
     *
     * Usable only by Curators
     *
     * @param id ID of the specified Story
     */
    @PreAuthorize("hasAuthority('ROLE_CURATOR')")
    public void publishStory(@Positive long id) {

        Optional<Story> requestedStory = storyRepository.findById(id);

        if (requestedStory.isPresent()) {

            Story savedStory = requestedStory.get();

            if (savedStory.getState().equals(StoryState.APPROVED)) {

                savedStory.setState(StoryState.PUBLISHED);

                storyRepository.save(savedStory);

            }

            else {
                throw new RuntimeException("Story cannot be published since it's current state is: " +
                        savedStory.getState());
            }

        }
        else {
            throw new RuntimeException("Story with id: " + id + " was not found");
        }

    }



}
