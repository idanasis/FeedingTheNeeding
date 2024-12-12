package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.User.Model.*;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import Project.Final.FeedingTheNeeding.User.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.BitSet;
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

    final long DONOR1_ID = 1L, NEEDY1_ID = 2L;
    final long DONOR2_ID = 10L, NEEDY2_ID = 20L;
    final String DONOR1_PHONE_NUMBER = "0500000000", NEEDY1_PHONE_NUMBER = "0500000001";
    final String DONOR2_PHONE_NUMBER = "0500000002", NEEDY2_PHONE_NUMBER = "0500000003";
    final String DONOR1_EMAIL = "donor1Email@gmail.com", DONOR2_EMAIL = "donor2Email@gmail.com";

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
        donor2.setId(DONOR2_ID);
        donor2.setPhoneNumber(DONOR2_PHONE_NUMBER);
        donor2.setEmail(DONOR2_EMAIL);
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
    void testGetDonorByEmail_Success(){
        when(donorRepository.findByEmail(DONOR1_EMAIL)).thenReturn(Optional.of(donor1));
        Donor foundDonor = userService.getDonorByEmail(DONOR1_EMAIL);
        assertNotNull(foundDonor);
        assertEquals(DONOR1_EMAIL, foundDonor.getEmail());
        verify(donorRepository, times(1)).findByEmail(DONOR1_EMAIL);
    }

    @Test
    void testGetDonorByEmail_NotFound(){
        when(donorRepository.findByEmail(DONOR2_PHONE_NUMBER)).thenReturn(Optional.empty());
        assertThrows(UserDoesntExistsException.class, () -> userService.getDonorByEmail(DONOR2_PHONE_NUMBER));
        verify(donorRepository, times(1)).findByEmail(DONOR2_PHONE_NUMBER);
    }
}
