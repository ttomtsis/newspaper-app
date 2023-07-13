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
     * Only user roles 'JOURNALIST' and 'CURATOR' may use this operation
     *
     * @param newStory The comment object to be saved in the database
     * @return a StoryModel representing the newly created resource.
     */
    @PostMapping(path = baseMapping, consumes = "application/json", produces = "application/json")
    public ResponseEntity<StoryModel> createStory(@RequestBody Story newStory) {
        log.info("New 'create story' Request");
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
    public ResponseEntity<Void> updateStory(@PathVariable long id, @RequestBody Story updatedStory) {
        log.info("New 'update story' Request");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete a Story.
     *
     * @param id the id of the Story entity that is going to be deleted
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @DeleteMapping(path = baseMapping + "/{id}")
    public ResponseEntity<Void> deleteStory(@PathVariable long id) {
        log.info("New 'delete story' Request");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Shows all Stories saved in the database, can optionally use one of the filters listed below: <br>
     * 1) Filter by name and/or content <br>
     * 2) Filter by state and/or a date range <br>
     * @param name Will filter stories according to this name
     * @param content Will filter stories according to this content
     * @param state Will filter stories according to this state
     * @param minDate Will filter stories according to this range of dates, this is the earlier date
     * @param maxDate Will filter stories according to this range of dates, this is the later date
     * @param page Number of the page the client has requested
     * @param size Size of the requested page
     * @return PagedModel containing the Story representations as well as the links to navigate it
     */
    // Also check GitHub Issue #9 : Rewrite Story Controller method: showAllStoriesFiltered
    // https://github.com/ttomtsis/newspaper-app/issues/9
    @GetMapping(value = baseMapping, produces = "application/json")
    public ResponseEntity<PagedModel<StoryModel>> showAllStoriesFiltered(@RequestParam(required = false) String name,
                                                                         @RequestParam(required = false) String content,
                                                                         @RequestParam(required = false) Date minDate,
                                                                         @RequestParam(required = false) Date maxDate,
                                                                         @RequestParam(required = false) StoryState state,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = defaultPageSize) int size){
        if (name != null || content != null) {
            log.info("New 'show all stories filtered by name and/or content' Request");
        }
        else if (state != null || minDate != null && maxDate != null) {
            log.info("New 'show all stories filtered by state and/or date range' Request");
        }
        else if (state == null && name == null && content == null && minDate == null && maxDate == null) {
            log.info("New 'show all stories' Request");
        }
        else {
            log.error("Malformed Request in showAllStoriesFiltered");
            throw new RuntimeException("Malformed request");
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
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    /**
     * Update the state of a Story <br>
     * Valid Story states are defined in the {@link StoryState} enum
     * @see gr.aegean.icsd.newspaperapp.util.enums.StoryState
     *
     * @param id The id of the Story whose state will be updated
     * @param state The new state of the Story
     * @param rejectionReason Required when a Curator rejects the Story
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    // Also check GitHub Issue #10 : Implement PATCH method properly
    // https://github.com/ttomtsis/newspaper-app/issues/10
    @PatchMapping(value = baseMapping + "/{id}")
    public ResponseEntity<Void> updateStoryState(@PathVariable long id, @RequestParam StoryState state,
                                                 @RequestBody(required = false) String rejectionReason) {
        log.info("New 'update a story's state' Request");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
