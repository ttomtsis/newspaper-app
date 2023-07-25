package gr.aegean.icsd.newspaperapp.model.repository;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Set;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {


    // -- Find Stories By Name -- //
    Page<Story> findByNameContainingIgnoreCaseAndStateIn(String name,
                                                         Set<StoryState> state,
                                                         Pageable pageable);

    @Query("SELECT s FROM Story s " +
            "WHERE s.name LIKE CONCAT('%', :name, '%') " +
            "AND (s.authorID.username = :author OR s.state IN :state)")
    Page<Story> findByNameForJournalist(@Param("name") String name,
                                       @Param("state") Set<StoryState> state,
                                       @Param("author") String authorID,
                                       Pageable pageable);


    // -- Find Stories By Content -- //
    Page<Story> findByContentContainingIgnoreCaseAndStateIn(String content,
                                                            Set<StoryState> state,
                                                            Pageable pageable);

    @Query("SELECT s FROM Story s " +
            "WHERE s.content LIKE CONCAT('%', :content, '%') " +
            "AND (s.authorID.username = :author OR s.state IN :state)")
    Page<Story> findByContentForJournalist(@Param("content") String content,
                                          @Param("state") Set<StoryState> state,
                                          @Param("author") String authorID,
                                          Pageable pageable);


    // -- Find All Stories -- //
    @Query("SELECT s FROM Story s " +
            "WHERE s.state IN :state " +
            "ORDER BY CASE s.state " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.CREATED THEN 1 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.SUBMITTED THEN 2 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.APPROVED THEN 3 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.PUBLISHED THEN 4 " +
            "END")
    Page<Story> findAllStories(@Param("state") Set<StoryState> state,
                               Pageable pageable);

    @Query("SELECT s FROM Story s " +
            "WHERE s.authorID.username = :author OR s.state IN :state " +
            "ORDER BY CASE s.state " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.CREATED THEN 1 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.SUBMITTED THEN 2 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.APPROVED THEN 3 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.PUBLISHED THEN 4 " +
            "END")
    Page<Story> findAllStoriesForJournalist(@Param("state") Set<StoryState> state,
                                            @Param("author") String authorID,
                                            Pageable pageable);



    // -- Find Stories By Name And Content-- //
    @Query("SELECT s FROM Story s " +
            "WHERE s.name LIKE CONCAT('%', :name, '%') " +
            "AND s.content LIKE CONCAT('%', :content, '%')" +
            "AND s.state IN :state")
    Page<Story> findByNameAndContent(@Param("name") String name,
                                      @Param("state") Set<StoryState> state,
                                      @Param("content") String content,
                                      Pageable pageable);

    @Query("SELECT s FROM Story s " +
            "WHERE s.name LIKE CONCAT('%', :name, '%') " +
            "AND s.content LIKE CONCAT('%', :content, '%') " +
            "AND (s.authorID.username = :author OR s.state IN :state)")
    Page<Story> findByNameAndContentForJournalist(@Param("name") String name,
                                                 @Param("state") Set<StoryState> state,
                                                 @Param("author") String authorID,
                                                 @Param("content") String content,
                                                 Pageable pageable);


    // -- Find Stories By Creation Date-- //
    Page<Story> findByCreationDateBetweenAndStateIn
    (Date firstDate, Date secondDate, Set<StoryState> state, Pageable pageable);

    @Query("SELECT s FROM Story s " +
            "WHERE s.creationDate BETWEEN  :firstDate AND :secondDate " +
            "AND (s.authorID.username = :author OR s.state IN :state)")
    Page<Story> findByDateRangeForJournalist(@Param("firstDate") Date firstDate,
                                            @Param("secondDate") Date secondDate,
                                            @Param("state") Set<StoryState> state,
                                            @Param("author") String authorID,
                                            Pageable pageable);


    // -- Find Stories By State -- //
    Page<Story> findByState(StoryState state, Pageable pageable);

    @Query("SELECT s FROM Story s " +
            "WHERE s.authorID.username = :author AND s.state IN :state")
    Page<Story> findByStateForJournalist(@Param("state") StoryState state,
                                         @Param("author") String authorID,
                                         Pageable pageable);


    // -- Find Stories By Topic ID -- //
    @Query("SELECT s FROM Topic t " +
            "JOIN t.storiesList s " +
            "WHERE t.id = :id " +
            "AND s.state IN :state")
    Page<Story> findByTopicID(@Param("id") Long id,
                              @Param("state") Set<StoryState> state,
                              Pageable pageable);

    @Query("SELECT s FROM Topic t " +
            "JOIN t.storiesList s " +
            "WHERE t.id = :id " +
            "AND (s.state IN :state OR s.authorID.username = :author)")
    Page<Story> findByTopicIDForJournalist(@Param("id") Long id,
                                           @Param("state") Set<StoryState> state,
                                           @Param("author") String author,
                                           Pageable pageable);
}
