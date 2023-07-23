package gr.aegean.icsd.newspaperapp.model.repository;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {


    // -- Find Stories By Name -- //
    List<Story> findByNameContainingIgnoreCaseAndStateIn(String name, Set<StoryState> state);

    @Query("SELECT s FROM Story s " +
            "WHERE s.name LIKE CONCAT('%', :name, '%') " +
            "AND (s.authorID.username = :author OR s.state IN :state)")
    List<Story> findByNameForJouralist(@Param("name") String name, @Param("state") Set<StoryState> state,
                                       @Param("author") String authorID);


    // -- Find Stories By Content -- //
    List<Story> findByContentContainingIgnoreCaseAndStateIn(String content, Set<StoryState> state);

    @Query("SELECT s FROM Story s " +
            "WHERE s.content LIKE CONCAT('%', :content, '%') " +
            "AND (s.authorID.username = :author OR s.state IN :state)")
    List<Story> findByContentForJouralist(@Param("content") String content, @Param("state") Set<StoryState> state,
                                       @Param("author") String authorID);


    // -- Find All Stories -- //
    @Query("SELECT s FROM Story s " +
            "WHERE s.state IN :state " +
            "ORDER BY CASE s.state " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.CREATED THEN 1 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.SUBMITTED THEN 2 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.APPROVED THEN 3 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.PUBLISHED THEN 4 " +
            "END")
    List<Story> findAllStories(@Param("state") Set<StoryState> state);

    @Query("SELECT s FROM Story s " +
            "WHERE s.authorID.username = :author OR s.state IN :state " +
            "ORDER BY CASE s.state " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.CREATED THEN 1 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.SUBMITTED THEN 2 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.APPROVED THEN 3 " +
            "WHEN gr.aegean.icsd.newspaperapp.util.enums.StoryState.PUBLISHED THEN 4 " +
            "END")
    List<Story> findAllStoriesForJournalist(@Param("state") Set<StoryState> state, @Param("author") String authorID);



    // -- Find Stories By Name And Content-- //
    List<Story> findByNameContainingIgnoreCaseAndStateInAndContentIgnoreCase(String name, Set<StoryState> state, String content);

    @Query("SELECT s FROM Story s " +
            "WHERE s.name LIKE CONCAT('%', :name, '%') " +
            "AND s.content LIKE CONCAT('%', :content, '%') " +
            "AND (s.authorID.username = :author OR s.state IN :state)")
    List<Story> findByNameAndContentForJouralist(@Param("name") String name, @Param("state") Set<StoryState> state,
                                       @Param("author") String authorID, @Param("content") String content);


    // -- Find Stories By Creation Date-- //
    List<Story> findByCreationDateBetweenAndStateIn(Date firstDate, Date secondDate, Set<StoryState> state);

    @Query("SELECT s FROM Story s " +
            "WHERE s.creationDate BETWEEN  :firstDate AND :secondDate " +
            "AND (s.authorID.username = :author OR s.state IN :state)")
    List<Story> findByDateRangeForJouralist(@Param("firstDate") Date firstDate, @Param("secondDate") Date secondDate,
                                            @Param("state") Set<StoryState> state, @Param("author") String authorID);


    // -- Find Stories By State -- //
    List<Story> findByState(StoryState state);

    @Query("SELECT s FROM Story s " +
            "WHERE s.authorID.username = :author AND s.state IN :state")
    List<Story> findByStateForJouralist(@Param("state") StoryState state, @Param("author") String authorID);


    // -- Find Stories By Topic ID -- //
    @Query("SELECT s FROM Topic t " +
            "JOIN t.storiesList s " +
            "WHERE t.id = :id " +
            "AND s.state IN :state")
    List<Story> findByTopicID(@Param("id") Long id, @Param("state") Set<StoryState> state);

    @Query("SELECT s FROM Topic t " +
            "JOIN t.storiesList s " +
            "WHERE t.id = :id " +
            "AND (s.state IN :state OR s.authorID.username = :author)")
    List<Story> findByTopicIDForJournalist(@Param("id") Long id, @Param("state") Set<StoryState> state,
                                           @Param("author") String author);
}
