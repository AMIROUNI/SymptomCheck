package com.SymptomCheck.userservice.unit.services;

import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.repositories.UserDataRepository;
import com.SymptomCheck.userservice.services.UserDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataServiceTest {

    @Mock
    private UserDataRepository userDataRepository;

    @InjectMocks
    private UserDataService userDataService;

    private UserData mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserData();
        mockUser.setId("abc123");
        mockUser.setPhoneNumber("555-1234");
        mockUser.setProfilePhotoUrl("/profiles/abc.jpg");
        mockUser.setProfileComplete(true);
        mockUser.setClinicId(10L);
        mockUser.setCreatedAt(Instant.now());
        mockUser.setUpdatedAt(Instant.now());
        mockUser.setSpeciality("Cardiology");
        mockUser.setDescription("Experienced heart specialist");
        mockUser.setDiploma("Medical Doctor");
    }

    @Nested
    @DisplayName("getUserDataById tests")
    class GetUserDataByIdTests {

        @Test
        @DisplayName("should return user data when found")
        void testGetUserDataById_Success() {
            // Arrange
            String id = "abc123";
            when(userDataRepository.findById(id))
                    .thenReturn(Optional.of(mockUser));

            // Act
            Optional<UserData> result = userDataService.getUserDataById(id);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("abc123", result.get().getId());
            assertEquals("Cardiology", result.get().getSpeciality());
            assertTrue(result.get().getProfileComplete());

            verify(userDataRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("should return empty when user not found")
        void testGetUserDataById_NotFound() {
            // Arrange
            String id = "notfound";
            when(userDataRepository.findById(id))
                    .thenReturn(Optional.empty());

            // Act
            Optional<UserData> result = userDataService.getUserDataById(id);

            // Assert
            assertFalse(result.isPresent());
            verify(userDataRepository, times(1)).findById(id);
        }
    }
}
