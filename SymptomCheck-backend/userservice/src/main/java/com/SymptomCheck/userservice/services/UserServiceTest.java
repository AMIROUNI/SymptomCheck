package com.SymptomCheck.userservice.services;

import com.SymptomCheck.userservice.models.User;
import com.SymptomCheck.userservice.repositories.UserRepository;
import com.SymptomCheck.userservice.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_shouldReturnSavedUser() {
        // Arrange
        User user = new User();
        user.setUsername("yasser");
        user.setPasswordHash("12345");
        user.setEmail("yasser@example.com");
        user.setRole(UserRole.PATIENT);
        user.setCreatedAt(Instant.now());

        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.saveUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("yasser", result.getUsername());
        assertEquals("yasser@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveUser_nullUser_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(null));
    }

    @Test
    void getAllUsers_shouldReturnList() {
        User user1 = new User();
        user1.setUsername("a");
        User user2 = new User();
        user2.setUsername("b");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("yasser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("yasser", result.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        User user = new User();
        user.setEmail("yasser@example.com");

        when(userRepository.findByEmail("yasser@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail("yasser@example.com");

        assertTrue(result.isPresent());
        assertEquals("yasser@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("yasser@example.com");
    }

    @Test
    void deleteUser_shouldCallRepository() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
