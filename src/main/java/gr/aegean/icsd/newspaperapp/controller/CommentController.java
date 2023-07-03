package gr.aegean.icsd.newspaperapp.controller;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.representation.comment.CommentModel;
import gr.aegean.icsd.newspaperapp.model.representation.comment.CommentModelAssembler;
import gr.aegean.icsd.newspaperapp.model.service.CommentService;
import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import gr.aegean.icsd.newspaperapp.util.enums.SortType;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles requests related to the 'Comment' resource. <br>
 * Maps all operations, except showAllCommentsForAStory, at 'api/v0/comments' <br>
 * showAllCommentsForAStory is exposed at '/api/v0/stories/{id}/comments'
 * @see #showAllCommentsForAStory(long, int, int, SortType)
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
    * @see #showAllCommentsForAStory(long, int, int, SortType)
    */
    private final String baseMapping = "/api/v0/comments";

    /** Default size of the response page, <br> only applies to
     endpoints that include a page as their response */
    private final String defaultPageSize = "10";

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
    @PostMapping(baseMapping)
    public ResponseEntity<CommentModel> createComment(@RequestBody Comment newComment) {
        log.info("New 'create comment' Request");
        return null;
    }

    /**
     * Updates the 'content' field of a specific Comment.
     *
     * @param id the id of the Comment entity that is going to be updated
     * @param updatedComment a Comment, containing the updated Content, that will replace the older one
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @PutMapping(baseMapping + "/{id}")
    public ResponseEntity<Void> updateComment(@PathVariable long id, @RequestBody Comment updatedComment) {
        log.info("New 'update comment' Request");
        return null;
    }

    /**
     * Get all the comments associated with a story <br>
     * Because the parent resource of the comment Entity is a story,
     * this Endpoint is mapped at 'api/v0/stories/{storyId}/comments'
     *
     * @param storyId ID of the Story entity that contains the comments
     * @param page Number of the page the client has requested
     * @param size Size of the requested page
     * @param sortType Type of sorting that the comments will have.
     *                 Only Sorted according to creation date
     * @return a PagedModel containing the Comment representations, sorted by their creation date in
     * ascending order, and the links to navigate it
     */
    @GetMapping("api/v0/stories/{storyId}/comments")
    public ResponseEntity<PagedModel<CommentModel>> showAllCommentsForAStory(@PathVariable long storyId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = defaultPageSize) int size,
                                                                             @RequestParam(defaultValue = "ASC") SortType sortType ) {
        log.info("New 'show all comments for a story' Request");
        return null;
    }

    /**
     * Update the state of a comment <br>
     * Valid comment states are defined in the {@link CommentState} enum
     * @see gr.aegean.icsd.newspaperapp.util.enums.CommentState
     *
     * @param id The id of the comment whose state will be updated
     * @param state The new state of the comment
     * @return {@link org.springframework.http.HttpStatus#NO_CONTENT 204 Status Code}
     */
    @PatchMapping(baseMapping + "/{id}")
    public ResponseEntity<Void> updateCommentState(@PathVariable long id, @RequestBody CommentState state) {
        log.info("New 'update comment's state' Request");
        return null;
    }
}
