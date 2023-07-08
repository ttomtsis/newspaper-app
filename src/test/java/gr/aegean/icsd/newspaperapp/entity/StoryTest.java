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
    public void testConstraintsViolationPersistence() {

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

        // Author tests
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                entityManager.persist(new Story("testName",null,"validContent")));

        assertThrows(IllegalStateException.class, () ->
                entityManager.persist(new Story("testName",new User(),"validContent")));

        // Topic tests
        assertThrows(RuntimeException.class, () ->
                entityManager.persist(new Story("testName",author,"validContent",null)));


        Set<Topic> invalidTopicList = new HashSet<>();
        invalidTopicList.add(new Topic("invalidTopic", author));

        assertThrows(IllegalStateException.class, () ->
                entityManager.persist(new Story("testName",author,"validContent", invalidTopicList)));
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


}
