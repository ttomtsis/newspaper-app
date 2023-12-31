package gr.aegean.icsd.newspaperapp.model.repository;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


    // -- Find Comments By Story ID -- //
    @Query("SELECT comments FROM Story story " +
            "JOIN story.commentsList comments " +
            "WHERE story.id = :id " +
            "AND comments.state IN :state " +
            "ORDER BY comments.creationDate DESC")
    Page<Comment> findByStoryID(@Param("id") Long id, @Param("state") Set<CommentState> state, Pageable pageable);


    @Query("SELECT comments FROM Story story " +
            "JOIN story.commentsList comments " +
            "WHERE story.id = :id " +
            "AND (comments.state IN :state OR comments.authorID.username = :author) " +
            "ORDER BY comments.creationDate DESC")
    Page<Comment> findByStoryIDForJournalist(@Param("id") Long id, @Param("state") Set<CommentState> state,
                                             @Param("author") String author, Pageable pageable);

}
