package gr.aegean.icsd.newspaperapp.entity;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.exception.DataException;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TopicTest {

    @Autowired
    private TestEntityManager entityManager;

    private User author;

    private Topic parentTopic;

    private final static Logger log = LoggerFactory.getLogger("### StoryTest ###");

    public TopicTest() {
        log.info("Initializing Topic Entity Tests...");
    }

    @BeforeEach
    protected void initialize() {
        author = new User("testName", "testPassword", UserType.CURATOR);
        parentTopic = new Topic("testParentTopic", author);

        entityManager.persist(author);
        entityManager.persist(parentTopic);
        entityManager.flush();

        log.info("Author and Parent Topic created with authorID: " + this.author.getId() + " and parentTopicID: " + this.parentTopic.getId());
    }

    /**
     * Test Case - ET1 <br>
     * Check for valid behaviour of the Topic so long as the provided parameters are valid
     */
    @Test
    public void testValidCreationAndPersistence() {

        // Test constructor Str, User
        Topic testTopic = new Topic("testTopic", author);

        assertNull(testTopic.getId());
        assertNull(testTopic.getCreationDate());

        entityManager.persistAndFlush(testTopic);

        assertNotNull(testTopic.getId());
        assertNotNull(testTopic.getCreationDate());
        assertEquals(TopicState.SUBMITTED, testTopic.getState());
        assertEquals(0, testTopic.getStories().size());
        assertEquals(0, testTopic.getChildrenTopics().size());
        assertNull(testTopic.getParentTopic());
        assertNotNull(entityManager.find(Topic.class, testTopic.getId()));

        // Test constructor Str, User, Topic
        Topic testTopic2 = new Topic("testTopic2", author, parentTopic);

        assertNull(testTopic2.getId());
        assertNull(testTopic2.getCreationDate());

        entityManager.persistAndFlush(testTopic2);

        assertNotNull(testTopic2.getId());
        assertNotNull(testTopic2.getCreationDate());
        assertEquals(TopicState.SUBMITTED, testTopic2.getState());
        assertEquals(0, testTopic2.getStories().size());
        assertEquals(0, testTopic2.getChildrenTopics().size());
        assertNotNull(testTopic2.getParentTopic());
        assertNotNull(entityManager.find(Topic.class, testTopic2.getId()));
    }


    /**
     * Test Case - ES2 <br>
     * Check if it is possible to persist a Topic with invalid parameters
     */
    @Test
    public void testPersistenceOfViolatedConstraints() {

        // No parameters given
        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Topic()));

        String invalidSizeString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Nam euismod, tortor nec pharetra ultricies, ante erat imperdiet velit, nec" +
                " laoreet enim lacus a velit. Nam elementum ullamcorper orci, ac porttitor velit" +
                " commodo ut. Sed quis nisl elementum, bibendum est at, porta erat. In hac habitasse" +
                " platea dictumst. Vivamus eget nibh id lacus mollis placerat. Nulla facilisi. Donec lacinia" +
                " congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel.";


        //Name tests
        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Topic(invalidSizeString, author)));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Topic(null, author)));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Topic("   ", author)));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Topic("", author)));

        // Duplicate name test
        Topic test1 = new Topic("sameName",author);
        Topic test2 = new Topic("sameName",author);
        entityManager.persist(test1);

        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                entityManager.persist(test2));

        // Author tests
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                entityManager.persist(new Topic("testName",null)));

        assertThrows(IllegalStateException.class, () ->
                entityManager.persist(new Topic("testName",new User())));

        // Topic tests
        assertThrows(RuntimeException.class, () ->
                entityManager.persist(new Topic("testName", author,null)));

        assertThrows(IllegalStateException.class, () ->
                entityManager.persist(new Topic("testName", author, new Topic("testTopic", author))));
    }

    /**
     * Test Case - ES3 <br>
     * Check if the Topic's setters function as intended
     */
    @Test
    public void testSetters() {

        Topic testTopic = new Topic("testName", author);
        Topic testTopic2 = new Topic("Duplicate Name", author);

        entityManager.persist(testTopic);
        entityManager.persist(testTopic2);
        entityManager.flush();

        String invalidSizeString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Nam euismod, tortor nec pharetra ultricies, ante erat imperdiet velit, nec" +
                " laoreet enim lacus a velit. Nam elementum ullamcorper orci, ac porttitor velit" +
                " commodo ut. Sed quis nisl elementum, bibendum est at, porta erat. In hac habitasse" +
                " platea dictumst. Vivamus eget nibh id lacus mollis placerat. Nulla facilisi. Donec lacinia" +
                " congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel.";


        testTopic.setName("validName");
        entityManager.flush();
        assertEquals("validName", testTopic.getName());

        assertThrows(ConstraintViolationException.class, () -> {
            testTopic.setName(invalidSizeString);
            entityManager.flush();
        });

        assertThrows(ConstraintViolationException.class, () -> {
            testTopic.setName(null);
            entityManager.flush();
        });

        assertThrows(ConstraintViolationException.class, () -> {
            testTopic.setName("    ");
            entityManager.flush();
        });

        assertThrows(ConstraintViolationException.class, () -> {
            testTopic.setName("");
            entityManager.flush();
        });

        assertThrows(DataException.class, () -> {
            testTopic.setName("Duplicate Name");
            entityManager.flush();
        });

    }

    /**
     * Test Case - ES4 <br>
     * Check if the Topic's add Story method functions as intended
     */
    @Test
    public void testAddStory() {
        Topic testTopic = new Topic("testName", author);
        entityManager.persistAndFlush(testTopic);

        // null story
        assertEquals(0, testTopic.getStories().size());
        assertThrows(NullPointerException.class, () -> {
            testTopic.addStory(null);
        });
        assertEquals(0, testTopic.getStories().size());

        // valid story
        assertEquals(0, testTopic.getStories().size());

        Story validStory = new Story("validStory",author,"validContent");
        testTopic.addStory(validStory);
        entityManager.flush();
        entityManager.clear();


        assertTrue(testTopic.getStories().contains(validStory));
        // Check that association has not been created, since Topic is not the owning side
        assertFalse(validStory.getTopics().contains(testTopic));

        // duplicate story
        assertEquals(1, testTopic.getStories().size());

        testTopic.addStory(validStory);
        entityManager.flush();

        assertEquals(1, testTopic.getStories().size());
    }

    /**
     * Test Case - ES5 <br>
     * Check if the Topic's add Story method functions as intended
     * when adding a Story that does not exist. <br>
     * This is a separate test case due to a bug <br>
     */
    @Test
    public void testAddNonExistingStory() {
        Topic testTopic = new Topic("testName", author);
        entityManager.persistAndFlush(testTopic);

        long testTopicID = testTopic.getId();

        // Create association with non-existing story
        assertEquals(0, testTopic.getStories().size());

        Story nonExistingStory = new Story("nonExisting", author, "content");
        nonExistingStory.addTopic(testTopic);
        testTopic.addStory(nonExistingStory);

        entityManager.flush();
        entityManager.clear();
        testTopic = entityManager.find(Topic.class, testTopicID);

        assertEquals(0, testTopic.getStories().size());
    }

    /**
     * Test Case - ES6 <br>
     * Check if the Topic's remove Story method functions as intended <br>
     * Remove Story should ONLY remove the specified story from the Topic
     * and should not be able to remove the entire association <br>
     */
    @Test
    public void testRemoveStory() {
        Topic testTopic = new Topic("testName", author);
        Story validStory = new Story("validStory",author,"validContent");

        entityManager.persist(testTopic);
        entityManager.persist(validStory);
        validStory.addTopic(testTopic);
        entityManager.flush();
        entityManager.refresh(testTopic);

        // null story
        assertTrue(testTopic.getStories().contains(validStory));
        assertTrue(validStory.getTopics().contains(testTopic));

        testTopic.removeStory(null);
        entityManager.flush();

        assertTrue(testTopic.getStories().contains(validStory));
        assertTrue(validStory.getTopics().contains(testTopic));

        // non-existing story
        assertTrue(testTopic.getStories().contains(validStory));
        assertTrue(validStory.getTopics().contains(testTopic));

        testTopic.removeStory(new Story("non-existing",author,"validContent"));
        entityManager.flush();

        assertTrue(testTopic.getStories().contains(validStory));
        assertTrue(validStory.getTopics().contains(testTopic));

        // valid story
        assertTrue(testTopic.getStories().contains(validStory));
        assertTrue(validStory.getTopics().contains(testTopic));

        testTopic.removeStory(validStory);
        entityManager.flush();

        assertFalse(testTopic.getStories().contains(validStory));
        // Check that association still exists, since Topic is not the owning side
        assertTrue(validStory.getTopics().contains(testTopic));

    }

    /**
     * Test Case - ES7 <br>
     * Check if the Topic's add Child method functions as intended <br>
     * Used by a parent to add children topics
     */
    @Test
    public void testAddChild() {
        Topic testTopic = new Topic("testName", author);
        entityManager.persistAndFlush(testTopic);

        // null
        assertEquals(0, testTopic.getChildrenTopics().size());

        assertThrows(NullPointerException.class, () -> {
            testTopic.addChild(null);
        });

        assertEquals(0, testTopic.getChildrenTopics().size());

        // valid
        assertEquals(0, testTopic.getChildrenTopics().size());
        Topic newChild = new Topic("child", author);

        testTopic.addChild(newChild);
        entityManager.flush();

        entityManager.clear();
        //testTopic = entityManager.find(Topic.class, testTopic.getId());

        assertTrue(testTopic.getChildrenTopics().contains(newChild));
        assertEquals(testTopic, newChild.getParentTopic());

        // duplicate
        assertEquals(1, testTopic.getChildrenTopics().size());

        testTopic.addChild(newChild);
        entityManager.flush();

        assertEquals(1, testTopic.getChildrenTopics().size());

    }

    /**
     * Test Case - ES8 <br>
     * Check if the Topic's add Child method functions as intended
     * when adding a child Topic that does not exist. <br>
     * This is a separate test case due to a bug <br>
     */
    @Test
    public void testAddNonExistingChildTopic() {
        Topic testTopic = new Topic("testName", author);

        long parentTopicID = parentTopic.getId();

        // non-existing
        assertFalse(parentTopic.getChildrenTopics().contains(testTopic));

        parentTopic.addChild(testTopic);
        entityManager.flush();

        entityManager.clear();
        parentTopic = entityManager.find(Topic.class, parentTopicID);

        assertFalse(parentTopic.getChildrenTopics().contains(testTopic));
    }

    /**
     * Test Case - ES9 <br>
     * Check if the Topic's remove Child method functions as intended <br>
     * Used by a parent to remove children topics
     */
    @Test
    public void testRemoveChildTopic() {
        Topic testTopic = new Topic("testName", author);
        entityManager.persist(testTopic);
        entityManager.flush();

        testTopic.setParent(parentTopic);

        entityManager.flush();
        entityManager.refresh(parentTopic);

        // null topic
        assertTrue(parentTopic.getChildrenTopics().contains(testTopic));
        assertEquals(parentTopic, testTopic.getParentTopic());

        parentTopic.removeChild(null);
        entityManager.flush();

        assertTrue(parentTopic.getChildrenTopics().contains(testTopic));
        assertEquals(parentTopic, testTopic.getParentTopic());

        // non-existing topic
        assertTrue(parentTopic.getChildrenTopics().contains(testTopic));
        assertEquals(parentTopic, testTopic.getParentTopic());

        parentTopic.removeChild(new Topic("non-existing",author));
        entityManager.flush();

        assertTrue(parentTopic.getChildrenTopics().contains(testTopic));
        assertEquals(parentTopic, testTopic.getParentTopic());

        // valid topic
        assertTrue(parentTopic.getChildrenTopics().contains(testTopic));
        assertEquals(parentTopic, testTopic.getParentTopic());

        parentTopic.removeChild(testTopic);
        entityManager.flush();

        assertEquals(0, parentTopic.getChildrenTopics().size());
        // Since parent topic is not the owning side, relationship should still exist
        assertNotNull(testTopic.getParentTopic());
    }

    /**
     * Test Case - ES10 <br>
     * Check if the Topic's set parent method functions as intended
     */
    @Test
    public void testSetParent() {
        Topic testTopic = new Topic("testName", author);
        entityManager.persistAndFlush(testTopic);
        long testTopicID = testTopic.getId();
        long parentTopicID = parentTopic.getId();

        // null parent
        assertNull(testTopic.getParentTopic());

        testTopic.setParent(null);
        entityManager.flush();

        assertNull(testTopic.getParentTopic());

        // non-existing parent
        assertNull(testTopic.getParentTopic());

        testTopic.setParent(new Topic("non-existing",author));
        assertThrows(IllegalStateException.class, () -> {
            entityManager.flush();
        });

        entityManager.clear();
        testTopic = entityManager.find(Topic.class, testTopicID);
        assertNull(testTopic.getParentTopic());

        // valid story
        assertNull(testTopic.getParentTopic());

        testTopic.setParent(parentTopic);
        entityManager.flush();

        assertNotNull(testTopic.getParentTopic());

        parentTopic = entityManager.find(Topic.class, parentTopicID);
        assertTrue(parentTopic.getChildrenTopics().contains(testTopic));
    }

    @Test
    public void testCascade() {

    }

    @Test
    public void testAssociation() {
        Topic testTopic = new Topic("testName", author);
        entityManager.persistAndFlush(testTopic);

        long testTopicID = testTopic.getId();

        // Create association with non-existing story
        assertEquals(0, testTopic.getStories().size());

        Story nonExistingStory = new Story("nonExisting", author, "content");
        nonExistingStory.addTopic(testTopic);
        testTopic.addStory(nonExistingStory);

        entityManager.flush();
        entityManager.clear();
        testTopic = entityManager.find(Topic.class, testTopicID);

        assertEquals(0, testTopic.getStories().size());
    }
}
