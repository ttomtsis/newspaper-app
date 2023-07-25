package gr.aegean.icsd.newspaperapp.controller;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.representation.comment.CommentModel;
import gr.aegean.icsd.newspaperapp.model.representation.comment.CommentModelAssembler;
import gr.aegean.icsd.newspaperapp.model.service.CommentService;
import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * Controller that handles requests related to the 'Comment' resource. <br>
 * Maps all operations, except {@link #showAllCommentsForAStory(long, int, int) showAllCommentsForAStory}, at 'api/v0/comments' <br>
 * @see #showAllCommentsForAStory(long, int, int)
 */
@RestController
public class CommentController {
    private final CommentService service;
    private final CommentModelAssembler assembler;

    private static final Logger log = LoggerFactory.getLogger("CommentController");

    /**
    * The baseMapping string is used instead of the RequestMapping annotation
    * at the class level, <br> solely because of the special mapping required by
    * the showAllCommentsForAStory method.
    * @see #showAllCommentsForAStory(long, int, int)
    */
    private static final String baseMapping = "/api/v0/comments";

    /** Default size of the response page, <br> only applies to
     endpoints that include a page as their response */
    private static final String defaultPageSize = "10";


    /**
     * Sole constructor, never used implicitly <br>
     * Instantiates the CommentService, to forward requests to the service layer
     * and the CommentModelAssembler to create representations of the Comment resource
     * @param commentService Service Implementation for the Comment entity
     * @param commentModelAssembler Representation Model Assembler, used to create
     *                              representations of the Comment resource, that
     *                              will be sent to the client
     */
    public CommentController(CommentService commentService, CommentModelAssembler commentModelAssembler) {
        this.service = commentService;
        this.assembler = commentModelAssembler;
    }



    /**
     * Creates a new Comment entity in the database. <br>
     *
     * @param newComment The comment object to be saved in the database
     * @return a CommentModel representing the newly created resource.
     */
    @PostMapping(path = baseMapping, consumes = "application/json", produces = "application/json")
    public ResponseEntity<CommentModel> createComment(@RequestBody CommentModel newComment) {

        log.info("New 'create comment' Request");

        Comment savedComment = service.createComment(newComment.getStoryID(), newComment.getContent());
        CommentModel savedCommentModel = assembler.toModel(savedComment);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/v0/stories/{storyId}/comments")
                .buildAndExpand(savedComment.getStory().getId())
                .toUri();

        return ResponseEntity.created(location).body(savedCommentModel);

    }



    /**
     * Updates the 'content' field of a specific Comment.
     *
     * @param id the id of the Comment entity that is going to be updated
     * @param updatedComment a Comment, containing the updated Content, that will replace the older one
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @PutMapping(path = baseMapping + "/{id}", consumes = "application/json")
    public ResponseEntity<Void> updateComment(@PathVariable long id, @RequestBody CommentModel updatedComment) {

        log.info("New 'update comment' Request");

        service.updateComment(id, updatedComment.getContent());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    /**
     * Get all the comments associated with a story <br>
     * Because the parent resource of the comment Entity is a story,
     * this Endpoint is mapped at 'api/v0/stories/{storyId}/comments'
     *
     * @param storyId ID of the Story entity that contains the comments
     * @param page Number of the page the client has requested
     * @param size Size of the requested page
     *
     * @return a PagedModel containing the Comment representations, sorted by their creation date in
     * ascending order, and the links to navigate it
     */
    @GetMapping(path = "api/v0/stories/{storyId}/comments", produces = "application/json")
    public ResponseEntity<PagedModel<CommentModel>> showAllCommentsForAStory(@PathVariable long storyId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = defaultPageSize) int size) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        log.info("New 'show all comments for a story' Request");
        Pageable pageable = PageRequest.of(page, size);

        Page<Comment> commentList = service.showCommentsByStory(storyId, pageable);

        Method thisMethod = CommentController.class.getMethod("showAllCommentsForAStory",
                long.class,
                int.class,
                int.class);

        PagedModel<CommentModel> commentPagedModel = assembler.GenerifiedCreatePagedModel(commentList, thisMethod);

        return new ResponseEntity<>(commentPagedModel, HttpStatus.OK);
    }



    /**
     * Update the state of a Comment to {@link CommentState#APPROVED APPROVED} <br>
     * Since Comments can only exist in two states ( SUBMITTED and APPROVED ),
     * and SUBMITTED is the initial state of every Comment, the only possible
     * state change is from SUBMITTED to APPROVED hence the client request
     * requires an empty PATCH document. <br>
     *
     * All valid comment states are defined in the {@link CommentState} enum
     * @see Comment
     * @see CommentState
     *
     * @param id The id of the comment whose state will be updated
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @PatchMapping(baseMapping + "/{id}")
    public ResponseEntity<Void> approveComment(@PathVariable long id) {

        log.info("New 'approve comment' Request");

        service.approveComment(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    /**
     * Delete a Comment. ( When a Comment is rejected it is automatically deleted )
     * @param id ID of the Comment that will be deleted
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @DeleteMapping(baseMapping + "/{id}")
    public ResponseEntity<Void> rejectComment(@PathVariable long id) {

        log.info("New 'delete comment' Request");

        service.rejectComment(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
