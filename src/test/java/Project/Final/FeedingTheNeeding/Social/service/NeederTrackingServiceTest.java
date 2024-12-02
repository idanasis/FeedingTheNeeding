package Project.Final.FeedingTheNeeding.Social.service;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.social.exception.NeederTrackingNotFoundException;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NeederTrackingServiceTest {

    @InjectMocks
    private NeederTrackingService neederTrackingService; // Service under test

    @Mock
    private NeederTrackingRepository neederTrackingRepository; // Mocked repository

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testGetNeederById_Success() {
        // Arrange
        Needy needy=new Needy();
        needy.setId(1L);
        needy.setFamilySize(4);
        needy.setFirstName("John");
        needy.setLastName("Doe");
        needy.setPhoneNumber("123456789");

        NeederTracking mockNeederTracking = new NeederTracking();
        mockNeederTracking.setId(1L);
        mockNeederTracking.setNeedy(needy);
        mockNeederTracking.setDietaryPreferences("Vegetarian");
        mockNeederTracking.setAdditionalNotes("Requires delivery before noon");


        when(neederTrackingRepository.findById(1L)).thenReturn(Optional.of(mockNeederTracking));

        // Act
        NeederTracking result = neederTrackingService.getNeederTrackById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Vegetarian", result.getDietaryPreferences());
        assertEquals("Requires delivery before noon", result.getAdditionalNotes());
        assertEquals(4, result.getNeedy().getFamilySize());
        assertEquals("John", result.getNeedy().getFirstName());
        assertEquals("Doe", result.getNeedy().getLastName());
        assertEquals("123456789", result.getNeedy().getPhoneNumber());
        verify(neederTrackingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetNeederById_NotFound() {
        // Arrange
        when(neederTrackingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NeederTrackingNotFoundException.class, () -> neederTrackingService.getNeederTrackById(1L));
        verify(neederTrackingRepository, times(1)).findById(1L);
    }

    @Test
    void testAddNeeder() {
        // Arrange
        NeederTracking newNeederTracking = new NeederTracking();
        when(neederTrackingRepository.save(any(NeederTracking.class))).thenReturn(newNeederTracking);

        // Act
        NeederTracking result = neederTrackingService.addNeederTracking(newNeederTracking);

        // Assert
        assertNotNull(result);
        verify(neederTrackingRepository, times(1)).save(newNeederTracking);
    }
}
