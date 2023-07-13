package gr.aegean.icsd.newspaperapp.entity;

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


}
