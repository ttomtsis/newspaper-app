package gr.aegean.icsd.newspaperapp.controller;

import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.representation.topic.TopicModel;
import gr.aegean.icsd.newspaperapp.model.representation.topic.TopicModelAssembler;
import gr.aegean.icsd.newspaperapp.model.service.TopicService;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles requests related to the 'Topic' resource. <br>
 * Maps all operations, except showAllCommentsForAStory, at 'api/v0/topics' <br>
 */
@RestController
@RequestMapping(value = "api/v0/topics")
public class TopicController {
    private final TopicService service;
    private final TopicModelAssembler assembler;

    /** Default size of the response page, <br> only applies to
     endpoints that include a page as their response */
    private final String defaultPageSize = "10";
    private static final Logger log = LoggerFactory.getLogger("TopicController");

    /**
     * Sole constructor, never used implicitly <br>
     * Instantiates the TopicService, to forward requests to the service layer
     * and the TopicModelAssembler to create representations of the Topic resource
     * @param topicService Service Implementation for the Topic entity
     * @param topicModelAssembler Representation Model Assembler, used to create
     *                              the representations of the Topic resource, that
     *                              will be sent to the client
     */
    public TopicController(TopicService topicService, TopicModelAssembler topicModelAssembler) {
        this.service = topicService;
        this.assembler = topicModelAssembler;
    }

    /**
     * Creates a new Topic entity in the database. <br>
     * Only user roles 'JOURNALIST' and 'CURATOR' may use this operation
     *
     * @param newTopic The comment object to be saved in the database
     * @return a TopicModel representing the newly created resource.
     */
    @PostMapping
    public ResponseEntity<TopicModel> createTopic(@RequestBody Topic newTopic) {
        log.info("New 'create topic' Request");
        return null;
    }

    /**
     * Searches for topics whose names are similar to the topic of the name provided <br>
     * This method is different from {@link #showTopic} since it returns all the topics
     * whose name is similar to the one provided
     * @see #showTopic(long)
     *
     * @param name The name of the provided topic
     * @return a PagedModel containing all the TopicModels of the topics matching the
     * provided name as well as links to navigate the PagedModel
     */
    @GetMapping("/search")
    public ResponseEntity<PagedModel<TopicModel>> searchTopicByName(@RequestParam String name,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = defaultPageSize) int size) {
        log.info("New 'search topic by name' Request");
        return null;
    }

    /**
     * Updates a specific topic <br>
     *
     * @param id The id of the topic that will be updated
     * @param updatedTopic The topic containing the updated values
     * @return a TopicModel representation of the topic that has been updated
     */
    @PutMapping("/{id}")
    public ResponseEntity<TopicModel> updateTopic(@PathVariable long id, @RequestBody Topic updatedTopic) {
        log.info("New 'update topic' Request");
        return null;
    }

    /**
     * Display a specific topic <br>
     *
     * @param id The id of the topic
     * @return a TopicModel representation of the requested topic
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicModel> showTopic(@PathVariable long id) {
        log.info("New 'show topic' Request");
        return null;
    }

    /**
     * Display all topics <br>
     *
     * @return a PagedModel containing all the TopicModels of the topics
     * as well as links to navigate the PagedModel
     */
    @GetMapping
    public ResponseEntity<PagedModel<TopicModel>> showAllTopics(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = defaultPageSize) int size) {
        log.info("New 'show all topics' Request");
        return null;
    }

    /**
     * Update the state of a topic <br>
     * Valid topic states are defined in the {@link TopicState} enum
     * @see gr.aegean.icsd.newspaperapp.util.enums.TopicState
     *
     * @param id The id of the topic whose state will be updated
     * @param state The new state of the topic
     * @return A TopicModel representation of the updated topic
     */
    @PatchMapping("/{id}")
    public ResponseEntity<TopicModel> updateTopicState(@PathVariable long id, @RequestBody TopicState state) {
        log.info("New 'update topic state' Request");
        return null;
    }
}
