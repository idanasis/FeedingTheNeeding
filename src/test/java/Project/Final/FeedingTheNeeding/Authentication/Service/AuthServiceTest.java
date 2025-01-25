package Project.Final.FeedingTheNeeding.Authentication.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.*;
import Project.Final.FeedingTheNeeding.Authentication.Exception.AccountNotVerifiedException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserAlreadyExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Repository.UserCredentialsRepository;
import Project.Final.FeedingTheNeeding.Authentication.Exception.InvalidCredentialException;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    final String EMAIL = "email@email.com";
    final String PASSWORD = "password";
    final String DONOR_FIRST_NAME = "Donor", DONOR_LAST_NAME = "Donor";
    final String NEEDY_FIRST_NAME = "Needy", NEEDY_LAST_NAME = "Needy";
    final String DONOR_PHONE_NUMBER = "0500000000", NEEDY_PHONE_NUMBER = "0500000001";
    final String DONOR_ADDRESS = "address", NEEDY_ADDRESS = "address";
    final String DONOR_EMAIL = "donor1Email@gmail.com";
    final int NEEDY_FAMILY_SIZE = 5;
    final String VERIFICATION_CODE = "123456";
    final String TOKEN = "token";

    private AuthenticationRequest authenticationRequest;
    private RegistrationRequest donorRegistrationRequest;
    private NeedyRegistrationRequest needyRegistrationRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationRequest = new AuthenticationRequest(DONOR_PHONE_NUMBER, PASSWORD);
        donorRegistrationRequest = new RegistrationRequest(DONOR_EMAIL, PASSWORD, PASSWORD, DONOR_FIRST_NAME, DONOR_LAST_NAME, DONOR_PHONE_NUMBER, DONOR_ADDRESS);
        needyRegistrationRequest = new NeedyRegistrationRequest(NEEDY_FIRST_NAME, NEEDY_LAST_NAME, NEEDY_PHONE_NUMBER, NEEDY_ADDRESS, NEEDY_FAMILY_SIZE);
    }


    @Test
    void testAuthenticate_Success() {
        UserCredentials user = new UserCredentials();
        user.setPhoneNumber(authenticationRequest.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(authenticationRequest.getPassword()));

        Donor donor = new Donor();
        donor.setPhoneNumber(authenticationRequest.getPhoneNumber());
        donor.setStatus(RegistrationStatus.AVAILABLE);
        user.setDonor(donor);
        donor.setUserCredentials(user);

        when(userCredentialsRepository.findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber()))
                .thenReturn(user);

        when(passwordEncoder.matches(authenticationRequest.getPassword(), user.getPasswordHash()))
                .thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        UserCredentials result = authService.authenticate(authenticationRequest);

        assertEquals(user, result);
        verify(userCredentialsRepository, times(1)).findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber());
        verify(passwordEncoder, times(1)).matches(authenticationRequest.getPassword(), user.getPasswordHash());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        when(userCredentialsRepository.findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber()))
                .thenReturn(null);

        Exception exception = assertThrows(InvalidCredentialException.class, () -> {
            authService.authenticate(authenticationRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userCredentialsRepository, times(1)).findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber());
        verifyNoMoreInteractions(passwordEncoder, authenticationManager);
    }

    @Test
    void testLogout_Success() {
        String token = TOKEN;

        authService.logout(token);
    }

    @Test
    void registerDonor_ValidRequest_Success() {
        when(donorRepository.findByPhoneNumber(donorRegistrationRequest.getPhoneNumber()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(donorRegistrationRequest.getPassword()))
                .thenReturn("encodedPassword");

        Donor savedDonor = new Donor();
        savedDonor.setPhoneNumber(donorRegistrationRequest.getPhoneNumber());
        when(donorRepository.save(any(Donor.class))).thenReturn(savedDonor);

        UserCredentials savedCredentials = new UserCredentials();
        savedCredentials.setPhoneNumber(donorRegistrationRequest.getPhoneNumber());
        savedCredentials.setPasswordHash("encodedPassword");
        when(userCredentialsRepository.save(any(UserCredentials.class))).thenReturn(savedCredentials);

        authService.registerDonor(donorRegistrationRequest);

        verify(donorRepository, times(1)).findByPhoneNumber(donorRegistrationRequest.getPhoneNumber());
        verify(passwordEncoder, times(1)).encode(donorRegistrationRequest.getPassword());
        verify(donorRepository, times(2)).save(any(Donor.class));
        verify(userCredentialsRepository, times(1)).save(any(UserCredentials.class));
    }

    @Test
    void registerDonor_DonorAlreadyExists_ThrowsException() {
        Donor existingDonor = new Donor();
        existingDonor.setPhoneNumber(donorRegistrationRequest.getPhoneNumber());

        when(donorRepository.findByPhoneNumber(donorRegistrationRequest.getPhoneNumber()))
                .thenReturn(Optional.of(existingDonor));

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authService.registerDonor(donorRegistrationRequest);
        });

        assertEquals("Donor already exists", exception.getMessage());
        verify(donorRepository, times(1)).findByPhoneNumber(donorRegistrationRequest.getPhoneNumber());
        verifyNoMoreInteractions(donorRepository, userCredentialsRepository, passwordEncoder);
    }

    @Test
    void registerNeedy_ValidRequest_Success() {
        when(needyRepository.findByPhoneNumber(needyRegistrationRequest.getPhoneNumber()))
                .thenReturn(Optional.empty());

        Needy savedNeedy = new Needy();
        savedNeedy.setPhoneNumber(needyRegistrationRequest.getPhoneNumber());
        when(needyRepository.save(any(Needy.class))).thenReturn(savedNeedy);

        authService.registerNeedy(needyRegistrationRequest);

        verify(needyRepository, times(1)).findByPhoneNumber(needyRegistrationRequest.getPhoneNumber());
        verify(needyRepository, times(1)).save(any(Needy.class));
    }

    @Test
    void registerNeedy_NeedyAlreadyExists_ThrowsException() {
        Needy existingNeedy = new Needy();
        existingNeedy.setPhoneNumber(needyRegistrationRequest.getPhoneNumber());

        when(needyRepository.findByPhoneNumber(needyRegistrationRequest.getPhoneNumber()))
                .thenReturn(Optional.of(existingNeedy));

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            authService.registerNeedy(needyRegistrationRequest);
        });

        assertEquals("Needy already exists", exception.getMessage());
        verify(needyRepository, times(1)).findByPhoneNumber(needyRegistrationRequest.getPhoneNumber());
        verifyNoMoreInteractions(needyRepository);
    }

    @Test
    void initiatePasswordReset_ValidRequest_Success() {
        UserCredentials user = new UserCredentials();
        user.setPhoneNumber(DONOR_PHONE_NUMBER);
        Donor donor = new Donor();
        donor.setPhoneNumber(DONOR_PHONE_NUMBER);
        user.setDonor(donor);

        when(userCredentialsRepository.findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER))
                .thenReturn(user);

        when(donorRepository.save(any(Donor.class))).thenReturn(donor);

        authService.initiatePasswordReset(DONOR_PHONE_NUMBER);

        verify(userCredentialsRepository, times(1)).findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER);
        verify(donorRepository, times(1)).save(donor);
        assertNotNull(donor.getVerificationCode());
        assertNotNull(donor.getVerificationCodeExpiresAt());
    }

    @Test
    void initiatePasswordReset_UserNotFound_ThrowsException() {
        when(userCredentialsRepository.findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER))
                .thenReturn(null);

        Exception exception = assertThrows(UserDoesntExistsException.class, () -> {
            authService.initiatePasswordReset(DONOR_PHONE_NUMBER);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userCredentialsRepository, times(1)).findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER);
        verifyNoMoreInteractions(donorRepository);
    }

    @Test
    void confirmPasswordReset_ValidRequest_Success() {
        UserCredentials user = new UserCredentials();
        user.setPhoneNumber(DONOR_PHONE_NUMBER);
        Donor donor = new Donor();
        donor.setPhoneNumber(DONOR_PHONE_NUMBER);
        donor.setVerificationCode(VERIFICATION_CODE);
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        user.setDonor(donor);

        when(userCredentialsRepository.findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER))
                .thenReturn(user);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn("encodedNewPassword");

        when(userCredentialsRepository.save(user)).thenReturn(user);
        when(donorRepository.save(donor)).thenReturn(donor);

        authService.confirmPasswordReset(DONOR_PHONE_NUMBER, VERIFICATION_CODE, PASSWORD);

        assertEquals("encodedNewPassword", user.getPasswordHash());
        assertNull(donor.getVerificationCode());
        assertNull(donor.getVerificationCodeExpiresAt());

        verify(userCredentialsRepository, times(1)).findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER);
        verify(passwordEncoder, times(1)).encode(PASSWORD);
        verify(userCredentialsRepository, times(1)).save(user);
        verify(donorRepository, times(1)).save(donor);
    }

    @Test
    void confirmPasswordReset_UserNotFound_ThrowsException() {
        when(userCredentialsRepository.findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER))
                .thenReturn(null);

        Exception exception = assertThrows(UserDoesntExistsException.class, () -> {
            authService.confirmPasswordReset(DONOR_PHONE_NUMBER, VERIFICATION_CODE, PASSWORD);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userCredentialsRepository, times(1)).findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER);
        verifyNoMoreInteractions(passwordEncoder, userCredentialsRepository, donorRepository);
    }

    @Test
    void confirmPasswordReset_VerificationCodeExpired_ThrowsException() {
        UserCredentials user = new UserCredentials();
        user.setPhoneNumber(DONOR_PHONE_NUMBER);
        Donor donor = new Donor();
        donor.setPhoneNumber(DONOR_PHONE_NUMBER);
        donor.setVerificationCode(VERIFICATION_CODE);
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));
        user.setDonor(donor);

        when(userCredentialsRepository.findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER))
                .thenReturn(user);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.confirmPasswordReset(DONOR_PHONE_NUMBER, VERIFICATION_CODE, PASSWORD);
        });

        assertEquals("Invalid verification code", exception.getMessage());
        verify(userCredentialsRepository, times(1)).findCredentialsByPhoneNumber(DONOR_PHONE_NUMBER);
        verifyNoMoreInteractions(passwordEncoder, userCredentialsRepository, donorRepository);
    }

    @Test
    void sendVerificationEmail_Success() throws MessagingException {
        Donor donor = new Donor();
        donor.setEmail(EMAIL);
        donor.setVerificationCode(VERIFICATION_CODE);
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        authService.sendVerificationEmail(donor);

        verify(emailService, times(1)).sendVerificationEmail(eq(EMAIL), anyString(), anyString());
        verifyNoMoreInteractions(emailService);
    }

    @Test
    void sendVerificationEmail_Failure_ThrowsException() throws MessagingException {
        Donor donor = new Donor();
        donor.setEmail(EMAIL);
        donor.setVerificationCode(VERIFICATION_CODE);
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        doThrow(new RuntimeException("Email service failure"))
                .when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

        Exception exception = assertThrows(UserDoesntExistsException.class, () -> {
            authService.sendVerificationEmail(donor);
        });

        assertEquals("cant send verification email", exception.getMessage());
        verify(emailService, times(1)).sendVerificationEmail(eq(EMAIL), anyString(), anyString());
        verifyNoMoreInteractions(emailService);
    }

    @Test
    void resendVerificationEmail_Success() throws Exception {
        Donor donor = new Donor();
        donor.setEmail(EMAIL);
        donor.setVerificationCode("oldCode");
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(donorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(donor));
        when(donorRepository.save(any(Donor.class))).thenReturn(donor);
        doNothing().when(emailService).sendVerificationEmail(eq(EMAIL), anyString(), anyString());

        authService.resendVerificationEmail(EMAIL);

        assertNotNull(donor.getVerificationCode());
        assertNotNull(donor.getVerificationCodeExpiresAt());

        verify(emailService, times(1)).sendVerificationEmail(eq(EMAIL), anyString(), anyString());
    }

    @Test
    void resendVerificationEmail_UserNotFound_ThrowsException() {
        when(donorRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserDoesntExistsException.class, () -> {
            authService.resendVerificationEmail(EMAIL);
        });

        assertEquals("donor not found", exception.getMessage());
        verify(donorRepository, times(1)).findByEmail(EMAIL);
        verifyNoMoreInteractions(donorRepository, emailService);
    }

    @Test
    void resendVerificationEmail_EmailSendFailure_ThrowsException() throws Exception {
        Donor donor = new Donor();
        donor.setEmail(EMAIL);
        donor.setVerificationCode("oldCode");
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(donorRepository.findByEmail(EMAIL)).thenReturn(Optional.of(donor));
        doThrow(new RuntimeException("Email service failure"))
                .when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

        Exception exception = assertThrows(UserDoesntExistsException.class, () -> {
            authService.resendVerificationEmail(EMAIL);
        });

        assertEquals("cant send verification email", exception.getMessage());
        verify(donorRepository, times(1)).findByEmail(EMAIL);
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString(), anyString());
        verifyNoMoreInteractions(donorRepository);
    }

    @Test
    void getUserRoleFromJWT_Success() {
        when(jwtTokenService.extractUsername(TOKEN)).thenReturn(DONOR_PHONE_NUMBER);

        Donor donor = new Donor();
        donor.setPhoneNumber(DONOR_PHONE_NUMBER);
        donor.setRole(UserRole.ADMIN);

        when(donorRepository.findByPhoneNumber(DONOR_PHONE_NUMBER)).thenReturn(Optional.of(donor));

        UserRole role = authService.getUserRoleFromJWT(TOKEN);

        assertEquals(UserRole.ADMIN, role);
        verify(jwtTokenService, times(1)).extractUsername(TOKEN);
        verify(donorRepository, times(1)).findByPhoneNumber(DONOR_PHONE_NUMBER);
    }

    @Test
    void getUserRoleFromJWT_UserNotFound_ThrowsException() {
        when(jwtTokenService.extractUsername(TOKEN)).thenReturn(DONOR_PHONE_NUMBER);
        when(donorRepository.findByPhoneNumber(DONOR_PHONE_NUMBER)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserDoesntExistsException.class, () -> {
            authService.getUserRoleFromJWT(TOKEN);
        });

        assertEquals("donor not found", exception.getMessage());
        verify(jwtTokenService, times(1)).extractUsername(TOKEN);
        verify(donorRepository, times(1)).findByPhoneNumber(DONOR_PHONE_NUMBER);
    }

    @Test
    void getUserRoleFromJWT_InvalidToken_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.getUserRoleFromJWT(null);
        });

        assertEquals("invalid token", exception.getMessage());
        verifyNoInteractions(jwtTokenService, donorRepository);
    }

    @Test
    void getUserIDFromJWT_Success() {
        when(jwtTokenService.extractUsername(TOKEN)).thenReturn(DONOR_PHONE_NUMBER);

        Donor donor = new Donor();
        donor.setPhoneNumber(DONOR_PHONE_NUMBER);
        donor.setId(12345L);

        when(donorRepository.findByPhoneNumber(DONOR_PHONE_NUMBER)).thenReturn(Optional.of(donor));

        long userId = authService.getUserIDFromJWT(TOKEN);

        assertEquals(12345L, userId);
        verify(jwtTokenService, times(1)).extractUsername(TOKEN);
        verify(donorRepository, times(1)).findByPhoneNumber(DONOR_PHONE_NUMBER);
    }

    @Test
    void getUserIDFromJWT_UserNotFound_ThrowsException() {
        when(jwtTokenService.extractUsername(TOKEN)).thenReturn(DONOR_PHONE_NUMBER);
        when(donorRepository.findByPhoneNumber(DONOR_PHONE_NUMBER)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserDoesntExistsException.class, () -> {
            authService.getUserIDFromJWT(TOKEN);
        });

        assertEquals("donor not found", exception.getMessage());
        verify(jwtTokenService, times(1)).extractUsername(TOKEN);
        verify(donorRepository, times(1)).findByPhoneNumber(DONOR_PHONE_NUMBER);
    }

    @Test
    void getUserIDFromJWT_InvalidToken_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.getUserIDFromJWT(null);
        });

        assertEquals("invalid token", exception.getMessage());
        verifyNoInteractions(jwtTokenService, donorRepository);
    }

    @Test
    void generateVerificationCode_ValidRequest_Success() {
        String verificationCode = authService.generateVerificationCode();

        assertNotNull(verificationCode);
        assertEquals(6, verificationCode.length());
        assertTrue(verificationCode.matches("\\d{6}"));
    }

    @Test
    void testGenerateVerificationCode_Randomness() {
        String code1 = authService.generateVerificationCode();
        String code2 = authService.generateVerificationCode();

        assertNotEquals(code1, code2, "Verification codes should be random and unique");
    }

}
