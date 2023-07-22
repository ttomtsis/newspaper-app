package gr.aegean.icsd.newspaperapp.controller;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.representation.story.StoryModel;
import gr.aegean.icsd.newspaperapp.model.representation.story.StoryModelAssembler;
import gr.aegean.icsd.newspaperapp.model.service.StoryService;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Controller that handles requests related to the 'Story' resource. <br>
 * Maps all operations, except {@link #showATopicsStories(long, int, int) showATopicsStories}, at 'api/v0/stories' <br>
 *
 * @see #showATopicsStories(long, int, int)
 */
@RestController
@RequestMapping(produces = "application/json")
public class StoryController {

    private final StoryService service;
    private final StoryModelAssembler assembler;

    private static final Logger log = LoggerFactory.getLogger("StoryController");

    /**
     * The baseMapping string is used instead of the RequestMapping annotation
     * at the class level, <br> solely because of the special mapping required by
     * the showAllStoriesForAStory method.
     * @see #showATopicsStories(long, int, int)
     */
    private static final String baseMapping = "/api/v0/stories";

    /** Default size of the response page, <br> only applies to
     endpoints that include a page as their response */
    private static final String defaultPageSize = "10";



    /**
     * Sole constructor, never used implicitly <br>
     * Instantiates the StoryService, to forward requests to the service layer
     * and the StoryModelAssembler to create representations of the Story resource
     * @param storyService Service Implementation for the Topic entity
     * @param storyModelAssembler Representation Model Assembler, used to create
     *                              representations of the story resource, that
     *                              will be sent to the client
     */
    public StoryController(StoryService storyService, StoryModelAssembler storyModelAssembler) {
        this.service = storyService;
        this.assembler = storyModelAssembler;
    }



    /**
     * Creates a new Story entity in the database. <br>
     * Only user role 'JOURNALIST' may use this operation
     *
     * @param newStory The StoryModel object, representing the Story that will be saved in the database
     * @return a StoryModel representing the newly created resource.
     */
    @PostMapping(path = baseMapping, consumes = "application/json", produces = "application/json")
    public ResponseEntity<StoryModel> createStory(@RequestBody StoryModel newStory) {

        log.info("New 'create story' Request");

        Story savedStory = service.createStory(newStory.getName(), newStory.getContent(), newStory.getTopics());

        return new ResponseEntity<>(null, HttpStatus.CREATED);

    }



    /**
     * Update a Story.
     *
     * @param id the id of the Story entity that is going to be updated
     * @param updatedStory an updated version of the Story, that will replace the older one
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @PutMapping(path = baseMapping + "/{id}", consumes = "application/json")
    public ResponseEntity<Void> updateStory(@PathVariable long id, @RequestBody StoryModel updatedStory) {

        log.info("New 'update story' Request");

        service.updateStory(id, updatedStory.getName(), updatedStory.getContent(), updatedStory.getTopics());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }



    /**
     * Show all Stories, sorted by their state
     *
     * @param page Requested page
     * @param size Size of the requested page
     *
     * @return A PagedModel with the Stories
     */
    @GetMapping(value = baseMapping, produces = "application/json")
    public ResponseEntity<PagedModel<StoryModel>> showAllStories(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = defaultPageSize) int size) {

        log.info("New 'show all stories' Request");
        List<Story> storyList = service.findAllStories();

        for (Story s : storyList){
            log.error(s.getName() +" - " + s.getState() + " - " + s.getAuthor().getUsername());
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    /**
     * Show all Stories whose name is similar to the provided name
     *
     * @param name Provided name
     * @param page Requested page
     * @param size Size of the requested page
     *
     * @return A PagedModel with the Stories
     */
    @GetMapping(value = baseMapping, produces = "application/json", params = "name")
    public ResponseEntity<PagedModel<StoryModel>> showAllStoriesByName(@RequestParam String name,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = defaultPageSize) int size) {

        log.info("New 'show all stories filtered by name' Request");
        List<Story> storyList = service.findStoriesByName(name);

        for (Story s : storyList){
            log.error(s.getName() +" - " + s.getState() + " - " + s.getAuthor().getUsername());
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    /**
     * Show all Stories whose content is similar to the provided content
     *
     * @param content Provided content
     * @param page Requested page
     * @param size Size of the requested page
     *
     * @return A PagedModel with the Stories
     */
    @GetMapping(value = baseMapping, produces = "application/json", params = "content")
    public ResponseEntity<PagedModel<StoryModel>> showAllStoriesByContent(@RequestParam String content,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = defaultPageSize) int size) {

        log.info("New 'show all stories filtered by content' Request");
        List<Story> storyList = service.findStoriesByContent(content);

        for (Story s : storyList){
            log.error(s.getName() +" - " + s.getState() + " - " + s.getAuthor().getUsername());
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    /**
     * Show all Stories whose name and content is similar to the provided content
     *
     * @param name Provided name
     * @param content Provided content
     * @param page Requested page
     * @param size Size of the requested page
     *
     * @return A PagedModel with the Stories
     */
    @GetMapping(value = baseMapping, produces = "application/json", params = {"content","name"})
    public ResponseEntity<PagedModel<StoryModel>> showAllStoriesByNameAndContent(@RequestParam String name,
                                                                                 @RequestParam String content,
                                                                                 @RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = defaultPageSize) int size) {

        log.info("New 'show all stories filtered by content and name' Request");
        List<Story> storyList = service.findStoriesByContentAndName(name, content);

        for (Story s : storyList){
            log.error(s.getName() +" - " + s.getState() + " - " + s.getAuthor().getUsername());
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    /**
     * Show all Stories whose creation date is between the provided dates
     *
     * @param minDate Provided date, starting point
     * @param maxDate Provided date, ending point
     * @param page Requested page
     * @param size Size of the requested page
     *
     * @return A PagedModel with the Stories
     */
    @GetMapping(value = baseMapping, produces = "application/json", params = {"minDate","maxDate"})
    public ResponseEntity<PagedModel<StoryModel>> showAllStoriesByDate(@RequestParam Date minDate,
                                                                       @RequestParam Date maxDate,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = defaultPageSize) int size) {

        log.info("New 'show all stories filtered by a range of dates' Request");
        List<Story> storyList = service.findStoriesByDateRange(minDate, maxDate);

        for (Story s : storyList){
            log.error(s.getName() +" - " + s.getCreationDate() + " - " + s.getAuthor().getUsername() );
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    /**
     * Show all Stories whose state matches the provided state
     *
     * @param state Provided state
     * @param page Requested page
     * @param size Size of the page
     *
     * @return A PagedModel with the Stories
     */
    @GetMapping(value = baseMapping, produces = "application/json", params = {"state"})
    public ResponseEntity<PagedModel<StoryModel>> showAllStoriesByState(@RequestParam StoryState state,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = defaultPageSize) int size) {

        log.info("New 'show all stories filtered by state' Request");
        List<Story> storyList = service.findStoriesByState(state);

        for (Story s : storyList){
            log.error(s.getName() +" - " + s.getState() + " - " + s.getAuthor().getUsername() );
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    /**
     * Get all Stories associated with a Topic
     *
     * @param topicId ID of the Topic whose Stories will be extracted
     * @param page Number of the page the client has requested
     * @param size Size of the requested page
     * @return a PagedModel containing the Story representations as well as the links to navigate it
     */
    @GetMapping(path = "api/v0/topics/{topicId}/stories", produces = "application/json")
    public ResponseEntity<PagedModel<StoryModel>> showATopicsStories(@PathVariable long topicId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = defaultPageSize) int size) {
        log.info("New 'show a topic's stories' Request");
        List<Story> storyList = service.findStoriesByTopicID(topicId);

        for (Story s : storyList){
            log.error(s.getName() +" - " + s.getState() + " - " + s.getAuthor().getUsername() );
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }



    /**
     * Submit the specified Story <br>
     *
     * @param id The id of the Story whose state will be updated
     * @param state The new state of the Story
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    // Also check GitHub Issue #10 : Implement PATCH method properly
    // https://github.com/ttomtsis/newspaper-app/issues/10
    @PatchMapping(value = baseMapping + "/{id}", params = "state=SUBMITTED")
    public ResponseEntity<Void> submitStory(@PathVariable long id, @RequestParam StoryState state) {

        log.info("New 'submit story' Request");

        service.submitStory(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    /**
     * Reject the specified Story <br>
     *
     * @param id The id of the Story whose state will be updated
     * @param state The new state of the Story
     * @param rejectionReason Required when a Curator rejects the Story
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    // Also check GitHub Issue #10 : Implement PATCH method properly
    // https://github.com/ttomtsis/newspaper-app/issues/10
    @PatchMapping(value = baseMapping + "/{id}", params = {"state=CREATED"})
    public ResponseEntity<Void> rejectStory(@PathVariable long id, @RequestParam StoryState state,
                                            @RequestBody String rejectionReason) {

        log.info("New 'reject story' Request");

        service.rejectStory(id, rejectionReason);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    /**
     * Approves the specified Story <br>
     *
     * @param id The id of the Story whose state will be updated
     * @param state The new state of the Story
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    // Also check GitHub Issue #10 : Implement PATCH method properly
    // https://github.com/ttomtsis/newspaper-app/issues/10
    @PatchMapping(value = baseMapping + "/{id}", params = "state=APPROVED")
    public ResponseEntity<Void> approveStory(@PathVariable long id, @RequestParam StoryState state) {

        log.info("New 'approve story' Request");

        service.approveStory(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    /**
     * Publish the specified Story <br>
     *
     * @param id The id of the Story whose state will be updated
     * @param state The new state of the Story
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    // Also check GitHub Issue #10 : Implement PATCH method properly
    // https://github.com/ttomtsis/newspaper-app/issues/10
    @PatchMapping(value = baseMapping + "/{id}", params = "state=PUBLISHED")
    public ResponseEntity<Void> publishStory(@PathVariable long id, @RequestParam StoryState state) {

        log.info("New 'publish story' Request");

        service.publishStory(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
