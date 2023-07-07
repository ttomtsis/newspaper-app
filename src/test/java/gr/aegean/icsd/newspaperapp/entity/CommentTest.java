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
     * Test case - EC1
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
     * Test case - EC2
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
     * Test case - EC3
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
     * Test case - EC4
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
     * Test case - EC5
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
     * Test case - EC6
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

        long testCommentID = testComment.getId();
        long storyID = story.getId();

        entityManager.remove(story);
        entityManager.flush();
        entityManager.clear();

        assertNull(entityManager.find(Story.class, storyID));
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
        entityManager.clear();

        story.addComment(testComment);
        entityManager.flush();
        entityManager.clear();

        assertTrue(story.getComments().contains(testComment));

        testComment.setContent("new content");
        entityManager.flush();
        entityManager.clear();

        assertEquals("new content",testComment.getContent());
        assertEquals("new content",story.getComments().stream().toList().get(0).getContent());

    }


}
