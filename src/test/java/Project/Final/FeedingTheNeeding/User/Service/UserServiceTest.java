package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private NeedyRepository needyRepository;

    @InjectMocks
    private UserService userService;

    private Donor donor1;
    private Donor donor2;
    private Needy needy1;
    private Needy needy2;

    private static final long DONOR1_ID = 1L;
    private static final long NEEDY1_ID = 2L;
    private static final long DONOR2_ID = 10L;
    private static final long NEEDY2_ID = 20L;
    private static final String DONOR1_PHONE_NUMBER = "0500000000";
    private static final String NEEDY1_PHONE_NUMBER = "0500000001";
    private static final String DONOR2_PHONE_NUMBER = "0500000002";
    private static final String NEEDY2_PHONE_NUMBER = "0500000003";
    private static final String DONOR1_EMAIL = "donor1Email@gmail.com";
    private static final String DONOR2_EMAIL = "donor2Email@gmail.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        donor1 = new Donor();
        donor2 = new Donor();
        needy1 = new Needy();
        needy2 = new Needy();

        donor1.setId(DONOR1_ID);
        donor1.setPhoneNumber(DONOR1_PHONE_NUMBER);
        donor1.setEmail(DONOR1_EMAIL);
        donor1.setStatus(RegistrationStatus.PENDING);
        donor2.setId(DONOR2_ID);
        donor2.setPhoneNumber(DONOR2_PHONE_NUMBER);
        donor2.setEmail(DONOR2_EMAIL);
        donor2.setStatus(RegistrationStatus.AVAILABLE);
        needy1.setId(NEEDY1_ID);
        needy1.setPhoneNumber(NEEDY1_PHONE_NUMBER);
        needy2.setId(NEEDY2_ID);
        needy2.setPhoneNumber(NEEDY2_PHONE_NUMBER);
    }

    @Test
    void testGetAllDonors(){
        when(donorRepository.findAll()).thenReturn(Arrays.asList(donor1, donor2));
        List<Donor> donors = userService.getAllDonors();
        assertNotNull(donors);
        assertEquals(2, donors.size());
        verify(donorRepository, times(1)).findAll();
    }

    @Test
    void testGetAllNeedyUsers(){
        when(needyRepository.findAll()).thenReturn(Arrays.asList(needy1, needy2));
        List<Needy> needyUsers = userService.getAllNeedyUsers();
        assertNotNull(needyUsers);
        assertEquals(2, needyUsers.size());
        verify(needyRepository, times(1)).findAll();
    }

    @Test
    void testGetAll(){
        when(donorRepository.findAll()).thenReturn(Arrays.asList(donor1, donor2));
        when(needyRepository.findAll()).thenReturn(Arrays.asList(needy1, needy2));
        List<BaseUser> users = userService.getAll();
        assertNotNull(users);
        assertEquals(4, users.size());
        verify(donorRepository, times(1)).findAll();
        verify(needyRepository, times(1)).findAll();
    }

    @Test
    void testGetNeedyByPhoneNumber_Success(){
        when(needyRepository.findByPhoneNumber(NEEDY1_PHONE_NUMBER)).thenReturn(Optional.of(needy1));
        Needy foundNeedy = userService.getNeedyByPhoneNumber(NEEDY1_PHONE_NUMBER);
        assertNotNull(foundNeedy);
        assertEquals(NEEDY1_PHONE_NUMBER, foundNeedy.getPhoneNumber());
        verify(needyRepository, times(1)).findByPhoneNumber(NEEDY1_PHONE_NUMBER);
    }

    @Test
    void testGetNeedyByPhoneNumber_NotFound(){
        when(needyRepository.findByPhoneNumber(NEEDY2_PHONE_NUMBER)).thenReturn(Optional.empty());
        assertThrows(UserDoesntExistsException.class, () -> userService.getNeedyByPhoneNumber(NEEDY2_PHONE_NUMBER));
        verify(needyRepository, times(1)).findByPhoneNumber(NEEDY2_PHONE_NUMBER);
    }

    @Test
    void testGetDonorByPhoneNumber_Success(){
        when(donorRepository.findByPhoneNumber(DONOR1_PHONE_NUMBER)).thenReturn(Optional.of(donor1));
        Donor foundDonor = userService.getDonorByPhoneNumber(DONOR1_PHONE_NUMBER);
        assertNotNull(foundDonor);
        assertEquals(DONOR1_PHONE_NUMBER, foundDonor.getPhoneNumber());
        verify(donorRepository, times(1)).findByPhoneNumber(DONOR1_PHONE_NUMBER);
    }

    @Test
    void testGetDonorByPhoneNumber_NotFound(){
        when(donorRepository.findByPhoneNumber(DONOR2_PHONE_NUMBER)).thenReturn(Optional.empty());
        assertThrows(UserDoesntExistsException.class, () -> userService.getDonorByPhoneNumber(DONOR2_PHONE_NUMBER));
        verify(donorRepository, times(1)).findByPhoneNumber(DONOR2_PHONE_NUMBER);
    }

    @Test
    void testGetDonorById_Success(){
        when(donorRepository.findById(DONOR1_ID)).thenReturn(Optional.of(donor1));
        Donor foundDonor = userService.getDonorById(DONOR1_ID);
        assertNotNull(foundDonor);
        assertEquals(DONOR1_ID, foundDonor.getId());
        verify(donorRepository, times(1)).findById(DONOR1_ID);
    }

    @Test
    void testGetDonorById_NotFound(){
        when(donorRepository.findById(DONOR2_ID)).thenReturn(Optional.empty());
        assertThrows(UserDoesntExistsException.class, () -> userService.getDonorById(DONOR2_ID));
        verify(donorRepository, times(1)).findById(DONOR2_ID);
    }

    @Test
    void testGetDonorsPending(){
        when(donorRepository.findByStatus(RegistrationStatus.PENDING)).thenReturn(Arrays.asList(donor1));
        List<Donor> pendingDonors = userService.getDonorsPending();
        assertNotNull(pendingDonors);
        assertEquals(1, pendingDonors.size());
        assertEquals(RegistrationStatus.PENDING, pendingDonors.get(0).getStatus());
        verify(donorRepository, times(1)).findByStatus(RegistrationStatus.PENDING);
    }

    @Test
    void testGetDonorsApproved(){
        when(donorRepository.findByStatus(RegistrationStatus.AVAILABLE)).thenReturn(Arrays.asList(donor2));
        List<Donor> approvedDonors = userService.getDonorsApproved();
        assertNotNull(approvedDonors);
        assertEquals(1, approvedDonors.size());
        assertEquals(RegistrationStatus.AVAILABLE, approvedDonors.get(0).getStatus());
        verify(donorRepository, times(1)).findByStatus(RegistrationStatus.AVAILABLE);
    }

    @Test
    void testUpdateDonor_Success(){
        Donor updatedDonor = new Donor();
        updatedDonor.setId(DONOR1_ID);
        updatedDonor.setPhoneNumber("0511111111");
        updatedDonor.setFirstName("John");
        updatedDonor.setLastName("Doe");
        updatedDonor.setAddress("123 Street");
        updatedDonor.setStatus(RegistrationStatus.AVAILABLE);
        updatedDonor.setEmail("john.doe@example.com");
        updatedDonor.setRole(UserRole.DONOR);
        updatedDonor.setLastDonationDate(LocalDate.now());

        when(donorRepository.findById(DONOR1_ID)).thenReturn(Optional.of(donor1));
        when(donorRepository.save(any(Donor.class))).thenReturn(updatedDonor);

        userService.updateDonor(updatedDonor);

        verify(donorRepository, times(1)).findById(DONOR1_ID);
        verify(donorRepository, times(1)).save(donor1);

        assertEquals("0511111111", donor1.getPhoneNumber());
        assertEquals("John", donor1.getFirstName());
        assertEquals("Doe", donor1.getLastName());
        assertEquals("123 Street", donor1.getAddress());
        assertEquals(RegistrationStatus.AVAILABLE, donor1.getStatus());
        assertEquals("john.doe@example.com", donor1.getEmail());
        assertEquals(UserRole.DONOR, donor1.getRole());
        assertEquals(LocalDate.now(), donor1.getLastDonationDate());
    }

    @Test
    void testUpdateDonor_NotFound(){
        Donor updatedDonor = new Donor();
        updatedDonor.setId(DONOR2_ID);
        when(donorRepository.findById(DONOR2_ID)).thenReturn(Optional.empty());

        assertThrows(UserDoesntExistsException.class, () -> userService.updateDonor(updatedDonor));
        verify(donorRepository, times(1)).findById(DONOR2_ID);
        verify(donorRepository, times(0)).save(any(Donor.class));
    }

    @Test
    void testDeleteDonor_Success(){
        when(donorRepository.existsById(DONOR1_ID)).thenReturn(true);
        when(donorRepository.findById(DONOR1_ID)).thenReturn(Optional.of(donor1));

        userService.deleteDonor(DONOR1_ID);

        verify(donorRepository, times(1)).existsById(DONOR1_ID);
        verify(donorRepository, times(1)).findById(DONOR1_ID);
        verify(donorRepository, times(1)).save(donor1);
        verify(donorRepository, times(1)).deleteById(DONOR1_ID);

        assertNull(donor1.getUserCredentials());
    }

    @Test
    void testDeleteDonor_NotFound(){
        when(donorRepository.existsById(DONOR2_ID)).thenReturn(false);

        assertThrows(UserDoesntExistsException.class, () -> userService.deleteDonor(DONOR2_ID));
        verify(donorRepository, times(1)).existsById(DONOR2_ID);
        verify(donorRepository, times(0)).findById(anyLong());
        verify(donorRepository, times(0)).save(any(Donor.class));
        verify(donorRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testSetDonationToDonor_Success(){
        LocalDate donationDate = LocalDate.of(2025, 1, 1);
        when(donorRepository.findById(DONOR1_ID)).thenReturn(Optional.of(donor1));
        when(donorRepository.save(any(Donor.class))).thenReturn(donor1);

        userService.setDonationToDonor(DONOR1_ID, donationDate);

        verify(donorRepository, times(1)).findById(DONOR1_ID);
        verify(donorRepository, times(1)).save(donor1);
        assertEquals(donationDate, donor1.getLastDonationDate());
    }

    @Test
    void testSetDonationToDonor_NotFound(){
        LocalDate donationDate = LocalDate.of(2025, 1, 1);
        when(donorRepository.findById(DONOR2_ID)).thenReturn(Optional.empty());

        assertThrows(UserDoesntExistsException.class, () -> userService.setDonationToDonor(DONOR2_ID, donationDate));
        verify(donorRepository, times(1)).findById(DONOR2_ID);
        verify(donorRepository, times(0)).save(any(Donor.class));
    }
}
