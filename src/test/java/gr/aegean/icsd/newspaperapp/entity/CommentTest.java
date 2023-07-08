package gr.aegean.icsd.newspaperapp.entity;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Comment Entity
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentTest {

    @Autowired
    private TestEntityManager entityManager;

    private Story story;

    private User author;

    private final static Logger log = LoggerFactory.getLogger("### CommentTest ###");

    public CommentTest() {
        log.info("Initializing Comment Entity Tests...");
    }

    /**
     * Create a Story and a User before every test
     */
    @BeforeEach
    protected void initialize() {
        story = new Story();
        author = new User();

        entityManager.persist(story);
        entityManager.persist(author);
        entityManager.flush();

        log.info("Author and Story created with authorID: " + this.author.getId() + " and storyID: " + this.story.getId());
    }

    /**
     * Test case - EC1 <br>
     * Check if comment has valid behaviour, for valid parameters
     */
    @Test
    public void testValidBehaviour() {

        log.info("Testing scenario: Create valid Comment with no Author provided");
        Comment noAuthorTestComment = new Comment(story,"test");
        entityManager.persist(noAuthorTestComment);
        entityManager.flush();

        assertNotNull(entityManager.find(Comment.class, noAuthorTestComment.getId()));

        log.info("Testing scenario: Create valid Comment with no Author provided");
        Comment AuthorTestComment = new Comment(story,"test", author);
        entityManager.persist(AuthorTestComment);
        entityManager.flush();

        assertNotNull(entityManager.find(Comment.class, AuthorTestComment.getId()));

    }

    /**
     * Test case - EC2 <br>
     * Check if comment can be created with constraint violating
     * parameters
     */
    @Test
    public void testCommentCreation() {

        log.info("Testing scenario: Create Comment without providing any of the required fields");
        assertThrows(ConstraintViolationException.class, () -> entityManager.persist(new Comment()));

        log.info("Testing scenario: Create Comment with empty content");
        assertThrows(ConstraintViolationException.class, () -> entityManager.persist(new Comment(story, "")));

        log.info("Testing scenario: Create Comment with null Content");
        assertThrows(ConstraintViolationException.class, () -> entityManager.persist(new Comment(story, null)));

        log.info("Testing scenario: Create Comment with whitespace Content");
        assertThrows(ConstraintViolationException.class, () -> entityManager.persist(new Comment(story, "   ")));

        log.info("Testing scenario: Create Comment with null Story");
        assertThrows(ConstraintViolationException.class, () -> entityManager.persist(new Comment(null, "test")));

        log.info("Testing scenario: Create Comment with non-existing Story");
        assertThrows(IllegalStateException.class, () -> entityManager.persist(new Comment(new Story(), "test")));

        log.info("Testing scenario: Create Comment with null Author");
        assertThrows(RuntimeException.class, () -> entityManager.persist(new Comment(story, "test", null)));

        log.info("Testing scenario: Create Comment with non-existing Author");
        assertThrows(IllegalStateException.class, () -> entityManager.persist(new Comment(new Story(), "test", new User())));

    }

    /**
     * Test case - EC3 <br>
     * Check if comment method setContent works appropriately
     */
    @Test
    public void testSetContent() {

        Comment testComment = new Comment(story, "test");
        entityManager.persist(testComment);
        entityManager.flush();

        log.info("Testing scenario: setContent with null content)");
        assertThrows(RuntimeException.class, () -> testComment.setContent(null));


        log.info("Testing scenario: setContent with empty content");
        assertThrows(RuntimeException.class, () -> testComment.setContent(""));

        log.info("Testing scenario: setContent with whitespace content");
        assertThrows(RuntimeException.class, () -> testComment.setContent("   "));

    }

    /**
     * Test case - EC4 <br>
     * Check if comment method setState works appropriately
     */
    @Test
    public void testSetState() {

        Comment testComment = new Comment(story, "test");
        entityManager.persist(testComment);
        entityManager.flush();

        log.info("Testing scenario: setState with null state)");
        assertThrows(RuntimeException.class, () -> testComment.setState(null));


        log.info("Testing scenario: setState with empty state");
        assertThrows(IllegalArgumentException.class, () -> testComment.setState(CommentState.valueOf("")));

        log.info("Testing scenario: setContent with invalid state");
        assertThrows(IllegalArgumentException.class, () -> testComment.setState(CommentState.valueOf("Test")));

    }

    /**
     * Test case - EC5 <br>
     * Check if deleting a comment affects the parent Story entity
     */
    @Test
    public void testOnDeleteCommentCascade() {

        Comment testComment = new Comment(story, "test");
        entityManager.persist(testComment);
        entityManager.flush();

        entityManager.remove(testComment);
        entityManager.flush();

        Story result = entityManager.find(Story.class, story.getId());
        assertNotNull(result);

    }

    /**
     * Test case - EC6 <br>
     * Check if the generated values are generated properly after persistence
     */
    @Test
    public void testGeneratedValues() {

        Comment testComment = new Comment(story, "test");

        assertNull(testComment.getId());
        assertNull(testComment.getCreationDate());
        assertEquals(CommentState.SUBMITTED, testComment.getState());

        entityManager.persist(testComment);
        entityManager.flush();

        long testCommentID = testComment.getId();
        Date testCommentDate = testComment.getCreationDate();
        CommentState testCommentState = testComment.getState();

        assertNotNull(testCommentID);
        assertNotNull(testCommentDate);
        assertEquals(CommentState.SUBMITTED, testCommentState);

    }

    /**
     * Test case - EC7 <br>
     * Check if deleting a story deletes associated comments
     */
    @Test
    public void testDeleteStory() {

        Comment testComment = new Comment(story, "test");
        entityManager.persist(testComment);
        entityManager.flush();

        /*
         * Since the same Entity Manager and Persistence contexts are used,
         * the Story residing in the Persistence Context needs to be refreshed by the manager
         * in order to correctly associate the new Comment with the Story in the persistence context
         */
        entityManager.refresh(story);

        long testCommentID = testComment.getId();
        long storyID = story.getId();

        /*
         * Since the same Entity Manager is used, the Persistence Context
         * needs to be refreshed by the manager
         * in order to reflect the changes made by the previous flush.
         * To achieve this the context is cleared to remove all entities
         * and then forced to add them anew using entityManager.find()
         *
         * This action simulates the following behaviour:
         *
         * 1) User A creates a new Transaction, associated with a new persistent
         * context and Entity Manager, to delete a Story in the database
         * 2) User A commits the Transaction and the persistence context is flushed,
         * deleting the Story Entity and all associated Comments from the database
         * 3) User B creates a new Transaction, searching for the Comment that
         * was previously deleted.
         */
        entityManager.remove(story);
        entityManager.flush();
        entityManager.clear();

        assertNull(entityManager.find(Story.class, storyID));
        assertNull(entityManager.find(Comment.class, testCommentID));

    }

    /**
     * Test case - EC8 <br>
     * Check whether upon deletion of a User the associated Comments are
     * deleted as well
     */
    @Test
    public void testDeleteUser() {
        Comment testComment = new Comment(story, "test", author);
        entityManager.persist(testComment);
        entityManager.flush();

        /*
         * NOTE: When referring to 'Author' below, it should be translated as the User Entity.
         * No Author entity exists.
         *
         * Since the same Entity Manager and Persistence contexts are used,
         * the Author residing in the Persistence Context needs to be refreshed by the manager
         * in order to correctly associate the new Comment with the Author in the persistence context
         */
        entityManager.refresh(author);

        long testCommentID = testComment.getId();
        long authorID = author.getId();

        /*
         * Since the same Entity Manager is used, the Persistence Context
         * needs to be refreshed by the manager
         * in order to reflect the changes made by the previous flush.
         * To achieve this the context is cleared to remove all entities
         * and then forced to add them anew using entityManager.find()
         *
         * This action simulates the following behaviour:
         *
         * 1) User A creates a new Transaction, associated with a new persistent
         * context and Entity Manager, to delete an Author in the database
         * 2) User A commits the Transaction and the persistence context is flushed,
         * deleting the Author Entity and all associated Comments from the database
         * 3) User B creates a new Transaction, searching for the Comment that
         * was previously deleted.
         */
        entityManager.remove(author);
        entityManager.flush();
        entityManager.clear();

        assertNull(entityManager.find(User.class, authorID));
        assertNull(entityManager.find(Comment.class, testCommentID));
    }

    /**
     * Test case - EC9 <br>
     * Check if upon update of a Comment's content, the change is reflected
     * in the Story
     */
    @Test
    public void testUpdateStoryCommentList() {

        Comment testComment = new Comment(story, "test");
        entityManager.persist(testComment);
        entityManager.flush();

        /*
        * Since the same Entity Manager is used, the Story residing in the
        * Persistence Context needs to be refreshed by the manager
        * in order to reflect the changes made by the previous flush.
        *
        * This action simulates the following behaviour:
        *
        * 1) User A creates a new Transaction, associated with a new persistent
        * context and Entity Manager, to save a Comment in the database
        * 2) User A commits the Transaction and the persistence context is flushed,
        * saving the new Comment Entity in the database
        * 3) User B creates a new Transaction, fetching all the Comments
        * associated with a Story, including the Comment User A created.
        */
        entityManager.refresh(story);

        assertTrue(story.getComments().contains(testComment));

        /*
        * The Comment's content field is updated, since the Story and the Comment
        * now share an association in the persistence context, as well as in the database,
        * it is not necessary to refresh the Story again.
        */
        testComment.setContent("new content");
        entityManager.flush();

        assertEquals("new content",testComment.getContent());
        assertEquals("new content",story.getComments().stream().toList().get(0).getContent());

    }

    /**
     * Test case - EC10 <br>
     * Check if setContent and setState function as expected with valid parameters
     */
    @Test
    public void testValidSetStateSetContent() {

        Comment testComment = new Comment(story, "test");
        entityManager.persist(testComment);
        entityManager.flush();
        entityManager.clear();

        testComment.setContent("test content");
        testComment.setState(CommentState.APPROVED);
        entityManager.flush();
        entityManager.clear();

        assertEquals("test content", testComment.getContent());
        assertEquals(CommentState.APPROVED, testComment.getState());

    }
}
