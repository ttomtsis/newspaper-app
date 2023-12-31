package gr.aegean.icsd.newspaperapp.controller;

import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.representation.topic.TopicModel;
import gr.aegean.icsd.newspaperapp.model.representation.topic.TopicModelAssembler;
import gr.aegean.icsd.newspaperapp.model.service.TopicService;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controller that handles requests related to the 'Topic' resource. <br>
 * Maps all operations at 'api/v0/topics' <br>
 */
@RestController
@RequestMapping(value = "api/v0/topics")
public class TopicController {
    private final TopicService service;
    private final TopicModelAssembler assembler;

    /** Default size of the response page, <br> only applies to
     endpoints that include a page as their response */
    private static final String defaultPageSize = "10";
    private static final Logger log = LoggerFactory.getLogger("TopicController");


    /**
     * Sole constructor, never used implicitly <br>
     * Instantiates the TopicService, to forward requests to the service layer
     * and the TopicModelAssembler to create representations of the Topic resource
     * @param topicService Service Implementation for the Topic entity
     * @param topicModelAssembler Representation Model Assembler, used to create
     *                              representations of the Topic resource, that
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
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<TopicModel> createTopic(@RequestBody TopicModel newTopic) {

        log.info("New 'create topic' Request");

        Topic savedTopic = service.createTopic(newTopic.getName(), newTopic.getParentTopicID());
        TopicModel savedTopicModel = assembler.toModel(savedTopic);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{id}")
                .buildAndExpand(savedTopic.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedTopicModel);
    }



    /**
     * Updates a specific topic <br>
     *
     * @param id The id of the topic that will be updated
     * @param updatedTopic The topic containing the updated values
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<Void> updateTopic(@PathVariable long id, @RequestBody TopicModel updatedTopic) {

        log.info("New 'update topic' Request");

        service.updateTopic(id, updatedTopic.getName(), updatedTopic.getParentTopicID());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    /**
     * Display a specific topic <br>
     *
     * @param id The id of the topic
     * @return a TopicModel representation of the requested topic
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<TopicModel> showTopic(@PathVariable long id) {

        log.info("New 'show topic' Request");
        Topic requestedTopic = service.showTopic(id);

        TopicModel topicModel = assembler.toModel(requestedTopic);

        return new ResponseEntity<>(topicModel, HttpStatus.OK);
    }



    /**
     * Display all topics saved in the database
     *
     * @return a PagedModel containing all the TopicModels of the topics
     * as well as links to navigate the PagedModel
     */
    @GetMapping
    public ResponseEntity<PagedModel<TopicModel>> showAllTopics(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = defaultPageSize) int size) {

        log.info("New 'show all topics' Request");
        Pageable pageable = PageRequest.of(page, size);
        Page<Topic> topicsPage = service.showAllTopics(pageable);

        PagedModel<TopicModel> pagedTopicModel = assembler.createPagedModel(topicsPage);

        return new ResponseEntity<>(pagedTopicModel, HttpStatus.OK);
    }



    /**
     * Display all topics matching the provided name.
     *
     * @param name The specified Topic name
     * @return a PagedModel containing all the TopicModels of the topics
     * as well as links to navigate the PagedModel
     */
    @GetMapping(params = "name")
    public ResponseEntity<PagedModel<TopicModel>> showAllTopicsByName(@RequestParam String name,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = defaultPageSize) int size) {

        log.info("New 'show all topics matching name' Request");
        Pageable pageable = PageRequest.of(page, size);
        Page<Topic> topicsPage = service.searchTopicByName(name, pageable);

        PagedModel<TopicModel> pagedTopicModel = assembler.createPagedModelForSearchByName(topicsPage, name);


        return new ResponseEntity<>(pagedTopicModel, HttpStatus.OK);
    }



    /**
     * Update the state of a Topic to {@link TopicState#APPROVED APPROVED} <br>
     * Since Topics can only exist in two states ( SUBMITTED and APPROVED ),
     * and SUBMITTED is the initial state of every Topic, the only possible
     * state change is from SUBMITTED to APPROVED hence the client request
     * requires an empty PATCH document. <br>
     *
     * All valid Topic states are defined in the {@link TopicState} enum
     * @see Topic
     * @see TopicState
     *
     * @param id The id of the Topic whose state will be updated
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> approveTopic(@PathVariable long id) {

        log.info("New 'approve topic state' Request");

        service.approveTopic(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    /**
     * Delete a Topic. ( When a Topic is rejected it is automatically deleted )
     * @param id ID of the Topic that will be deleted
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> rejectTopic(@PathVariable long id) {

        log.info("New 'reject topic' Request");

        service.rejectTopic(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
