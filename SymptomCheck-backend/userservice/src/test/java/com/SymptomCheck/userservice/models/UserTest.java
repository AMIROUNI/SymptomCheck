package com.SymptomCheck.userservice.models;

import com.SymptomCheck.userservice.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("should set and get all fields correctly")
        void shouldSetAndGetAllFields() {
            // Given
            Long id = 1L;
            String username = "john_doe";
            String passwordHash = "hashed_password";
            UserRole role = UserRole.DOCTOR;
            String firstName = "John";
            String lastName = "Doe";
            String email = "john.doe@example.com";

            // When
            user.setId(id);
            user.setUsername(username);
            user.setPasswordHash(passwordHash);
            user.setRole(role);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);

            // Then
            assertEquals(id, user.getId());
            assertEquals(username, user.getUsername());
            assertEquals(passwordHash, user.getPasswordHash());
            assertEquals(role, user.getRole());
            assertEquals(firstName, user.getFirstName());
            assertEquals(lastName, user.getLastName());
            assertEquals(email, user.getEmail());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create user with all-args constructor")
        void shouldCreateUserWithAllArgsConstructor() {
            // Given
            Long id = 2L;
            String username = "jane_smith";
            String passwordHash = "another_hash";
            UserRole role = UserRole.PATIENT;
            String firstName = "Jane";
            String lastName = "Smith";
            String email = "jane.smith@example.com";

            // When
            User newUser = new User(id, username, passwordHash, role, firstName, lastName, email);

            // Then
            assertEquals(id, newUser.getId());
            assertEquals(username, newUser.getUsername());
            assertEquals(passwordHash, newUser.getPasswordHash());
            assertEquals(role, newUser.getRole());
            assertEquals(firstName, newUser.getFirstName());
            assertEquals(lastName, newUser.getLastName());
            assertEquals(email, newUser.getEmail());
        }
    }

    @Nested
    @DisplayName("Default Value Tests")
    class DefaultValueTests {

        @Test
        @DisplayName("should have PATIENT as default role")
        void shouldHavePatientAsDefaultRole() {
            // When creating with no-args constructor
            User newUser = new User();

            // Then
            assertEquals(UserRole.PATIENT, newUser.getRole());
        }

        @Test
        @DisplayName("should override default role when set explicitly")
        void shouldOverrideDefaultRole() {
            // When
            user.setRole(UserRole.ADMIN);

            // Then
            assertEquals(UserRole.ADMIN, user.getRole());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {


        @Test
        @DisplayName("should not be equal when IDs differ")
        void shouldNotBeEqualWhenIdsDiffer() {
            // Given
            User user1 = new User();
            user1.setId(1L);

            User user2 = new User();
            user2.setId(2L);

            // Then
            assertNotEquals(user1, user2);
        }
    }


}