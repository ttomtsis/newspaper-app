package gr.aegean.icsd.newspaperapp.model.repository;

import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {


    Optional<Topic> findByIdAndStateIn(Long topicID, Set<TopicState> state);

    @Query("SELECT topic FROM Topic topic " +
            "WHERE topic.id = :id " +
            "AND (topic.authorID.username = :author OR topic.state IN :state)")
    Optional<Topic> findTopicByIDForJournalist(@Param("id") Long id,
                                               @Param("state") Set<TopicState> state,
                                               @Param("author") String authorID);



    // -- Find All Topics -- //
    @Query("SELECT topic FROM Topic topic " +
            "WHERE topic.state IN :state " +
            "ORDER BY CASE topic.state " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.TopicState.SUBMITTED THEN 1 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.TopicState.APPROVED THEN 2 " +
            "END DESC")
    Page<Topic> findAllTopics(@Param("state") Set<TopicState> state,
                              Pageable pageable);

    @Query("SELECT topic FROM Topic topic " +
            "WHERE topic.authorID.username = :author OR topic.state IN :state " +
            "ORDER BY CASE topic.state " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.TopicState.SUBMITTED THEN 1 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.TopicState.APPROVED THEN 2 " +
            "END DESC ")
    Page<Topic> findAllTopicsForJournalist(@Param("state") Set<TopicState> state,
                                           @Param("author") String authorID,
                                           Pageable pageable);



    // -- Find Topic By Name -- //
    Page<Topic> findByNameContainingIgnoreCaseAndStateIn(String name,
                                                         Set<TopicState> state,
                                                         Pageable pageable);

    @Query("SELECT topic FROM Topic topic " +
            "WHERE topic.name LIKE CONCAT('%', :name, '%') " +
            "AND (topic.authorID.username = :author OR topic.state IN :state)")
    Page<Topic> findTopicsByNameForJournalist(@Param("name") String name,
                                             @Param("state") Set<TopicState> state,
                                             @Param("author") String authorID,
                                             Pageable pageable);


}

