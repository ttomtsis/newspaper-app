package gr.aegean.icsd.newspaperapp.controller;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.representation.comment.CommentModel;
import gr.aegean.icsd.newspaperapp.model.representation.comment.CommentModelAssembler;
import gr.aegean.icsd.newspaperapp.model.service.CommentService;
import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import gr.aegean.icsd.newspaperapp.util.enums.SortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller that handles requests related to the 'Comment' resource. <br>
 * Maps all operations, except showAllCommentsForAStory, at 'api/v0/comments' <br>
 * showAllCommentsForAStory is exposed at '/api/v0/stories/{id}/comments'
 * @see #showAllCommentsForAStory(UUID, int, int, SortType)
 */
@RestController
public class CommentController {
    private final CommentService service;
    private final CommentModelAssembler assembler;

    private static final Logger log = LoggerFactory.getLogger("CommentController");

    /* Using the baseMapping string instead of the RequestMapping annotation
    * at the class level, solely because of the special mapping required by
    * the showAllCommentsForAStory method */
    private final String baseMapping = "/api/v0/comments";

    /**
     * Sole constructor, never used implicitly <br>
     * Instantiates the CommentService, to forward requests to the service layer
     * and the CommentModelAssembler to create representations of the Comment resource
     * @param commentService Service Implementation for the Comment entity
     * @param commentModelAssembler Representation Model Assembler, used to create
     *                              the representations of the Comment resource, that
     *                              will be sent to the client
     */
    public CommentController(CommentService commentService, CommentModelAssembler commentModelAssembler) {
        this.service = commentService;
        this.assembler = commentModelAssembler;
    }

    /**
     * Creates a new Comment entity in the database. <br>
     * Only user roles 'JOURNALIST' and 'CURATOR' may use this operation
     *
     * @param newComment The comment object to be saved in the database
     * @return a CommentModel representing the newly created resource.
     */
    @PostMapping(baseMapping)
    public ResponseEntity<CommentModel> createComment(@RequestBody Comment newComment) {
        return null;
    }

    /**
     * Updates the 'content' field of a specific Comment.
     *
     * @param id the id of the Comment entity that is going to be updated
     * @param updatedComment a Comment, containing the updated Content, that will replace the older one
     * @return a CommentModel representing the new version updated resource
     */
    @PutMapping(baseMapping + "/{id}")
    public ResponseEntity<CommentModel> updateComment(@PathVariable UUID id, @RequestBody Comment updatedComment) {
        return null;
    }

    /**
     * Get all the comments associated with a story <br>
     * Because the parent resource of the comment Entity is a story,
     * this Endpoint is mapped at 'api/v0/stories/{storyId}/comments'
     *
     * @param storyId the id of the Story entity that contains the comments
     * @param page number of the page the client has requested
     * @param size size of the requested page
     * @param sortType type of sorting that the comments will have.
     *                 Only Sorted according to creation date
     * @return a PagedModel containing the Comment representations and the links
     * to navigate it
     */
    @GetMapping("api/v0/stories/{storyId}/comments")
    public ResponseEntity<PagedModel<CommentModel>> showAllCommentsForAStory(@PathVariable UUID storyId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @RequestParam(defaultValue = "ASC") SortType sortType ) {
        return null;
    }

    /**
     * Updates the 'content' field of a specific Comment.
     *
     * @param id the id of the Comment entity that is going to be updated
     * @param newState the new state that will replace the older one
     * @return a CommentModel representing the new version updated resource
     */
    @PatchMapping(baseMapping + "/{id}")
    public ResponseEntity<CommentModel> updateCommentState(@PathVariable UUID id, @RequestParam CommentState newState) {
        return null;
    }
}
