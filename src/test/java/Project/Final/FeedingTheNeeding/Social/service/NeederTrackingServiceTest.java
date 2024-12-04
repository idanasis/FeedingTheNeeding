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

    private static final long NEEDY_ID = 1L;
    private static final int FAMILY_SIZE = 4;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String PHONE_NUMBER = "123456789";
    private static final String DIETARY_PREFERENCES = "Vegetarian";
    private static final String ADDITIONAL_NOTES = "Requires delivery before noon";

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
        Needy needy = new Needy();
        needy.setId(NEEDY_ID);
        needy.setFamilySize(FAMILY_SIZE);
        needy.setFirstName(FIRST_NAME);
        needy.setLastName(LAST_NAME);
        needy.setPhoneNumber(PHONE_NUMBER);

        NeederTracking mockNeederTracking = new NeederTracking();
        mockNeederTracking.setId(NEEDY_ID);
        mockNeederTracking.setNeedy(needy);
        mockNeederTracking.setDietaryPreferences(DIETARY_PREFERENCES);
        mockNeederTracking.setAdditionalNotes(ADDITIONAL_NOTES);

        when(neederTrackingRepository.findById(NEEDY_ID)).thenReturn(Optional.of(mockNeederTracking));

        // Act
        NeederTracking result = neederTrackingService.getNeederTrackById(NEEDY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(NEEDY_ID, result.getId());
        assertEquals(DIETARY_PREFERENCES, result.getDietaryPreferences());
        assertEquals(ADDITIONAL_NOTES, result.getAdditionalNotes());
        assertEquals(FAMILY_SIZE, result.getNeedy().getFamilySize());
        assertEquals(FIRST_NAME, result.getNeedy().getFirstName());
        assertEquals(LAST_NAME, result.getNeedy().getLastName());
        assertEquals(PHONE_NUMBER, result.getNeedy().getPhoneNumber());
        verify(neederTrackingRepository, times(1)).findById(NEEDY_ID);
    }

    @Test
    void testGetNeederById_NotFound() {
        // Arrange
        when(neederTrackingRepository.findById(NEEDY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NeederTrackingNotFoundException.class, () -> neederTrackingService.getNeederTrackById(NEEDY_ID));
        verify(neederTrackingRepository, times(1)).findById(NEEDY_ID);
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
