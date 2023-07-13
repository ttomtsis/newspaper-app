package gr.aegean.icsd.newspaperapp.entity;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Test Class for the Story Entity
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Story Entity tests")
@Tag("Entity")
@Tag("Story")
public class StoryTest {

    @Autowired
    private TestEntityManager entityManager;

    private User author;

    private Topic topic;

    private Story story;

    private final static Logger log = LoggerFactory.getLogger("### StoryTest ###");

    public StoryTest() {}

    @BeforeEach
    protected void initialize() {

        author = new User("testName", "testPassword", UserType.CURATOR);
        topic = new Topic("testTopic", author);
        story = new Story("testStory", author, "testContent");

        entityManager.persist(author);
        entityManager.persist(topic);
        entityManager.persist(story);
        entityManager.flush();

    }

    private static String generateLargeString() {
        return String.join("", Collections.nCopies(550, "a"));
    }

    @Nested
    @DisplayName("Constructor Tests")
    @Tag("Constructor")
    class constructorTests {

        @Test
        @DisplayName("String-User-String, valid parameters")
        public void constructorStringUserString() {

            Story testStory = new Story("validName", author, "validContent");

            assertAll(
                    () -> assertNull(testStory.getId(),
                            "ID should be null before persisting"),

                    () -> assertNull(testStory.getCreationDate(),
                            "Creation date should be null before persisting"),

                    () -> assertEquals(StoryState.CREATED, testStory.getState(),
                            "State should be CREATED before persisting")
            );

            entityManager.persistAndFlush(testStory);

            assertAll(
                    () -> assertNotNull(testStory.getId(),
                            "ID should not be null after persisting"),

                    () -> assertNotNull(testStory.getCreationDate(),
                            "Creation date should not be null after persisting"),

                    () -> assertEquals(StoryState.CREATED, testStory.getState(),
                            "State should be CREATED after persisting")
            );

        }

        @Test
        @DisplayName("String-User-String-Set, valid parameters")
        public void constructorStringUserStringSet() {

            Set<Topic> topicSet = new HashSet<>();
            topicSet.add(topic);

            Story testStory = new Story("validName", author, "validContent", topicSet);

            assertAll(
                    () -> assertNull(testStory.getId(),
                            "ID should be null before persisting"),

                    () -> assertNull(testStory.getCreationDate(),
                            "Creation date should be null before persisting"),

                    () -> assertEquals(StoryState.CREATED, testStory.getState(),
                            "State should be CREATED before persisting")
            );

            entityManager.persistAndFlush(testStory);
            entityManager.refresh(topic);

            assertAll(
                    () -> assertNotNull(testStory.getId(),
                            "ID should not be null after persistence"),

                    () -> assertNotNull(testStory.getCreationDate(),
                            "Creation date should not be null after persistence"),

                    () -> assertEquals(StoryState.CREATED, testStory.getState(),
                            "Story state must be equal to CREATED after persistence"),

                    () -> assertEquals(1, testStory.getTopics().size(),
                            "Story should be associated with the Topic provided"),

                    () -> assertEquals(1, topic.getStories().size(),
                            "Topic should be associated with the Story provided")
            );

        }

        @Test
        @DisplayName("String-User-String-Topic, valid parameters")
        public void constructorStringUserStringTopic() {

            Story testStory = new Story("validName", author, "validContent", topic);

            assertAll(
                    () -> assertNull(testStory.getId(),
                            "ID should be null before persisting"),

                    () -> assertNull(testStory.getCreationDate(),
                            "Creation date should be null before persisting"),

                    () -> assertEquals(StoryState.CREATED, testStory.getState(),
                            "State should be CREATED before persisting")
            );

            entityManager.persistAndFlush(testStory);
            entityManager.refresh(topic);

            assertAll(
                    () -> assertNotNull(testStory.getId(),
                            "ID should not be null after persistence"),

                    () -> assertNotNull(testStory.getCreationDate(),
                            "Creation date should not be null after persistence"),

                    () -> assertEquals(StoryState.CREATED, testStory.getState(),
                            "Story state must be equal to CREATED after persistence"),

                    () -> assertEquals(1, testStory.getTopics().size(),
                            "Story should be associated with the Topic provided"),

                    () -> assertEquals(1, topic.getStories().size(),
                            "Topic should be associated with the Story provided")
            );

        }

        @ParameterizedTest
        @MethodSource("invalidNameAndContentGenerator")
        @DisplayName("Invalid name and content")
        public void constructorsInvalidNameAndContent(String name, String content) {

            Set<Topic> topicsList = new HashSet<>();
            topicsList.add(topic);

            Story testStoryConstructor1 = new Story(name, author, content);
            Story testStoryConstructor2 = new Story(name, author, content, topicsList);
            Story testStoryConstructor3 = new Story(name, author, content, topic);

            // Constraint Violation exception is thrown, except when the name is duplicate where
            // hibernate constraint violation exception is thrown
            assertAll(
                    () -> assertThrows(RuntimeException.class, () -> entityManager.persist(testStoryConstructor1)),
                    () -> assertThrows(RuntimeException.class, () -> entityManager.persist(testStoryConstructor2)),
                    () -> assertThrows(RuntimeException.class, () -> entityManager.persist(testStoryConstructor3))
            );


        }

        private static Stream<Arguments> invalidNameAndContentGenerator() {

            return Stream.of(
                    arguments(null, "validContent"),
                    arguments("", "validContent"),
                    arguments("   ", "validContent"),
                    arguments("testStory", "validContent"),
                    arguments(generateLargeString(), "validContent" ),

                    arguments("validName", null),
                    arguments("validName", ""),
                    arguments("validName", "   ")
            );
        }

        @ParameterizedTest
        @MethodSource("invalidAuthorGenerator")
        @DisplayName("Null and non-existing author")
        public void constructorsInvalidAuthor(User testAuthor) {

            Set<Topic> topicsList = new HashSet<>();
            topicsList.add(topic);

            Story testStoryConstructor1 = new Story("validName", testAuthor, "validContent");
            Story testStoryConstructor2 = new Story("validName", testAuthor, "validContent", topicsList);
            Story testStoryConstructor3 = new Story("validName", testAuthor, "validContent", topic);

            assertAll(
                    () -> assertThrows(RuntimeException.class, () -> entityManager.persist(testStoryConstructor1)),
                    () -> assertThrows(RuntimeException.class, () -> entityManager.persist(testStoryConstructor2)),
                    () -> assertThrows(RuntimeException.class, () -> entityManager.persist(testStoryConstructor3))
            );

        }

        private static Stream<User> invalidAuthorGenerator() {
            return Stream.of(
                    null,
                    new User("NonExistingAuthor", "password", UserType.CURATOR)
            );
        }

        @Test
        @DisplayName("Null Topic")
        public void constructorsNullTopic() {

            Set<Topic> topicsList = new HashSet<>();
            topicsList.add(null);

            Story testStoryConstructor2 = new Story("validName2", author, "validContent", topicsList);
            entityManager.persistAndFlush(testStoryConstructor2);
            entityManager.refresh(testStoryConstructor2);

            assertAll(
                    () -> assertThrows(RuntimeException.class,
                            () -> new Story("validName3", author, "validContent", (Topic) null),
                            "An exception should be thrown when Topic is null"),

                    () -> assertTrue(testStoryConstructor2.getTopics().isEmpty(),
                            "Topics list should be empty since a null topic was provided")
            );

        }

        @Test
        @DisplayName("Non-existing Topic")
        public void constructorsInvalidTopic() {

            Topic invalidTopic = new Topic("InvalidTopic", author);

            Set<Topic> topicsList = new HashSet<>();
            topicsList.add(invalidTopic);

            Story testStoryConstructor2 = new Story("validName2", author, "validContent", topicsList);
            Story testStoryConstructor3 = new Story("validName3", author, "validContent", invalidTopic);

            assertAll(
                    () -> assertThrows(IllegalStateException.class,
                            () -> entityManager.persistAndFlush(testStoryConstructor3),
                            "An exception should be thrown when Topic is non-existent"),

                    () -> assertThrows(IllegalStateException.class,
                            () -> entityManager.persistAndFlush(testStoryConstructor2),
                            "An exception should be thrown when list of Topics contains a Topic that is non-existent")
            );

        }

    }

    @Nested
    @DisplayName("Setter Method tests")
    @Tag("SetterMethods")
    class setterTests {

        private static Stream<String> nameGenerator() {
            return Stream.of(
                null,
                    "",
                    "    ",
                    "testStory",
                    generateLargeString(),
                    "Valid String"
            );
        }

        @ParameterizedTest
        @DisplayName("setName test")
        @MethodSource("nameGenerator")
        public void setNameTest(String name) {

            Story testStory = new Story("testName", author, "validContent");
            entityManager.persistAndFlush(testStory);

            if (Objects.equals(name, "Valid String")) {
                testStory.setName(name);
                entityManager.persistAndFlush(testStory);
                entityManager.refresh(testStory);

                assertEquals("Valid String", testStory.getName());
            }
            else {
                assertThrows(RuntimeException.class, () -> {
                   testStory.setName(name);
                   entityManager.persistAndFlush(testStory);
                });
            }

        }

        private static Stream<String> rejectionReasonGenerator() {
            return Stream.of(
                    null,
                    "",
                    "    ",
                    generateLargeString(),
                    "Valid String"
            );
        }

        @ParameterizedTest
        @DisplayName("setRejectionReason test")
        @MethodSource("rejectionReasonGenerator")
        public void setRejectionReasonTest(String reason) {

            Story testStory = new Story("testName", author, "validContent");
            entityManager.persistAndFlush(testStory);

            if (Objects.equals(reason, "Valid String")) {
                testStory.setRejectionReason(reason);
                entityManager.persistAndFlush(testStory);
                entityManager.refresh(testStory);

                assertEquals("Valid String", testStory.getRejectionReason());
            }
            else {
                assertThrows(RuntimeException.class, () -> {
                    testStory.setRejectionReason(reason);
                    entityManager.persistAndFlush(testStory);
                });
            }

        }

        private static Stream<String> contentGenerator() {
            return Stream.of(
                    null,
                    "",
                    "    ",
                    "Valid String"
            );
        }

        @ParameterizedTest
        @DisplayName("setContent test")
        @MethodSource("contentGenerator")
        public void setContentTest(String content) {

            Story testStory = new Story("testName", author, "validContent");
            entityManager.persistAndFlush(testStory);

            if (Objects.equals(content, "Valid String")) {
                testStory.setContent(content);
                entityManager.persistAndFlush(testStory);
                entityManager.refresh(testStory);

                assertEquals("Valid String", testStory.getContent());
            }
            else {
                assertThrows(RuntimeException.class, () -> {
                    testStory.setContent(content);
                    entityManager.persistAndFlush(testStory);
                });
            }

        }

        @Test
        @DisplayName("Set null state")
        public void setNullState() {

            story.setState(null);

            assertNull(story.getState());

            assertThrows(ConstraintViolationException.class, () ->
                    entityManager.flush()
            );

            story = entityManager.refresh(story);

            assertEquals(StoryState.CREATED, story.getState());

        }

        @ParameterizedTest
        @DisplayName("Set invalid state")
        @ValueSource(strings = {"", "   ", "invalidEnum"})
        public void setInvalidState(String invalidState) {

            assertThrows(RuntimeException.class, () -> {
                story.setState(StoryState.valueOf(invalidState));
                entityManager.flush();
            });

        }

        @ParameterizedTest
        @DisplayName("Set valid state")
        @EnumSource(StoryState.class)
        public void setValidState(StoryState testState) {

            story.setState(testState);
            entityManager.flush();

            assertEquals(testState, story.getState());

        }


    }

    @Nested
    @DisplayName("Entity Association tests")
    @Tag("Associations")
    class associationTests {


        @Nested
        @DisplayName("Story-Comment Association tests")
        @Tag("StoryCommentAssociation")
        class storyCommentAssociation {

        }


        @Nested
        @DisplayName("Story-Topic Association tests")
        @Tag("StoryTopicAssociation")
        class storyTopicAssociation {

            @Test
            @DisplayName("Add null topic")
            public void addNullTopic() {

                assertThrows(NullPointerException.class, () ->
                    story.addTopic(null),
                    "Exception should be thrown when associating with a null Topic"
                );

            }

            @Test
            @DisplayName("Add non-existing topic")
            public void addNonExitingTopic() {

                Topic nonExistingTopic = new Topic("name",author);
                story.addTopic(nonExistingTopic);

                assertThrows(IllegalStateException.class, () ->
                        entityManager.flush(),
                        "Exception should be thrown when associating Story with" +
                                "a Topic that does not exist"
                );

            }

            @Test
            @DisplayName("Add duplicate topic")
            public void addDuplicateTopic() {

                Topic validTopic = new Topic("name",author);
                entityManager.persistAndFlush(validTopic);

                story.addTopic(validTopic);
                entityManager.flush();

                story.addTopic(validTopic);
                entityManager.flush();
                entityManager.refresh(story);

                assertEquals(1, story.getTopics().size(),
                        "Should not be able to associate with the same Topic more than once"
                );

            }

            @Test
            @DisplayName("Add valid topic")
            public void addValidTopic() {

                story.addTopic(topic);
                entityManager.flush();

                topic = entityManager.refresh(topic);
                story = entityManager.refresh(story);

                assertAll(
                        () -> assertEquals(1, story.getTopics().size(),
                                "Story should be associated with new Topic"),

                        () -> assertEquals(1, topic.getStories().size(),
                                "Topic should be associated with new Story")
                );

            }

            @Test
            @DisplayName("Remove null topic")
            public void removeNullTopic() {

                story.addTopic(topic);
                entityManager.flush();

                topic = entityManager.refresh(topic);
                story = entityManager.refresh(story);

                story.removeTopic(null);
                entityManager.flush();

                story = entityManager.refresh(story);

                assertEquals(1, story.getTopics().size());

            }

            @Test
            @DisplayName("Remove non-existing topic")
            public void removeNonExistingTopic() {

                story.addTopic(topic);
                entityManager.flush();

                topic = entityManager.refresh(topic);
                story = entityManager.refresh(story);

                story.removeTopic(new Topic("name", author));
                entityManager.flush();

                story = entityManager.refresh(story);

                assertEquals(1, story.getTopics().size());

            }

            @Test
            @DisplayName("Remove valid topic")
            public void removeValidTopic() {

                story.addTopic(topic);
                entityManager.flush();

                topic = entityManager.refresh(topic);
                story = entityManager.refresh(story);

                story.removeTopic(topic);
                entityManager.flush();

                story = entityManager.refresh(story);
                topic = entityManager.refresh(topic);

                assertAll(
                        () -> assertTrue(story.getTopics().isEmpty(),
                                "Story should not be associated with Topic"),

                        () -> assertTrue(topic.getStories().isEmpty(),
                                "Topic should not be associated with Story")
                );

            }

        }


        @Nested
        @DisplayName("Cascading behaviour tests")
        @Tag("Cascade")
        class cascade {
            private Comment testComment;

            @BeforeEach
            public void setup() {
                testComment = new Comment(story,"validContent");
                story.addTopic(topic);
                entityManager.persistAndFlush(testComment);

                testComment = entityManager.refresh(testComment);
                story = entityManager.refresh(story);
                topic = entityManager.refresh(topic);
            }

            @Test
            @DisplayName("On Update Story, Cascade test")
            public void updateCascade() {

                assertEquals(1, story.getComments().size());
                assertEquals(1, story.getTopics().size());

                story.setName("Updated Name");
                entityManager.flush();

                testComment = entityManager.refresh(testComment);
                topic = entityManager.refresh(topic);
                story = entityManager.refresh(story);

                assertAll(
                        () -> assertEquals("Updated Name", story.getName(),
                                "Story's name should have been updated"),

                        () -> assertEquals("Updated Name", testComment.getStory().getName(),
                                "The Story associated with the Comment should have been updated"),

                        () -> assertEquals("Updated Name", topic.getStories().stream().toList().get(0).getName(),
                                "The Story associated with the Topic should have been updated")
                );
            }

            @Test
            @DisplayName("On Delete Story, Cascade Test")
            public void deleteCascade() {

                assertEquals(1, story.getComments().size());
                assertEquals(1, story.getTopics().size());

                long commentID = testComment.getId();
                long storyID = story.getId();
                long topicID = topic.getId();

                entityManager.remove(story);
                entityManager.flush();

                entityManager.clear();

                topic = entityManager.find(Topic.class, topicID);

                assertAll(
                        () -> assertNull(entityManager.find(Story.class, storyID),
                                "Story should have been deleted"),

                        () -> assertNull(entityManager.find(Comment.class, commentID),
                                "The associated Comment should have been deleted"),

                        () -> assertNotNull(topic, "Topic shouldn't have been deleted"),

                        () -> assertTrue(topic.getStories().isEmpty(),
                                "The Topic should no longer be associated with the Story")
                );

            }


        }


    }


}
