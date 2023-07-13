package gr.aegean.icsd.newspaperapp.entity;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Topic Entity tests")
@Tag("Entity")
@Tag("Topic")
public class TopicTest {

    @Autowired
    private TestEntityManager entityManager;

    private static User mockAuthor;

    private static Topic mockTopic;

    private static Story mockStory;

    private final static Logger log = LoggerFactory.getLogger("### TopicTest ###");

    public TopicTest() {}

    @BeforeEach
    protected void initialize() {

        mockAuthor = new User("mockAuthor", "testPassword", UserType.CURATOR);
        mockTopic = new Topic("mockTopic", mockAuthor);
        mockStory = new Story("mockStory", mockAuthor, "testContent");

        entityManager.persist(mockAuthor);
        entityManager.persist(mockTopic);
        entityManager.persist(mockStory);
        entityManager.flush();

    }

    private static Stream<Story> storyGenerator() {
        return Stream.of(
                null,
                new Story("nonExistingStory", mockAuthor, "validContent")
        );
    }

    private static Stream<Topic> topicGenerator() {
        return Stream.of(
                null,
                new Topic("nonExistingTopic", mockAuthor)
        );
    }

    private static Stream<String> generateLargeString() {
        return Stream.of(String.join("", Collections.nCopies(500, "a")));
    }


    @Nested
    @DisplayName("Constructor tests")
    @Tag("Constructor")
    class constructorTests {

        @Test
        @DisplayName("Constructor String, Author - Valid parameters")
        public void constructorStringAuthor() {

            Topic testTopic = new Topic("testTopic", mockAuthor);

            assertAll(
                    () -> assertNull(testTopic.getId(), "ID should be null before persistence"),
                    () -> assertNull(testTopic.getCreationDate(), "Creation date should be null before persistence"),
                    () -> assertEquals(TopicState.SUBMITTED, testTopic.getState(),
                            "State should be SUBMITTED before persistence"),
                    () -> assertEquals(0, testTopic.getStories().size(), "Associated stories should be 0"),
                    () -> assertEquals(0, testTopic.getChildrenTopics().size(), "Children topics should be 0"),
                    () -> assertNull(testTopic.getParentTopic(), "Parent topic should be null")
                    );

            entityManager.persistAndFlush(testTopic);

            assertAll(
                    () -> assertNotNull(testTopic.getId(), "ID should not be null after persistence"),
                    () -> assertNotNull(testTopic.getCreationDate(), "Creation date should not be null after persistence"),
                    () -> assertEquals(TopicState.SUBMITTED, testTopic.getState(),
                            "State should be SUBMITTED after persistence"),
                    () -> assertEquals(0, testTopic.getStories().size(), "Associated stories should be 0"),
                    () -> assertEquals(0, testTopic.getChildrenTopics().size(), "Children topics should be 0"),
                    () -> assertNull(testTopic.getParentTopic(), "Parent topic should be null"),
                    () -> assertNotNull(entityManager.find(Topic.class, testTopic.getId()),
                            "Entity should exist in the database after persistence")
            );

        }

        @Test
        @DisplayName("Constructor String, Author, Topic - Valid parameters")
        public void constructorStringAuthorTopic() {

            Topic testTopic = new Topic("testTopic", mockAuthor, mockTopic);

            assertAll(
                    () -> assertNull(testTopic.getId(), "ID should be null before persistence"),
                    () -> assertNull(testTopic.getCreationDate(), "Creation date should be null before persistence"),
                    () -> assertEquals(TopicState.SUBMITTED, testTopic.getState(),
                            "State should be SUBMITTED before persistence"),
                    () -> assertEquals(0, testTopic.getStories().size(), "Associated stories should be 0"),
                    () -> assertEquals(0, testTopic.getChildrenTopics().size(), "Children topics should be 0"),
                    () -> assertSame(mockTopic, testTopic.getParentTopic(), "Provided parent topic and saved parent topic" +
                            "should be the same")
            );

            entityManager.persistAndFlush(testTopic);

            assertAll(
                    () -> assertNotNull(testTopic.getId(), "ID should not be null after persistence"),
                    () -> assertNotNull(testTopic.getCreationDate(), "Creation date should not be null after persistence"),
                    () -> assertEquals(TopicState.SUBMITTED, testTopic.getState(),
                            "State should be SUBMITTED after persistence"),
                    () -> assertEquals(0, testTopic.getStories().size(), "Associated stories should be 0"),
                    () -> assertEquals(0, testTopic.getChildrenTopics().size(), "Children topics should be 0"),
                    () -> assertSame(mockTopic, testTopic.getParentTopic(), "Provided parent topic and saved parent topic" +
                            "should be the same"),
                    () -> assertNotNull(entityManager.find(Topic.class, testTopic.getId()),
                            "Entity should exist in the database after persistence")
            );

        }

        @Test
        @DisplayName("Null Parent Topic")
        public void nullParentTopic() {

            assertThrows(NullPointerException.class, () ->
                    entityManager.persist(new Topic("validName", mockAuthor, null)),
                    "Constraint Violation Exception should be thrown" +
                    "when parent topic is null"
            );

        }

        @Test
        @DisplayName("Non-Existing Parent Topic")
        public void nonExistingParentTopic() {

            Topic nonExistingTopic = new Topic ("validName", mockAuthor);
            Topic testTopic = new Topic("validName", mockAuthor, nonExistingTopic);

            assertThrows(IllegalStateException.class, () -> entityManager.persistAndFlush(testTopic),
                    "An Illegal State Exception should be thrown when flushing the transaction" +
                            "and a non-existing Topic has been specified in the database");

        }

        @ParameterizedTest
        @DisplayName("Invalid name")
        @NullAndEmptySource
        @MethodSource("gr.aegean.icsd.newspaperapp.entity.TopicTest#generateLargeString")
        @ValueSource(strings = "   ")
        public void invalidName(String name) {

            log.info("Testing name: " + name);

            assertAll(
                    () -> assertThrows(ConstraintViolationException.class, () ->
                        entityManager.persist(new Topic(name, mockAuthor)),
                            "Constraint violation exception should be thrown" +
                            "when name is: " + name
                    ),
                    () -> assertThrows(ConstraintViolationException.class, () ->
                        entityManager.persist(new Topic(name, mockAuthor, mockTopic)),
                            "Constraint violation exception should be thrown" +
                            "when name is: " + name
                    )
            );

        }

        @Test
        @DisplayName("Duplicate name")
        public void duplicateName() {

            assertAll(
                    () -> assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                            entityManager.persist(new Topic(mockTopic.getName(), mockAuthor)),
                            "Hibernate Constraint violation exception should be thrown" +
                            "when specified name is not unique"
                    ),
                    () -> assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                            entityManager.persist(new Topic(mockTopic.getName(), mockAuthor, mockTopic)),
                            "Hibernate Constraint violation exception should be thrown" +
                            "when specified name is not unique"
                    )
            );

        }

        @Test
        @DisplayName("Null author")
        public void nullAuthor() {

            assertAll(
                    () -> assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                            entityManager.persist(new Topic("validName", null)),
                            "Hibernate Constraint violation exception should be thrown" +
                            "when specified name is not unique"
                    ),
                    () -> assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                            entityManager.persist(new Topic("validName", null, mockTopic)),
                            "Hibernate Constraint violation exception should be thrown" +
                            "when specified name is not unique"
                    )
            );

        }

        @Test
        @DisplayName("Non-Existing author")
        public void invalidAuthor() {

            User nonExistingAuthor = new User("username","password",UserType.JOURNALIST);

            assertAll(
                    () -> assertThrows(IllegalStateException.class, () ->
                            entityManager.persist(new Topic("validName", nonExistingAuthor)),
                            "Illegal State Exception should be thrown" +
                            "when specified author does not exist in the database"
                    ),
                    () -> assertThrows(IllegalStateException.class, () ->
                            entityManager.persist(new Topic("validName", nonExistingAuthor, mockTopic)),
                            "Illegal State Exception should be thrown" +
                            "when specified author does not exist in the database"
                    )
            );

        }

    }


    @Nested
    @DisplayName("Setter method tests")
    @Tag("SetterMethods")
    class setterTests {

        @Test
        @DisplayName("setName with valid parameters")
        public void testSetNameValid() {

            mockTopic.setName("new Valid Name");
            entityManager.flush();

            assertEquals("new Valid Name", mockTopic.getName(), "Strings should be equal");

        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "   ")
        @MethodSource(value = "gr.aegean.icsd.newspaperapp.entity.TopicTest#generateLargeString")
        @DisplayName("setName with invalid parameters")
        public void testSetName(String topicName) {

            mockTopic.setName(topicName);

            assertThrows(ConstraintViolationException.class, () ->
                entityManager.flush(),
                "Constraint Violation Exception should be thrown when name is: " + topicName
            );

        }

        @Test
        @DisplayName("setName with duplicate name")
        public void testSetNameDuplicate() {

            Topic testTopic = new Topic("Valid Name", mockAuthor);
            entityManager.persistAndFlush(testTopic);

            testTopic.setName(mockTopic.getName());

            assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                    entityManager.flush(),
                    "Hibernate Constraint Violation Exception should be thrown when a non unique name is set"
            );

        }

    }


    @Nested
    @DisplayName("Entity Association tests")
    @Tag("Associations")
    class associationTests {

        @Nested
        @DisplayName("Topic-Story Association tests")
        @Tag("TopicStoryAssociation")
        class topicStoryAssociationTests {

            @Test
            @DisplayName("Create association with a valid Story")
            public void addValidStory() {

                mockTopic.addStory(mockStory);

                entityManager.flush();

                mockTopic = entityManager.refresh(mockTopic);

                assertAll(
                        () -> assertFalse(mockStory.getTopics().contains(mockTopic),
                                "Story should not be associated with Topic, since Topic is not the" +
                                        "owning side of the relationship"),

                        () -> assertFalse(mockTopic.getStories().contains(mockStory),
                                "Topic should not be associated with Story, Topic Story is not the" +
                                        "owning side of the relationship")
                );

            }

            @Test
            @DisplayName("Create association with a duplicate Story")
            public void addDuplicateStory() {

                mockStory.addTopic(mockTopic);
                mockTopic.addStory(mockStory);

                entityManager.flush();

                assertTrue(mockTopic.getStories().contains(mockStory),
                        "Topic should be associated with the new Story");

                assertTrue(mockStory.getTopics().contains(mockTopic),
                        "Story should be associated with the new Topic");

                // Adding twice
                mockTopic.addStory(mockStory);
                entityManager.flush();

                mockTopic = entityManager.refresh(mockTopic);

                assertEquals(1, mockTopic.getStories().size(),
                        "Duplicate topic shouldn't have been added");

            }

            @ParameterizedTest
            @MethodSource("gr.aegean.icsd.newspaperapp.entity.TopicTest#storyGenerator")
            @DisplayName("Create association with an invalid Story")
            public void addStoryTest(Story testStory) {

                mockTopic.addStory(testStory);
                entityManager.flush();

                entityManager.clear();
                mockTopic = entityManager.find(Topic.class, mockTopic.getId());

                assertTrue(mockTopic.getStories().isEmpty());

            }

            @Test
            @DisplayName("Remove association with a valid Story")
            public void removeValidStoryTest() {

                // Add a Story
                mockStory.addTopic(mockTopic);
                mockTopic.addStory(mockStory);

                entityManager.flush();

                assertTrue(mockTopic.getStories().contains(mockStory),
                        "Topic should be associated with the new Story");

                assertTrue(mockStory.getTopics().contains(mockTopic),
                        "Story should be associated with the new Topic");

                mockTopic.removeStory(mockStory);
                entityManager.flush();

                mockTopic = entityManager.refresh(mockTopic);

                assertEquals(1, mockTopic.getStories().size(),
                        "The size of the Topic's Story list should be equal to one"
                );

            }

            @ParameterizedTest
            @MethodSource("gr.aegean.icsd.newspaperapp.entity.TopicTest#storyGenerator")
            @DisplayName("Remove association with an invalid Story")
            public void removeStoryTest(Story testStory) {

                // Add a Story
                mockStory.addTopic(mockTopic);
                mockTopic.addStory(mockStory);

                entityManager.flush();

                assertTrue(mockTopic.getStories().contains(mockStory),
                        "Topic should be associated with the new Story");

                assertTrue(mockStory.getTopics().contains(mockTopic),
                        "Story should be associated with the new Topic");

                mockTopic.removeStory(testStory);
                entityManager.flush();

                mockTopic = entityManager.refresh(mockTopic);

                assertEquals(1, mockTopic.getStories().size(),
                        "The size of the Topic's Story list should be equal to one"
                );

            }

        }

        @Nested
        @DisplayName("Topic-Topic Association tests")
        @Tag("TopicTopicAssociation")
        class topicTopicAssociationTests {

            Topic validChildTopic;

            @BeforeEach
            public void createChildTopic() {
                validChildTopic = new Topic("validChildName", mockAuthor);
                entityManager.persistAndFlush(validChildTopic);
            }

            @Test
            @DisplayName("Create association with a valid child Topic")
            public void addValidChild() {

                mockTopic.addChild(validChildTopic);

                entityManager.flush();
                mockTopic = entityManager.refresh(mockTopic);

                assertAll(
                        () -> assertTrue(mockTopic.getChildrenTopics().isEmpty(),
                                "Parent topic's children should not contain the child," +
                                        " since the child is the owning side"),
                        () -> assertNull(validChildTopic.getParentTopic(),
                                "Child topic's parent should be null, since the child is the owning side")
                );

            }

            @ParameterizedTest
            @MethodSource("gr.aegean.icsd.newspaperapp.entity.TopicTest#topicGenerator")
            @DisplayName("Create association with an invalid child Topic")
            public void addInvalidChild(Topic childTopic) {

                mockTopic.addChild(childTopic);

                entityManager.flush();
                entityManager.clear();
                mockTopic = entityManager.find(Topic.class, mockTopic.getId());

                assertTrue(mockTopic.getChildrenTopics().isEmpty(),
                        "Parent topic's children should not contain the child," +
                                " since the child is the owning side");

            }

            @Test
            @DisplayName("Create association with a duplicate child Topic")
            public void addDuplicateChild() {

                mockTopic.addChild(validChildTopic);
                validChildTopic.setParent(mockTopic);

                entityManager.flush();

                assertTrue(mockTopic.getChildrenTopics().contains(validChildTopic),
                        "Topic should be associated with the new child");

                assertSame(mockTopic, validChildTopic.getParentTopic(),
                        "Child should be associated with the new parent");

                // Adding twice
                mockTopic.addChild(validChildTopic);
                entityManager.flush();

                mockTopic = entityManager.refresh(mockTopic);

                assertEquals(1, mockTopic.getChildrenTopics().size(),
                        "Duplicate topic shouldn't have been added");

            }

            @Test
            @DisplayName("Create association with self")
            public void addSelfAsChild() {

                assertThrows(RuntimeException.class, () ->
                        mockTopic.addChild(mockTopic),
                        "Runtime Exception should be thrown when setting self as a child");

            }

            @Test
            @DisplayName("Remove association with a valid child Topic")
            public void removeValidChild() {

                validChildTopic.setParent(mockTopic);
                entityManager.flush();
                entityManager.refresh(mockTopic);

                assertTrue(mockTopic.getChildrenTopics().contains(validChildTopic),
                        "Parent Topic must be associated with the child");

                assertEquals(mockTopic, validChildTopic.getParentTopic(),
                        "Child Topic must be associated with the parent");

                mockTopic.removeChild(validChildTopic);
                entityManager.flush();

                mockTopic = entityManager.refresh(mockTopic);

                assertEquals(1, mockTopic.getChildrenTopics().size(),
                        "Parent Topic must still be associated with child, since the child is the owning side");
            }

            @ParameterizedTest
            @MethodSource("gr.aegean.icsd.newspaperapp.entity.TopicTest#topicGenerator")
            @DisplayName("Remove association with an invalid child Topic")
            public void removeInvalidChild(Topic testTopic) {

                validChildTopic.setParent(mockTopic);
                entityManager.flush();
                entityManager.refresh(mockTopic);

                assertTrue(mockTopic.getChildrenTopics().contains(validChildTopic),
                        "Parent Topic must be associated with the child");

                assertEquals(mockTopic, validChildTopic.getParentTopic(),
                        "Child Topic must be associated with the parent");

                mockTopic.removeChild(testTopic);
                entityManager.flush();

                mockTopic = entityManager.refresh(mockTopic);

                assertEquals(1, mockTopic.getChildrenTopics().size(),
                        "Parent Topic must still be associated with child, since an invalid topic was removed");

            }

            @Test
            @DisplayName("Create parental association with a valid parent topic")
            public void setValidParent() {

                validChildTopic.setParent(mockTopic);
                entityManager.flush();

                entityManager.refresh(mockTopic);

                assertAll(
                        () -> assertTrue(mockTopic.getChildrenTopics().contains(validChildTopic),
                                "Parent topic should be associated with child, since child is owner of" +
                                        "the association"),
                        () -> assertSame(mockTopic, validChildTopic.getParentTopic(),
                                "Child topic should have been associated with the parent")
                );

            }

            @Test
            @DisplayName("Create parental association with an invalid parent topic")
            public void setInvalidParent() {

                Topic nonExistingTopic = new Topic("nonExistingTopic", mockAuthor);

                validChildTopic.setParent(nonExistingTopic);

                assertThrows(IllegalStateException.class, () -> entityManager.flush());

            }

            @Test
            @DisplayName("Create parental association with self")
            public void setSelfAsParent() {

                assertThrows(RuntimeException.class, () -> mockTopic.setParent(mockTopic),
                        "A Runtime Exception should be thrown when trying to set self as a parent topic");

            }

            @Test
            @DisplayName("Remove parental association with a parent")
            public void removeParent() {

                validChildTopic.setParent(mockTopic);
                entityManager.flush();
                entityManager.refresh(mockTopic);

                validChildTopic.setParent(null);
                entityManager.flush();
                entityManager.refresh(mockTopic);

                assertAll(
                        () -> assertTrue(mockTopic.getChildrenTopics().isEmpty(),
                                "Parent Topic should no longer be associated with the child"),
                        () -> assertNull(validChildTopic.getParentTopic(),
                                "Child Topic should no longer be associated with the parent")
                );

            }

            @Test
            @DisplayName("Change parent")
            public void changeParent() {

                validChildTopic.setParent(mockTopic);
                entityManager.flush();
                entityManager.refresh(mockTopic);

                Topic newParent = new Topic("newParent", mockAuthor);
                entityManager.persistAndFlush(newParent);

                validChildTopic.setParent(newParent);

                entityManager.flush();
                entityManager.refresh(newParent);
                entityManager.refresh(mockTopic);

                assertAll(
                        () -> assertTrue(newParent.getChildrenTopics().contains(validChildTopic),
                                "New parent topic should be associated with child, since child is owner of" +
                                        "the association"),
                        () -> assertSame(newParent, validChildTopic.getParentTopic(),
                                "Child topic should have been associated with the new parent")
                );
            }
        }

        @Nested
        @DisplayName("Cascading behaviour tests")
        @Tag("Cascade")
        class cascadingTests {

            @Test
            @DisplayName("On Delete Topic Cascade")
            public void deleteTopic() {

                Topic validChildTopic = new Topic("validChildTopic", mockAuthor);
                entityManager.persistAndFlush(validChildTopic);

                long mockTopicID = mockTopic.getId();
                long mockStoryID = mockStory.getId();
                long validChildTopicID = validChildTopic.getId();

                // Create associations
                mockStory.addTopic(mockTopic);
                mockTopic.addStory(mockStory);
                validChildTopic.setParent(mockTopic);
                entityManager.flush();

                entityManager.refresh(validChildTopic);
                entityManager.refresh(mockTopic);
                entityManager.refresh(mockStory);

                // Ensure they exist
                assertTrue(mockStory.getTopics().contains(mockTopic));
                assertTrue(mockTopic.getStories().contains(mockStory));
                assertSame(mockTopic, validChildTopic.getParentTopic());

                // Delete the parent Topic
                entityManager.remove(mockTopic);
                entityManager.flush();

                // Clear persistence context and refresh entities from db
                entityManager.clear();

                mockStory = entityManager.find(Story.class, mockStoryID);
                mockTopic = entityManager.find(Topic.class, mockTopicID);
                Topic remappedValidChildTopic = entityManager.find(Topic.class, validChildTopicID);

                assertAll(
                        // Topic is deleted
                        () -> assertNull(entityManager.find(Topic.class, mockTopicID)),

                        // Story still exists
                        () -> assertNotNull(entityManager.find(Story.class, mockStoryID)),

                        // Story is not associated with deleted Topic
                        () -> assertTrue(mockStory.getTopics().isEmpty()),

                        // Child Topic still exists
                        () -> assertNotNull(entityManager.find(Topic.class, validChildTopicID)),

                        // Child Topic is not associated with deleted parent Topic
                        () -> assertNull(remappedValidChildTopic.getParentTopic())
                );

            }

            @Test
            @DisplayName("On Update Topic Cascade")
            public void updateTopicCascade() {

                Topic validChildTopic = new Topic("validChildTopic", mockAuthor);
                entityManager.persistAndFlush(validChildTopic);

                long mockTopicID = mockTopic.getId();
                long mockStoryID = mockStory.getId();
                long validChildTopicID = validChildTopic.getId();

                // Create associations
                mockStory.addTopic(mockTopic);
                mockTopic.addStory(mockStory);
                validChildTopic.setParent(mockTopic);
                entityManager.flush();

                entityManager.refresh(validChildTopic);
                entityManager.refresh(mockTopic);
                entityManager.refresh(mockStory);

                // Ensure they exist
                assertTrue(mockStory.getTopics().contains(mockTopic));
                assertTrue(mockTopic.getStories().contains(mockStory));
                assertSame(mockTopic, validChildTopic.getParentTopic());

                // Modify the parent Topic's name
                mockTopic.setName("updatedName");
                entityManager.flush();

                // Clear persistence context and refresh entities from db
                entityManager.clear();

                mockStory = entityManager.find(Story.class, mockStoryID);
                mockTopic = entityManager.find(Topic.class, mockTopicID);
                Topic remappedValidChildTopic = entityManager.find(Topic.class, validChildTopicID);

                assertAll(

                        // Topic is updated
                        () -> assertEquals("updatedName", mockTopic.getName(),
                                "Topic's name should have been updated"),

                        // Story is updated
                        () -> assertEquals("updatedName", mockStory.getTopics().stream().toList().get(0).getName(),
                                "The changes in the Topic should be reflected in the associated Story"),

                        // Child Topic is updated
                        () -> assertEquals("updatedName", remappedValidChildTopic.getParentTopic().getName(),
                                "The changes in the parent Topic should be reflected in the child Topic")

                );

            }

            @Test
            @DisplayName("On Delete Child Topic Cascade")
            public void deleteChildTopic() {
                Topic validChildTopic = new Topic("validChildTopic", mockAuthor);
                entityManager.persistAndFlush(validChildTopic);

                long mockTopicID = mockTopic.getId();
                long validChildTopicID = validChildTopic.getId();

                // Create associations
                validChildTopic.setParent(mockTopic);
                entityManager.flush();

                entityManager.refresh(validChildTopic);
                entityManager.refresh(mockTopic);

                // Ensure associations exist
                assertTrue(mockTopic.getChildrenTopics().contains(validChildTopic));
                assertSame(mockTopic, validChildTopic.getParentTopic());

                // Remove child topic
                entityManager.remove(validChildTopic);
                entityManager.flush();

                // Clear persistence context and refresh entities from db
                entityManager.clear();

                mockTopic = entityManager.find(Topic.class, mockTopicID);
                Topic remappedValidChildTopic = entityManager.find(Topic.class, validChildTopicID);

                assertAll(

                        // Child Topic is removed
                        () -> assertNull(remappedValidChildTopic, "Child Topic should have been removed"),

                        // Parent Topic exists
                        () -> assertNotNull(mockTopic, "Parent Topic shouldn't have been removed"),

                        // Parent Topic is no longer associated with the child Topic
                        () -> assertTrue(mockTopic.getChildrenTopics().isEmpty(),
                                "Parent Topic should no longer be associated with the Child")

                );
            }

        }

    }


}
