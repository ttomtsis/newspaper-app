package gr.aegean.icsd.newspaperapp.entity;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Comment Entity
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentTest {

    @Autowired
    private TestEntityManager entityManager;

    private Story story;

    private User author;

    private Comment comment;

    private final static Logger log = LoggerFactory.getLogger("### CommentTest ###");

    public CommentTest() {}

    @BeforeEach
    public void initialize() {

        author = new User("testName", "testPassword", UserType.CURATOR);
        story = new Story("testStory", author, "testContent");
        comment = new Comment(story, "validContent", author);

        entityManager.persist(author);
        entityManager.persist(story);
        entityManager.persist(comment);
        entityManager.flush();

    }

    private static String generateLargeString() {
        return String.join("", Collections.nCopies(550, "a"));
    }

    private static Stream<String> contentGenerator() {
        return Stream.of(
                null,
                "",
                "    ",
                generateLargeString()
        );
    }


    @Nested
    @DisplayName("Constructor Tests")
    @Tag("Constructor")
    class constructorTests {

        @Test
        @DisplayName("Constructor Story, String, User - Valid parameters")
        public void constructorStoryStrUserValid() {

            Comment testComment = new Comment(story, "validContent", author);

            assertAll(
                    () -> assertNull(testComment.getId(),
                            "ID should be null before persisting"),

                    () -> assertNull(testComment.getCreationDate(),
                            "Creation date should be null before persisting"),

                    () -> assertEquals(CommentState.SUBMITTED, testComment.getState(),
                            "State should be SUBMITTED before persisting")
            );

            entityManager.persistAndFlush(testComment);

            assertAll(
                    () -> assertNotNull(testComment.getId(),
                            "ID should not be null after persisting"),

                    () -> assertNotNull(testComment.getCreationDate(),
                            "Creation date should not be null after persisting"),

                    () -> assertEquals(CommentState.SUBMITTED, testComment.getState(),
                            "State should be SUBMITTED after persisting")
            );

        }

        @Test
        @DisplayName("Constructor Story, String - Valid parameters")
        public void constructorStoryStrValid() {

            Comment testComment = new Comment(story, "validContent");

            assertAll(
                    () -> assertNull(testComment.getId(),
                            "ID should be null before persisting"),

                    () -> assertNull(testComment.getCreationDate(),
                            "Creation date should be null before persisting"),

                    () -> assertEquals(CommentState.SUBMITTED, testComment.getState(),
                            "State should be SUBMITTED before persisting")
            );

            entityManager.persistAndFlush(testComment);

            assertAll(
                    () -> assertNotNull(testComment.getId(),
                            "ID should not be null after persisting"),

                    () -> assertNotNull(testComment.getCreationDate(),
                            "Creation date should not be null after persisting"),

                    () -> assertEquals(CommentState.SUBMITTED, testComment.getState(),
                            "State should be SUBMITTED after persisting")
            );

        }

        @ParameterizedTest
        @MethodSource("gr.aegean.icsd.newspaperapp.entity.CommentTest#contentGenerator")
        @DisplayName("Invalid content")
        public void invalidContent(String content) {

            Comment testCommentConstructor1 = new Comment(story, content, author);
            Comment testCommentConstructor2 = new Comment(story, content);

            assertAll(
                    () -> assertThrows(ConstraintViolationException.class, () ->
                            entityManager.persist(testCommentConstructor1),
                            "Constraint Violation Exception should be thrown when content is " + content),

                    () -> assertThrows(ConstraintViolationException.class, () ->
                                    entityManager.persist(testCommentConstructor2),
                            "Constraint Violation Exception should be thrown when content is " + content)
            );

        }

        @Test
        @DisplayName("Null Story")
        public void nullStory() {

            Comment testCommentConstructor1 = new Comment(null, "validContent");
            Comment testCommentConstructor2 = new Comment(null, "validContent", author);

            assertAll(
                    () -> assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                                    entityManager.persist(testCommentConstructor1),
                            "Constraint Violation Exception should be thrown when Story is null"),

                    () -> assertThrows(org.hibernate.exception.ConstraintViolationException.class, () ->
                                    entityManager.persist(testCommentConstructor2),
                            "Constraint Violation Exception should be thrown when Story is null")
            );

        }

        @Test
        @DisplayName("Non-Existing Story")
        public void nonExistingStory() {

            Story nonExistingStory = new Story("nonExistingStory", author, "validContent");

            Comment testCommentConstructor1 = new Comment(nonExistingStory, "validContent");
            Comment testCommentConstructor2 = new Comment(nonExistingStory, "validContent", author);

            assertAll(
                    () -> assertThrows(IllegalStateException.class, () ->
                                    entityManager.persist(testCommentConstructor1),
                            "Illegal State Exception should be thrown when Story does not exist"),

                    () -> assertThrows(IllegalStateException.class, () ->
                                    entityManager.persist(testCommentConstructor2),
                            "Illegal State Exception should be thrown when Story does not exist")
            );

        }

        @Test
        @DisplayName("Null Author")
        public void nullAuthor() {

            assertThrows(IllegalArgumentException.class, () -> {
                Comment testComment = new Comment(story, "validContent", null);
                entityManager.persistAndFlush(testComment);
            },
                "Null Pointer Exception should be thrown when Author is null");

        }

        @Test
        @DisplayName("Non-Existing Author")
        public void nonExistingAuthor() {

            User nonExistingAuthor = new User("nonExistingAuthor", "password", UserType.CURATOR);

            Comment testComment = new Comment(story, "validContent", nonExistingAuthor);

            assertThrows(IllegalStateException.class, () ->
                            entityManager.persistAndFlush(testComment),
                    "Illegal State Exception should be thrown when Author does not exist");

        }

    }


    @Nested
    @DisplayName("Setter Method tests")
    @Tag("SetterMethods")
    class setterTests {

        @ParameterizedTest
        @MethodSource("gr.aegean.icsd.newspaperapp.entity.CommentTest#contentGenerator")
        @DisplayName("Set invalid content test")
        public void setInvalidContent(String newContent) {

            Comment testComment = new Comment(story, "validContent");
            entityManager.persist(testComment);

            testComment.setContent(newContent);

            assertThrows(ConstraintViolationException.class, () ->
                    entityManager.flush(),
                    "Constraint Violation Exception should be thrown when content is: " + newContent);

        }

        @ParameterizedTest
        @MethodSource("gr.aegean.icsd.newspaperapp.entity.CommentTest#contentGenerator")
        @DisplayName("Set valid content test")
        public void setValidContent() {

            Comment testComment = new Comment(story, "validContent");
            entityManager.persist(testComment);

            testComment.setContent("New validContent");
            entityManager.flush();

            assertEquals("New validContent", testComment.getContent(),
                    "Content should have been updated")
            ;

        }

        @Test
        @DisplayName("Set null state")
        public void setNullState() {

            comment.setState(null);

            assertNull(comment.getState());

            assertThrows(ConstraintViolationException.class, () ->
                    entityManager.flush()
            );

            comment = entityManager.refresh(comment);

            assertEquals(CommentState.SUBMITTED, comment.getState());

        }

        @ParameterizedTest
        @DisplayName("Set invalid state")
        @ValueSource(strings = {"", "   ", "invalidEnum"})
        public void setInvalidState(String invalidState) {

            assertThrows(RuntimeException.class, () -> {
                comment.setState(CommentState.valueOf(invalidState));
                entityManager.flush();
            });

        }

        @ParameterizedTest
        @DisplayName("Set valid state")
        @EnumSource(CommentState.class)
        public void setValidState(CommentState testState) {

            comment.setState(testState);
            entityManager.flush();

            assertEquals(testState, comment.getState());

        }

    }


    @Nested
    @DisplayName("Association tests")
    @Tag("Association")
    class associationTests {

        @Test
        @DisplayName("Associate Comment with a Story and Author")
        public void createAssociation() {

            Comment testComment = new Comment(story, "validContent", author);
            entityManager.persistAndFlush(testComment);

            story = entityManager.refresh(story);
            author = entityManager.refresh(author);

            assertAll(
                    () -> assertEquals(story, testComment.getStory(),
                            "Comment should have been associated with the Comment"),

                    () -> assertTrue(story.getComments().contains(testComment),
                            "Story should have been associated with the Comment"),

                    () -> assertEquals(2, story.getComments().size(),
                            "Story should have been associated with both Comments"),

                    () -> assertTrue(author.getComments().contains(testComment),
                            "Author should have been associated with the Comment"),

                    () -> assertEquals(2, author.getComments().size(),
                            "Author should have been associated with both Comments")
            );

        }

        @Test
        @Tag("Cascade")
        @DisplayName("On Delete Comment, Cascade Test")
        public void deleteComment() {

            Comment testComment = new Comment(story, "validContent", author);
            entityManager.persistAndFlush(testComment);

            story = entityManager.refresh(story);
            author = entityManager.refresh(author);

            entityManager.remove(testComment);
            entityManager.flush();

            entityManager.clear();
            story = entityManager.find(Story.class, story.getId());
            author = entityManager.find(User.class, author.getId());

            assertAll(
                    () -> assertNull(entityManager.find(Comment.class, testComment.getId()),
                            "Comment should have been deleted"),

                    () -> assertNotNull(entityManager.find(Story.class, story.getId()),
                            "Story shouldn't have been deleted"),

                    () -> assertNotNull(entityManager.find(User.class, author.getId()),
                            "User shouldn't have been deleted"),

                    () -> assertEquals(1, story.getComments().size(),
                            "Story should no longer be associated with the Comment"),

                    () -> assertEquals(1, author.getComments().size(),
                            "Author should no longer be associated with the Comment")
            );

        }

        @Test
        @Tag("Cascade")
        @DisplayName("On Update Comment, Cascade Test")
        public void updateComment() {

            comment.setContent("New Valid Content");
            entityManager.flush();

            story = entityManager.refresh(story);
            author = entityManager.refresh(author);

            assertAll(
                    () -> assertEquals("New Valid Content", comment.getContent(),
                            "Content should have been updated"),

                    () -> assertEquals("New Valid Content", story.getComments().stream().toList().get(0).getContent(),
                            "Updated content should be reflected in the Story's Comment"),

                    () -> assertEquals("New Valid Content", author.getComments().stream().toList().get(0).getContent(),
                            "Updated content should be reflected in the User's Comment")
            );

        }

    }


}
