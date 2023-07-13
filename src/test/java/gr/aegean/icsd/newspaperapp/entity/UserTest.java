package gr.aegean.icsd.newspaperapp.entity;

import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
@DisplayName("User Entity tests")
@Tag("Entity")
@Tag("Topic")
public class UserTest {

    @Autowired
    private TestEntityManager entityManager;

    public UserTest() {}


    @Nested
    @DisplayName("Constructor Tests")
    @Tag("Constructor")
    class constructorTests {

        @ParameterizedTest
        @EnumSource(UserType.class)
        @DisplayName("Constructor String, String, Enum - Valid parameters")
        public void constructorValidParameters(UserType type) {

            User testUser = new User("validUsername", "validPassword", type);

            assertNull(testUser.getId());

            entityManager.persistAndFlush(testUser);

            assertNotNull(testUser.getId());

        }

        @Test
        @DisplayName("No parameters constructor")
        public void constructorNoParameters() {

            assertThrows(ConstraintViolationException.class, () -> {
               User testUser = new User();
               entityManager.persistAndFlush(testUser);
            });

        }

        private static String generateString(int size) {
            return String.join("", Collections.nCopies(size, "a"));
        }

        private static Stream<String> usernameGenerator() {
            return Stream.of(
                    null,
                    "",
                    "   ",
                    generateString(2),
                    generateString(50)
            );
        }

        @ParameterizedTest
        @MethodSource("usernameGenerator")
        @DisplayName("Invalid username")
        public void invalidUsername(String username) {

            assertThrows(ConstraintViolationException.class, () -> {
               User testUser = new User(username, "validPassword", UserType.CURATOR);
               entityManager.persistAndFlush(testUser);
            });

        }

        @Test
        @DisplayName("Duplicate username")
        public void duplicateUsername() {

            User duplicateUser = new User("duplicateUser", "validPassword", UserType.CURATOR);
            entityManager.persistAndFlush(duplicateUser);

            assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
                User testUser = new User("duplicateUser", "validPassword", UserType.CURATOR);
                entityManager.persistAndFlush(testUser);
            });

        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "aa"})
        @DisplayName("Invalid password")
        public void invalidPassword(String password) {

            assertThrows(ConstraintViolationException.class, () -> {
                User testUser = new User("validUsername", password, UserType.CURATOR);
                entityManager.persistAndFlush(testUser);
            });

        }

        @Test
        @DisplayName("Null role")
        public void nullRole() {

            assertThrows(ConstraintViolationException.class, () -> {
                User testUser = new User("validUsername", "validPassword", null);
                entityManager.persistAndFlush(testUser);
            });

        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "InvalidRole"})
        @DisplayName("Invalid role")
        public void invalidRole(String role) {

            assertThrows(IllegalArgumentException.class, () -> {
                User testUser = new User("validUsername", "validPassword", UserType.valueOf(role));
                entityManager.persistAndFlush(testUser);
            });

        }

    }


    @Nested
    @DisplayName("Association Tests")
    @Tag("Association")
    @Disabled
    class associationTests {

        @Test
        @Tag("Cascade")
        @DisplayName("On Delete User Cascade")
        public void onDeleteUserCascade() {

            User testUser = new User("authorName", "validPassword", UserType.CURATOR);

            Story testStory = new Story("storyName", testUser, "content");
            Topic testTopic = new Topic("topicName", testUser);
            Comment testComment = new Comment(testStory, "validContent", testUser);

            entityManager.persist(testUser);
            entityManager.persist(testStory);
            entityManager.persist(testTopic);
            entityManager.persist(testComment);

            entityManager.flush();

            User refreshedTestUser = entityManager.refresh(testUser);
            Story refreshedTestStory = entityManager.refresh(testStory);
            Topic refreshedtestTopic = entityManager.refresh(testTopic);
            Comment refreshedtestComment = entityManager.refresh(testComment);

            // Ensure relationships exist
            assertAll(
                    () -> assertTrue(refreshedTestUser.getComments().contains(refreshedtestComment),
                            "User should be associated with the Comment"),

                    () -> assertTrue(refreshedTestUser.getStories().contains(refreshedTestStory),
                            "User should be associated with the Story"),

                    () -> assertTrue(refreshedTestUser.getTopics().contains(refreshedtestTopic),
                            "User should be associated with the Topic"),

                    () -> assertEquals(refreshedTestUser, refreshedTestStory.getAuthor(),
                            "Story should be associated with the User"),

                    () -> assertEquals(refreshedTestUser, refreshedtestTopic.getAuthor(),
                    "Topic should be associated with the User"),

                    () -> assertEquals(refreshedTestUser, refreshedtestComment.getAuthor().get(),
                            "Comment should be associated with the User")
            );

            entityManager.remove(testUser);
            entityManager.flush();

            entityManager.clear();

            assertAll(
                    () -> assertNull(entityManager.find(User.class, testUser.getId()),
                            "User should not exist"),

                    () -> assertNotNull(entityManager.find(Story.class, testStory.getId()),
                            "Story shouldn't be deleted"),

                    () -> assertNotNull(entityManager.find(Topic.class, testTopic.getId()),
                            "Topic shouldn't be deleted"),

                    () -> assertNull(entityManager.find(Comment.class, testComment.getId()),
                            "Comment should not exist"),

                    () -> assertNull(entityManager.find(Story.class, testStory.getId()).getAuthor(),
                            "Story's Author should be null"),

                    () -> assertNull(entityManager.find(Topic.class, testTopic.getId()).getAuthor(),
                            "Topic's Author should be null")
            );

        }

    }


}
