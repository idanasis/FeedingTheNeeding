package Project.Final.FeedingTheNeeding.Social.service;

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
        NeederTracking needer = new NeederTracking();
        needer.setId(1L);
        needer.setName("John Doe");

        when(neederTrackingRepository.findById(1L)).thenReturn(Optional.of(needer));

        NeederTracking result = neederTrackingService.getNeederById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetNeederById_NotFound() {
        when(neederTrackingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NeederTrackingNotFoundException.class, () -> neederTrackingService.getNeederById(1L));
    }

    @Test
    void testAddNeeder() {
        NeederTracking needer = new NeederTracking();
        needer.setName("Jane Doe");

        when(neederTrackingRepository.save(needer)).thenReturn(needer);

        NeederTracking result = neederTrackingService.addNeeder(needer);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
    }
}
