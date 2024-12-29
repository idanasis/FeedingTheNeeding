package Project.Final.FeedingTheNeeding.Authentication.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.*;
import Project.Final.FeedingTheNeeding.Authentication.Exception.AccountNotVerifiedException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserAlreadyExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Repository.UserCredentialsRepository;
import Project.Final.FeedingTheNeeding.User.Exception.InvalidCredentialException;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private NeedyRepository needyRepository;

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @Mock
    private TwilioSmsSender smsSender;

    @InjectMocks
    private AuthService authService;

    final String EMAIL = "email@email.com";
    final String PASSWORD = "password";
    final String DONOR_FIRST_NAME = "Donor", DONOR_LAST_NAME = "Donor";
    final String NEEDY_FIRST_NAME = "Needy", NEEDY_LAST_NAME = "Needy";
    final String DONOR_PHONE_NUMBER = "0500000000", NEEDY_PHONE_NUMBER = "0500000001";
    final String DONOR_ADDRESS = "address", NEEDY_ADDRESS = "address";
    final String DONOR_CITY = "city", NEEDY_CITY = "city";
    final String DONOR_EMAIL = "donor1Email@gmail.com";
    final int NEEDY_FAMILY_SIZE = 5;
    final String VERIFICATION_CODE = "123456";

    private AuthenticationRequest authenticationRequest;
    private RegistrationRequest donorRegistrationRequest;
    private NeedyRegistrationRequest needyRegistrationRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationRequest = new AuthenticationRequest(DONOR_PHONE_NUMBER, PASSWORD);
        donorRegistrationRequest = new RegistrationRequest(DONOR_EMAIL, PASSWORD, PASSWORD,DONOR_FIRST_NAME, DONOR_LAST_NAME, DONOR_PHONE_NUMBER, DONOR_ADDRESS, DONOR_CITY);
        needyRegistrationRequest = new NeedyRegistrationRequest(NEEDY_FIRST_NAME, NEEDY_LAST_NAME, NEEDY_PHONE_NUMBER, NEEDY_ADDRESS, NEEDY_CITY, NEEDY_FAMILY_SIZE);
    }

    @Test
    void testAuthenticate_Success() {
        UserCredentials user = new UserCredentials();
        user.setPhoneNumber(authenticationRequest.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(authenticationRequest.getPassword()));
        Donor donor = new Donor();
        donor.setPhoneNumber(authenticationRequest.getPhoneNumber());
        donor.setVerified(true);
        user.setDonor(donor);

        when(userCredentialsRepository.findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber()))
                .thenReturn(user);

        // Mock password encoder to return true for matching passwords
        when(passwordEncoder.matches(authenticationRequest.getPassword(), user.getPasswordHash()))
                .thenReturn(true);

        // Mock authenticate to return null or a valid Authentication object
        when(authenticationManager.authenticate(any())).thenReturn(null);

        UserCredentials result = authService.authenticate(authenticationRequest);

        assertEquals(user, result);
        verify(userCredentialsRepository, times(1)).findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber());
        verify(passwordEncoder, times(1)).matches(authenticationRequest.getPassword(), user.getPasswordHash());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        when(userCredentialsRepository.findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber())).thenReturn(null);

        assertThrows(InvalidCredentialException.class, () -> authService.authenticate(authenticationRequest));
    }

    @Test
    void authenticate_AccountNotVerified_ThrowsException() {
        UserCredentials user = new UserCredentials();
        user.setPhoneNumber(authenticationRequest.getPhoneNumber());
        user.setPasswordHash(authenticationRequest.getPassword());
        Donor donor = new Donor();
        donor.setPhoneNumber(authenticationRequest.getPhoneNumber());
        donor.setVerified(false);
        user.setDonor(donor);

        when(userCredentialsRepository.findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber())).thenReturn(user);

        assertThrows(AccountNotVerifiedException.class, () -> authService.authenticate(authenticationRequest));
    }

    @Test
    void authenticate_InvalidPassword_ThrowsException() {
        UserCredentials user = new UserCredentials();
        user.setPhoneNumber(authenticationRequest.getPhoneNumber());
        user.setPasswordHash(authenticationRequest.getPassword());
        Donor donor = new Donor();
        donor.setPhoneNumber(authenticationRequest.getPhoneNumber());
        donor.setVerified(true);
        user.setDonor(donor);

        when(userCredentialsRepository.findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber())).thenReturn(user);
        doThrow(InvalidCredentialException.class).when(authenticationManager).authenticate(any());

        assertThrows(InvalidCredentialException.class, () -> authService.authenticate(authenticationRequest));
    }

    @Test
    void registerDonor_ValidRequest_Success() {
        doNothing().when(smsSender).sendSms(any(SmsRequest.class));
        when(donorRepository.findByPhoneNumber(donorRegistrationRequest.getPhoneNumber())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(donorRegistrationRequest.getPassword())).thenReturn("encodedPassword");
        when(donorRepository.save(any(Donor.class))).thenAnswer(i -> i.getArgument(0));
        when(userCredentialsRepository.save(any(UserCredentials.class))).thenAnswer(i -> i.getArgument(0));

        authService.registerDonor(donorRegistrationRequest);

        verify(donorRepository, times(1)).save(any(Donor.class));
        verify(userCredentialsRepository, times(1)).save(any(UserCredentials.class));
    }

    @Test
    void registerDonor_DonorAlreadyExists_ThrowsException() {
        when(donorRepository.findByPhoneNumber(donorRegistrationRequest.getPhoneNumber())).thenReturn(Optional.of(new Donor()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerDonor(donorRegistrationRequest));
    }

    @Test
    void registerNeedy_ValidRequest_Success() {
        when(needyRepository.findByPhoneNumber(needyRegistrationRequest.getPhoneNumber())).thenReturn(Optional.empty());
        when(needyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        authService.registerNeedy(needyRegistrationRequest);

        verify(needyRepository, times(1)).save(any());
    }

    @Test
    void registerNeedy_NeedyAlreadyExists_ThrowsException() {
        when(needyRepository.findByPhoneNumber(needyRegistrationRequest.getPhoneNumber())).thenReturn(Optional.of(new Needy()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerNeedy(needyRegistrationRequest));
    }


    @Test
    void resetPassword_ValidRequest_Success() {
        UserCredentials user = new UserCredentials();
        user.setPhoneNumber(DONOR_PHONE_NUMBER);

        when(userCredentialsRepository.findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER)).thenReturn(user);
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");

        authService.resetPassword(DONOR_PHONE_NUMBER, PASSWORD);

        verify(userCredentialsRepository, times(1)).save(user);
        assertEquals("encodedPassword", user.getPasswordHash());
    }

    @Test
    void resetPassword_UserNotFound_ThrowsException() {
        when(userCredentialsRepository.findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER)).thenReturn(null);

        assertThrows(UserDoesntExistsException.class, () -> authService.resetPassword(DONOR_PHONE_NUMBER, PASSWORD));
    }

    @Test
    void generateVerificationCode_ValidRequest_Success() {
        String verificationCode = authService.generateVerificationCode();

        assertNotNull(verificationCode);
        assertEquals(6, verificationCode.length()); // Assuming a 6-character code
    }

}
