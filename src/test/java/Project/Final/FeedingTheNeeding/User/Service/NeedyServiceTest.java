package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NeedyServiceTest {

    @Mock
    private NeedyRepository needyRepository;

    @Mock
    private NeederTrackingService neederTrackingService;

    @InjectMocks
    private NeedyService needyService;

    private static final long NEEDY1_ID = 1L;
    private static final long NEEDY2_ID = 2L;
    private static final long NEEDY3_ID = 3L;
    private static final String NEEDY1_FIRST_NAME = "Alice";
    private static final String NEEDY1_LAST_NAME = "Smith";
    private static final String NEEDY1_PHONE_NUMBER = "0510000000";
    private static final NeedyStatus NEEDY1_STATUS = NeedyStatus.PENDING;

    private static final String NEEDY2_FIRST_NAME = "Bob";
    private static final String NEEDY2_LAST_NAME = "Johnson";
    private static final String NEEDY2_PHONE_NUMBER = "0520000000";
    private static final NeedyStatus NEEDY2_STATUS = NeedyStatus.APPROVED;

    private static final String NEEDY3_FIRST_NAME = "Charlie";
    private static final String NEEDY3_LAST_NAME = "Brown";
    private static final String NEEDY3_PHONE_NUMBER = "0530000000";
    private static final NeedyStatus NEEDY3_STATUS = NeedyStatus.APPROVED;

    private static final LocalDate TRACKING_DATE = LocalDate.of(2025, 1, 1);

    private Needy needy1;
    private Needy needy2;
    private Needy needy3;
    private NeederTracking tracking1;
    private NeederTracking tracking2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        needy1 = new Needy();
        needy1.setId(NEEDY1_ID);
        needy1.setFirstName(NEEDY1_FIRST_NAME);
        needy1.setLastName(NEEDY1_LAST_NAME);
        needy1.setPhoneNumber(NEEDY1_PHONE_NUMBER);
        needy1.setConfirmStatus(NEEDY1_STATUS);

        needy2 = new Needy();
        needy2.setId(NEEDY2_ID);
        needy2.setFirstName(NEEDY2_FIRST_NAME);
        needy2.setLastName(NEEDY2_LAST_NAME);
        needy2.setPhoneNumber(NEEDY2_PHONE_NUMBER);
        needy2.setConfirmStatus(NEEDY2_STATUS);

        needy3 = new Needy();
        needy3.setId(NEEDY3_ID);
        needy3.setFirstName(NEEDY3_FIRST_NAME);
        needy3.setLastName(NEEDY3_LAST_NAME);
        needy3.setPhoneNumber(NEEDY3_PHONE_NUMBER);
        needy3.setConfirmStatus(NEEDY3_STATUS);

        tracking1 = new NeederTracking();
        tracking1.setNeedy(needy2);
        tracking1.setDate(TRACKING_DATE);

        tracking2 = new NeederTracking();
        tracking2.setNeedy(needy2);
        tracking2.setDate(TRACKING_DATE);
    }

    @Test
    void testGetPendingNeedy() {
        when(needyRepository.findByConfirmStatus(NeedyStatus.PENDING)).thenReturn(Collections.singletonList(needy1));

        List<Needy> result = needyService.getPendingNeedy();

        assertEquals(1, result.size());
        assertEquals(NEEDY1_FIRST_NAME, result.get(0).getFirstName());
        verify(needyRepository, times(1)).findByConfirmStatus(NeedyStatus.PENDING);
    }

    @Test
    void testSaveOrUpdateNeedy() {
        when(needyRepository.save(needy1)).thenReturn(needy1);

        Needy result = needyService.saveOrUpdateNeedy(needy1);

        assertNotNull(result);
        assertEquals(NEEDY1_FIRST_NAME, result.getFirstName());
        verify(needyRepository, times(1)).save(needy1);
    }

    @Test
    void testGetAllNeedyUsers() {
        when(needyRepository.findAll()).thenReturn(Arrays.asList(needy1, needy2));

        List<Needy> result = needyService.getAllNeedyUsers();

        assertEquals(2, result.size());
        verify(needyRepository, times(1)).findAll();
    }

    @Test
    void testGetNeedyById() {
        when(needyRepository.findById(NEEDY1_ID)).thenReturn(Optional.of(needy1));

        Optional<Needy> result = needyService.getNeedyById(NEEDY1_ID);

        assertTrue(result.isPresent());
        assertEquals(NEEDY1_FIRST_NAME, result.get().getFirstName());
        verify(needyRepository, times(1)).findById(NEEDY1_ID);
    }

    @Test
    void testDeleteNeedyById_Success() {
        when(needyRepository.existsById(NEEDY1_ID)).thenReturn(true);
        doNothing().when(needyRepository).deleteById(NEEDY1_ID);

        needyService.deleteNeedyById(NEEDY1_ID);

        verify(needyRepository, times(1)).existsById(NEEDY1_ID);
        verify(needyRepository, times(1)).deleteById(NEEDY1_ID);
    }

    @Test
    void testDeleteNeedyById_Failure() {
        when(needyRepository.existsById(NEEDY1_ID)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            needyService.deleteNeedyById(NEEDY1_ID);
        });
        assertEquals("Needy user with ID 1 does not exist.", exception.getMessage());
        verify(needyRepository, times(1)).existsById(NEEDY1_ID);
        verify(needyRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testGetNeedyByPhoneNumber() {
        when(needyRepository.findByPhoneNumber(NEEDY1_PHONE_NUMBER)).thenReturn(Optional.of(needy1));

        Optional<Needy> result = needyService.getNeedyByPhoneNumber(NEEDY1_PHONE_NUMBER);

        assertTrue(result.isPresent());
        assertEquals(NEEDY1_FIRST_NAME, result.get().getFirstName());
        verify(needyRepository, times(1)).findByPhoneNumber(NEEDY1_PHONE_NUMBER);
    }

    @Test
    void testGetNeedyUsersTrackingByData_AllApprovedHaveTracking() {
        when(neederTrackingService.getAllNeedersTrackingsByDate(TRACKING_DATE))
                .thenReturn(Collections.singletonList(tracking1))
                .thenReturn(Collections.singletonList(tracking1));

        when(needyRepository.findAll()).thenReturn(Arrays.asList(needy2));

        List<NeederTracking> result = needyService.getNeedyUsersTrackingByData(TRACKING_DATE);

        assertEquals(1, result.size());
        verify(neederTrackingService, times(2)).getAllNeedersTrackingsByDate(TRACKING_DATE);
        verify(needyRepository, times(1)).findAll();
        verify(neederTrackingService, times(0)).addNeederTracking(any(Needy.class), any(LocalDate.class));
    }

    @Test
    void testGetNeedyUsersTrackingByData_SomeApprovedMissingTracking() {
        when(neederTrackingService.getAllNeedersTrackingsByDate(TRACKING_DATE))
                .thenReturn(Collections.singletonList(tracking1))
                .thenReturn(Arrays.asList(tracking1, tracking2));

        when(needyRepository.findAll()).thenReturn(Arrays.asList(needy2, needy3));

        List<NeederTracking> result = needyService.getNeedyUsersTrackingByData(TRACKING_DATE);

        ArgumentCaptor<Needy> needyCaptor = ArgumentCaptor.forClass(Needy.class);
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(neederTrackingService, times(1)).addNeederTracking(needy3, TRACKING_DATE);
        verify(neederTrackingService, times(2)).getAllNeedersTrackingsByDate(TRACKING_DATE);
        assertEquals(2, result.size());
    }

    @Test
    void testGetNeedyUsersTrackingByData_NoApprovedNeedy() {
        when(neederTrackingService.getAllNeedersTrackingsByDate(TRACKING_DATE))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        when(needyRepository.findAll()).thenReturn(Collections.emptyList());

        List<NeederTracking> result = needyService.getNeedyUsersTrackingByData(TRACKING_DATE);

        assertTrue(result.isEmpty());
        verify(neederTrackingService, times(2)).getAllNeedersTrackingsByDate(TRACKING_DATE);
        verify(needyRepository, times(1)).findAll();
        verify(neederTrackingService, times(0)).addNeederTracking(any(Needy.class), any(LocalDate.class));
    }
}
