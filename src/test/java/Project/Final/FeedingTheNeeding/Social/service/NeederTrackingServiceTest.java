package Project.Final.FeedingTheNeeding.Social.service;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.social.exception.NeederTrackingNotFoundException;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.model.WeekStatus;
import Project.Final.FeedingTheNeeding.social.projection.NeederTrackingProjection;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
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
    @Test
    void testUpdateNeederTrack_Success() {
        // Arrange
        Long existingId = 1L;
        Needy existingNeedy = new Needy();
        existingNeedy.setId(existingId);
        existingNeedy.setFirstName("Old Name");

        NeederTracking existingTracking = new NeederTracking();
        existingTracking.setId(existingId);
        existingTracking.setNeedy(existingNeedy);
        existingTracking.setDietaryPreferences("Old Preference");

        Needy updatedNeedy = new Needy();
        updatedNeedy.setId(existingId);
        updatedNeedy.setFirstName("New Name");

        NeederTracking updatedTracking = new NeederTracking();
        updatedTracking.setNeedy(updatedNeedy);
        updatedTracking.setDietaryPreferences("New Preference");
        updatedTracking.setAdditionalNotes("Updated Notes");

        when(neederTrackingRepository.findById(existingId)).thenReturn(Optional.of(existingTracking));
        when(neederTrackingRepository.save(any(NeederTracking.class))).thenReturn(updatedTracking);

        // Act
        NeederTracking result = neederTrackingService.updateNeederTrack(existingId, updatedTracking);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getNeedy().getFirstName());
        assertEquals("New Preference", result.getDietaryPreferences());
        verify(neederTrackingRepository, times(1)).findById(existingId);
        verify(neederTrackingRepository, times(1)).save(any(NeederTracking.class));
    }

    @Test
    void testUpdateNeederTrack_NotFound() {
        // Arrange
        Long nonExistentId = 999L;
        NeederTracking updatedTracking = new NeederTracking();

        when(neederTrackingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NeederTrackingNotFoundException.class,
                () -> neederTrackingService.updateNeederTrack(nonExistentId, updatedTracking));
    }

    @Test
    void testDeleteNeederTrack_Success() {
        // Arrange
        Long existingId = 1L;
        when(neederTrackingRepository.existsById(existingId)).thenReturn(true);

        // Act
        neederTrackingService.deleteNeederTrack(existingId);

        // Assert
        verify(neederTrackingRepository, times(1)).deleteById(existingId);
        verify(neederTrackingRepository, times(1)).existsById(existingId);

    }

    @Test
    void testDeleteNeederTrack_NotFound() {
        // Arrange
        Long nonExistentId = 999L;
        when(neederTrackingRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        assertThrows(NeederTrackingNotFoundException.class,
                () -> neederTrackingService.deleteNeederTrack(nonExistentId));
    }

    @Test
    void testGetAllNeedersTrackings() {
        // Arrange
        List<NeederTracking> mockTrackings = Arrays.asList(
                new NeederTracking(),
                new NeederTracking()
        );
        when(neederTrackingRepository.findAll()).thenReturn(mockTrackings);

        // Act
        List<NeederTracking> result = neederTrackingService.getAllNeedersTrackings();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(neederTrackingRepository, times(1)).findAll();
    }

    @Test
    void testGetNeedersHere() {
        // Arrange
        List<NeederTrackingProjection> mockProjections = Arrays.asList(
                mock(NeederTrackingProjection.class),
                mock(NeederTrackingProjection.class)
        );

        when(neederTrackingRepository.findByWeekStatus(WeekStatus.Here)).thenReturn(mockProjections);

        // Act
        List<NeederTrackingProjection> result = neederTrackingService.getNeedersHere();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(neederTrackingRepository, times(1)).findByWeekStatus(WeekStatus.Here);
    }
}
