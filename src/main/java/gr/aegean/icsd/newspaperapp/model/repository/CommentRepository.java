package gr.aegean.icsd.newspaperapp.model.repository;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // UPDATE table_name
    //SET column1 = value1, column2 = value2, ...
    //WHERE condition;
    @Modifying
    @Query("update comments set content = ?1 where id = ?2")
    void updateCommentById(String firstname, long id);
}
