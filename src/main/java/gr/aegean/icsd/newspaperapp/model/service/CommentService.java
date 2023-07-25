package gr.aegean.icsd.newspaperapp.model.service;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.model.repository.CommentRepository;
import gr.aegean.icsd.newspaperapp.model.repository.StoryRepository;
import gr.aegean.icsd.newspaperapp.security.UserUtils;
import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
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
 * the Comment entity
 */
@Service
@Transactional
@Validated
public class CommentService {

    private final CommentRepository commentRepository;
    private final StoryRepository storyRepository;

    // Allowed Comment states per User, a User cannot access a
    // Comment whose state is not in this List.
    // ( Except the Journalist, in case he owns the Comment )
    private final Set<CommentState> allowedCuratorStates;
    private final Set <CommentState> allowedJournalistStates;
    private final Set <CommentState> allowedVisitorStates;


    public CommentService(CommentRepository commentRepository, StoryRepository storyRepository) {
        this.commentRepository = commentRepository;
        this.storyRepository = storyRepository;

        allowedCuratorStates = new HashSet<>();
        allowedCuratorStates.add(CommentState.SUBMITTED);
        allowedCuratorStates.add(CommentState.APPROVED);

        allowedVisitorStates = new HashSet<>();
        allowedVisitorStates.add(CommentState.APPROVED);

        allowedJournalistStates = new HashSet<>();
        allowedJournalistStates.add(CommentState.APPROVED);
    }



    /**
     * Creates a new Comment entity and persists it in the database
     *
     * @param storyID ID of the Story associated with the Comment
     * @param content Content of the new Comment
     */
    public Comment createComment(@NotNull @Positive Integer storyID, @NotBlank String content) {

        Optional<Story> parentStory = storyRepository.findById(Long.valueOf(storyID));
        String authorID = SecurityContextHolder.getContext().getAuthentication().getName();

        if (parentStory.isEmpty() || !parentStory.get().getState().equals(StoryState.PUBLISHED)) {
            throw new RuntimeException("Parent Story was not found");
        }

        Comment newComment;

        if (authorID.equals("anonymousUser")) {
            newComment = new Comment(parentStory.get(), content);
        }
        else {
            User author = new User(authorID);
            newComment = new Comment(parentStory.get(), content, author);
        }

        commentRepository.save(newComment);
        return newComment;

    }



    /**
     * Updates the content of an existing Comment
     *
     * @param id ID of the Comment ot be updated
     * @param content New content of the Comment
     */
    @PreAuthorize("hasRole('ROLE_CURATOR')")
    public void updateComment(@Positive long id, @NotBlank String content) {

        Optional<Comment> requestedComment = commentRepository.findById(id);

        if (requestedComment.isEmpty()) {throw new RuntimeException("Comment was not found");}

        Comment savedComment = requestedComment.get();
        savedComment.setContent(content);

        commentRepository.save(savedComment);
    }



    /**
     * Approves the specified Comment, and set it's state equal to {@link CommentState#APPROVED APPROVED}
     * IF AND ONLY IF the Comment's state has been {@link CommentState#SUBMITTED SUBMITTED}.
     *
     * @param id ID of the Comment that will be approved
     */
    @PreAuthorize("hasRole('ROLE_CURATOR')")
    public void approveComment(@Positive long id) {

        Optional<Comment> requestedComment = commentRepository.findById(id);

        if (requestedComment.isEmpty()) {throw new RuntimeException("Comment was not found");}

        Comment savedComment = requestedComment.get();
        savedComment.setState(CommentState.APPROVED);

        commentRepository.save(savedComment);
    }



    /**
     * Reject the specified Comment, and delete it from the database,
     * IF AND ONLY IF the Comment's state has been {@link CommentState#SUBMITTED SUBMITTED}.
     *
     * @param id ID of the Comment that will be deleted
     */
    @PreAuthorize("hasRole('ROLE_CURATOR')")
    public void rejectComment(@Positive long id) {

        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }
        else {
            throw new RuntimeException("Requested comment was not found");
        }

    }



    /**
     * Show all Comments associated with a Story
     *
     * @param storyId  ID of the requested Story
     * @param pageable Details of the requested Page
     *
     * @return A list of all Comments associated with that Story
     */
    @Transactional(readOnly = true)
    public Page<Comment> showCommentsByStory(@Positive long storyId, @NotNull Pageable pageable) {

        if (UserUtils.isVisitor()) {
            return commentRepository.findByStoryID(storyId, allowedVisitorStates, pageable);
        }
        else if (UserUtils.isJournalist()) {
            String username = UserUtils.getUsername();
            return commentRepository.findByStoryIDForJournalist(storyId, allowedJournalistStates, username, pageable);
        }
        else if (UserUtils.isCurator()) {
            return commentRepository.findByStoryID(storyId, allowedCuratorStates, pageable);
        }

        throw new AccessDeniedException("User with role: " + UserUtils.getUsername()
                + " is not supported by this operation");

    }



}
