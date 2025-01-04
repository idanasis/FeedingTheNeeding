package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
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

public class NeedyServiceTest {

    @Mock
    private NeedyRepository needyRepository;

    @InjectMocks
    private NeedyService needyService;

    private final long NEEDY1_ID = 1L, NEEDY2_ID = 2L;
    private final String NEEDY1_FIRST_NAME = "needy1", NEEDY2_FIRST_NAME = "needy2";
    private final String NEEDY1_LAST_NAME = "needy1", NEEDY2_LAST_NAME = "needy2";
    private final String NEEDY1_PHONE_NUMBER = "0510000000", NEEDY2_PHONE_NUMBER = "0520000000";
    private final NeedyStatus NEEDY1_STATUS = NeedyStatus.PENDING;
    private final NeedyStatus NEEDY2_STATUS = NeedyStatus.APPROVED;

    private Needy needy1;
    private Needy needy2;

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
    }

    @Test
    void testGetPendingNeedy() {
        when(needyRepository.findByConfirmStatus(NeedyStatus.PENDING)).thenReturn(Arrays.asList(needy1));

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
        verify(needyRepository, times(0)).deleteById(NEEDY1_ID);
    }

    @Test
    void testGetNeedyByPhoneNumber() {
        when(needyRepository.findByPhoneNumber(NEEDY1_PHONE_NUMBER)).thenReturn(Optional.of(needy1));

        Optional<Needy> result = needyService.getNeedyByPhoneNumber(NEEDY1_PHONE_NUMBER);

        assertTrue(result.isPresent());
        assertEquals(NEEDY1_FIRST_NAME, result.get().getFirstName());
        verify(needyRepository, times(1)).findByPhoneNumber(NEEDY1_PHONE_NUMBER);
    }
}
