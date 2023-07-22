package gr.aegean.icsd.newspaperapp.model.service;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.model.repository.CommentRepository;
import gr.aegean.icsd.newspaperapp.model.repository.StoryRepository;
import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final StoryRepository storyRepository;

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



    public void createComment(@NotNull @Positive Integer storyID, @NotBlank String content) {

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

    }



    @PreAuthorize("hasRole('ROLE_CURATOR')")
    public void updateComment(long id, @NotBlank String content) {

        Optional<Comment> requestedComment = commentRepository.findById(id);

        if (requestedComment.isEmpty()) {throw new RuntimeException("Comment was not found");}

        Comment savedComment = requestedComment.get();
        savedComment.setContent(content);

        commentRepository.save(savedComment);
    }



    @PreAuthorize("hasRole('ROLE_CURATOR')")
    public void approveComment(long id) {

        Optional<Comment> requestedComment = commentRepository.findById(id);

        if (requestedComment.isEmpty()) {throw new RuntimeException("Comment was not found");}

        Comment savedComment = requestedComment.get();
        savedComment.setState(CommentState.APPROVED);

        commentRepository.save(savedComment);
    }



    @PreAuthorize("hasRole('ROLE_CURATOR')")
    public void rejectComment(long id) {

        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        }
        else {
            throw new RuntimeException("Requested comment was not found");
        }

    }

    @Transactional(readOnly = true)
    public List<Comment> showCommentsByStory(long storyId) {

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        switch (userRole) {
            case "[ROLE_ANONYMOUS]" -> {
                return commentRepository.findByStoryID(storyId, allowedVisitorStates);
            }
            case "[ROLE_JOURNALIST]" -> {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return commentRepository.findByStoryIDForJournalist(storyId, allowedJournalistStates, username);
            }
            case "[ROLE_CURATOR]" -> {
                return commentRepository.findByStoryID(storyId, allowedCuratorStates);
            }
        }

        throw new AccessDeniedException("User with role: " + userRole + " is not supported by this operation");

    }

}
