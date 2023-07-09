package gr.aegean.icsd.newspaperapp.entity;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import gr.aegean.icsd.newspaperapp.util.enums.UserType;
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

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Story Entity
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoryTest {

    @Autowired
    private TestEntityManager entityManager;

    private User author;

    private Topic topic;

    private final static Logger log = LoggerFactory.getLogger("### StoryTest ###");

    public StoryTest() {
        log.info("Initializing Story Entity Tests...");
    }

    @BeforeEach
    protected void initialize() {
        author = new User("testName", "testPassword", UserType.CURATOR);
        topic = new Topic("testTopic", author);

        entityManager.persist(author);
        entityManager.persist(topic);
        entityManager.flush();

        log.info("Author and Topic created with authorID: " + this.author.getId() + " and topicID: " + this.topic.getId());
    }

    /**
     * Test Case - ES1 <br>
     * Check for valid behaviour of the Story so long as parameters are valid
     */
    @Test
    public void testValidStoryCreationAndPersistence() {

        Story testStory = new Story("testStory", author, "validContent");

        assertNull(testStory.getId());
        assertNull(testStory.getCreationDate());

        entityManager.persist(testStory);
        entityManager.flush();

        assertNotNull(testStory.getId());
        assertNotNull(testStory.getCreationDate());
        assertEquals(StoryState.CREATED, testStory.getState());
        assertNotNull(entityManager.find(Story.class, testStory.getId()));
    }

    /**
     * Test Case - ES2 <br>
     * Check if it is possible to persist a story with invalid parameters
     */
    @Test
    public void testPersistenceOfViolatedConstraints() {

        // No parameters given
        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story()));

        // Content tests
        String invalidSizeString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Nam euismod, tortor nec pharetra ultricies, ante erat imperdiet velit, nec" +
                " laoreet enim lacus a velit. Nam elementum ullamcorper orci, ac porttitor velit" +
                " commodo ut. Sed quis nisl elementum, bibendum est at, porta erat. In hac habitasse" +
                " platea dictumst. Vivamus eget nibh id lacus mollis placerat. Nulla facilisi. Donec lacinia" +
                " congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel" +
                "congue felis in faucibus. Nunc non tincidunt neque, eu ultrices arcu. Praesent vel.";

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story("testName", author, invalidSizeString)));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story("testName",author,null)));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story("testName",author,"   ")));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story("testName",author,"")));

        //Name tests
        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story(invalidSizeString, author, "validContent")));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story(null, author,"validContent")));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story("   ", author,"validContent")));

        assertThrows(ConstraintViolationException.class, () ->
                entityManager.persist(new Story("", author,"validContent")));

            // Duplicate name test
        Story test1 = new Story("sameName",author,"validContent");
        Story test2 = new Story("sameName",author,"validContent");
        entityManager.persist(test1);

        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                entityManager.persist(test2));

        // Author tests
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                entityManager.persist(new Story("testName",null,"validContent")));

        assertThrows(IllegalStateException.class, () ->
                entityManager.persist(new Story("testName",new User(),"validContent")));

        // Topic tests
        assertThrows(RuntimeException.class, () ->
                entityManager.persist(new Story("testName",author,"validContent", (Set<Topic>) null)));

        assertThrows(RuntimeException.class, () ->
                entityManager.persist(new Story("testName",author,"validContent", (Topic) null)));

        Set<Topic> invalidTopicList = new HashSet<>();
        invalidTopicList.add(new Topic("invalidTopic", author));

        assertThrows(IllegalStateException.class, () ->
                entityManager.persist(new Story("testName",author,"validContent", invalidTopicList)));

        assertThrows(RuntimeException.class, () ->
                entityManager.persist(new Story("testName",author,"validContent", new HashSet<>())));

        HashSet<Topic> nullTopicList = new HashSet<>();
        nullTopicList.add(null);
        assertThrows(RuntimeException.class, () ->
                entityManager.persist(new Story("testName",author,"validContent", nullTopicList)));

        assertThrows(IllegalStateException.class, () ->
                entityManager.persist(new Story("testName",author,"validContent", new Topic("testTopic", author))));
    }

    /**
     * Test Case - ES3 <br>
     * Check if the Story's methods can be used with invalid parameters
     */
    @Test
    public void testSetterMethods() {

        Story testStory = new Story("testName", author, "validContent");
        entityManager.persist(testStory);
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

        assertThrows(RuntimeException.class, () -> testStory.setContent(invalidSizeString));
        assertThrows(RuntimeException.class, () -> testStory.setName(invalidSizeString));
        assertThrows(RuntimeException.class, () -> testStory.setRejectionReason(invalidSizeString));

        assertThrows(RuntimeException.class, () -> testStory.setContent(null));
        assertThrows(RuntimeException.class, () -> testStory.setName(null));
        assertThrows(RuntimeException.class, () -> testStory.setRejectionReason(invalidSizeString));

        assertThrows(RuntimeException.class, () -> testStory.setContent("   "));
        assertThrows(RuntimeException.class, () -> testStory.setName("    "));
        assertThrows(RuntimeException.class, () -> testStory.setRejectionReason(invalidSizeString));

        assertThrows(RuntimeException.class, () -> testStory.setContent(""));
        assertThrows(RuntimeException.class, () -> testStory.setName(""));
        assertThrows(RuntimeException.class, () -> testStory.setRejectionReason(invalidSizeString));

        testStory.setContent("New Valid Content");
        testStory.setName("New Valid Name");
        testStory.setRejectionReason("New Rejection Reason");
        entityManager.flush();

        assertEquals("New Valid Content", testStory.getContent());
        assertEquals("New Valid Name", testStory.getName());
        assertEquals("New Rejection Reason", testStory.getRejectionReason());
    }

    /**
     * Test Case - ES4 <br>
     * Check if the Story's addTopic method functions as expected
     */
    @Test
    public void testAddTopic() {
        Story testStory = new Story("testName", author, "validContent");
        entityManager.persistAndFlush(testStory);

        // Valid functionality
        assertEquals(0, testStory.getTopics().size());

        testStory.addTopic(topic);
        entityManager.flush();
        entityManager.refresh(topic);

        assertEquals(1, testStory.getTopics().size());

        // null Topic
        assertThrows(NullPointerException.class, () ->
                testStory.addTopic(null));
        assertEquals(1, testStory.getTopics().size());

        // Non-existing Topic
        testStory.addTopic(new Topic("testTopic",author));
        assertThrows(IllegalStateException.class, () -> entityManager.flush());

        // Creating new TestStory since the previous one cannot be flushed
        // due to the non-existing Topic present in it's topicsList
        Story newTestStory = new Story("newTestName", author, "validContent", topic);

        // Cleaning the persistence context so the previous story is no longer managed
        entityManager.clear();
        entityManager.persistAndFlush(newTestStory);

        // Duplicate Topic
        newTestStory.addTopic(topic);
        entityManager.flush();
        assertEquals(1, newTestStory.getTopics().size());
        assertEquals(1, topic.getStories().size());

    }

    /**
     * Test Case - ES5 <br>
     * Check if the Story's removeTopic method functions as expected
     */
    @Test
    public void removeTopic() {
        Story testStory = new Story("testName", author, "validContent", topic);
        entityManager.persistAndFlush(testStory);

        // null
        assertEquals(1, testStory.getTopics().size());
        testStory.removeTopic(null);
        entityManager.flush();
        assertEquals(1, testStory.getTopics().size());

        // non-existing
        assertEquals(1, testStory.getTopics().size());
        testStory.removeTopic(new Topic("testTopic",author));
        entityManager.flush();
        assertEquals(1, testStory.getTopics().size());

        // valid
        assertEquals(1, testStory.getTopics().size());
        testStory.removeTopic(topic);
        entityManager.flush();
        assertEquals(0, testStory.getTopics().size());
    }

    /**
     * Test Case - ES6 <br>
     * Check if the Story's foreign key constraints and cascade behaviour are valid
     */
    @Test
    public void testForeignKeyConstraints() {

        // Update Story, update Topic
        Story testStory = new Story("testName", author, "validContent", topic);
        entityManager.persist(testStory);
        entityManager.flush();

        testStory.setName("New Valid Name");
        entityManager.flush();
        entityManager.refresh(topic);

        assertEquals("New Valid Name", topic.getStories().stream().toList().get(0).getName());

        // Update Topic, update Story
        topic.setName("New Valid Topic Name");
        entityManager.flush();
        entityManager.refresh(testStory);

        assertEquals("New Valid Topic Name", testStory.getTopics().stream().toList().get(0).getName());

        // Add same Story twice in the same Topic
        topic.addStory(testStory);
        entityManager.flush();
        entityManager.refresh(testStory);
        entityManager.refresh(topic);

        assertEquals(1, testStory.getTopics().size());
        assertEquals(1, topic.getStories().size());

        // 2 Stories in 1 Topic
        assertEquals(1, topic.getStories().size());

        Story testStory2 = new Story("testName", author, "validContent", topic);
        entityManager.persist(testStory2);
        entityManager.flush();

        topic.addStory(testStory2);
        entityManager.flush();

        assertEquals(2, topic.getStories().size());

        // 2 Topics in 1 Story
        assertEquals(1, topic.getStories().size());

        Topic topic2 = new Topic("topic2",author);
        entityManager.persist(topic2);
        entityManager.flush();

        testStory2.addTopic(topic2);
        entityManager.flush();

        assertEquals(2, testStory2.getTopics().size());

        // Delete Topic, Story persist
        long testStory2ID = testStory2.getId();

        testStory2.removeTopic(topic2);
        entityManager.remove(topic2);
        entityManager.flush();

        assertNotNull(entityManager.find(Story.class, testStory2ID));
        assertEquals(1, testStory2.getTopics().size());

        // Delete Story, Topic persist
        long topicID = topic.getId();

        topic.removeStory(testStory);
        entityManager.remove(testStory);
        entityManager.flush();

        assertNotNull(entityManager.find(Topic.class, topicID));
        assertEquals(1, topic.getStories().size());

    }




}
