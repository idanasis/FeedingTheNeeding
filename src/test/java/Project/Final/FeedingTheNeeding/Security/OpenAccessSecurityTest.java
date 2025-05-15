package Project.Final.FeedingTheNeeding.Security;

import Project.Final.FeedingTheNeeding.Authentication.Config.SecurityConfig;
import Project.Final.FeedingTheNeeding.Authentication.Controller.AuthController;
import Project.Final.FeedingTheNeeding.Authentication.DTO.AuthenticationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.NeedyRegistrationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationRequest;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.Authentication.Service.JwtTokenService;
import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import Project.Final.FeedingTheNeeding.cook.Controller.ConstraintMapper;
import Project.Final.FeedingTheNeeding.cook.Controller.CookController;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Import({SecurityConfig.class})
public class OpenAccessSecurityTest extends BaseSecurityTest{

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final long ID = 1L;
    private final String PHONE_NUMBER = "0500000000", PASSWORD = "123123123", HASHED_PASSWORD = "hashedPassword";
    private final String EMAIL = "test@test.com", FIRSTNAME = "firstname", LASTNAME = "lastname", ADDRESS = "address";
    private final String TOKEN = "token", VERIFICATION_CODE = "123456";
    private final String PHONE_NUMNER2 = "0500000001";
    private final int FAMILY_SIZE = 2;
    final String DONOR_STREET = "ד'" , NEEDY_STREET = "ד'";


    UserCredentials mockCredentials;
    Donor mockDonor;
    AuthenticationRequest authRequest;
    RegistrationRequest registrationRequest;
    NeedyRegistrationRequest needyRegistrationRequest;

    @MockBean
    CookingService cookingService;
    @MockBean
    ConstraintMapper constraintMapper;

    @BeforeEach
    void setup() {
        mockCredentials = new UserCredentials();
        mockDonor = new Donor();
        mockCredentials.setId(ID);
        mockCredentials.setPhoneNumber(PHONE_NUMBER);
        mockCredentials.setPasswordHash(HASHED_PASSWORD);
        mockDonor.setId(ID);
        mockDonor.setPhoneNumber(PHONE_NUMBER);
        mockDonor.setEmail(EMAIL);
        mockDonor.setFirstName(FIRSTNAME);
        mockDonor.setLastName(LASTNAME);
        mockDonor.setAddress(ADDRESS);
        mockCredentials.setDonor(mockDonor);
        mockDonor.setUserCredentials(mockCredentials);

        authRequest = new AuthenticationRequest(PHONE_NUMBER, PASSWORD);
        registrationRequest = new RegistrationRequest(EMAIL, PASSWORD, PASSWORD, FIRSTNAME, LASTNAME, PHONE_NUMBER,DONOR_STREET, ADDRESS);
        needyRegistrationRequest = new NeedyRegistrationRequest(FIRSTNAME, LASTNAME, PHONE_NUMNER2, ADDRESS, FAMILY_SIZE,NEEDY_STREET);

        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(mockCredentials);
        when(jwtTokenService.generateToken(mockCredentials)).thenReturn(TOKEN);
        when(jwtTokenService.getExpirationTime()).thenReturn(3600L); // 1 hour
        doNothing().when(authService).registerDonor(any(RegistrationRequest.class));
        doNothing().when(authService).registerNeedy(any(NeedyRegistrationRequest.class));
        doNothing().when(authService).logout(anyString());
        doNothing().when(authService).initiatePasswordReset(anyString());
        doNothing().when(authService).confirmPasswordReset(anyString(), anyString(), anyString());
        doNothing().when(authService).resendVerificationEmail(anyString());
        when(authService.getUserRoleFromJWT(anyString())).thenReturn(UserRole.ADMIN);
        when(authService.getUserIDFromJWT(anyString())).thenReturn(ID);
    }

    public static Stream<String> openAccessEndpoints() {
        return Stream.of(
                "/auth/login",
                "/auth/register/donor",
                "/auth/register/needy",
                "/auth/request-reset-password",
                "/auth/confirm-reset-password",
                "/auth/resend-email"
                );
    }

    @ParameterizedTest(name = "Accessing {0} should be permitted without authentication")
    @MethodSource("openAccessEndpoints")
    @WithAnonymousUser
    void testOpenAccessEndpoints(String url) throws Exception {
        switch (url) {
            case "/auth/login":
                mockMvc.perform(post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)))
                        .andExpect(status().isOk());
                break;
            case "/auth/register/donor":
                mockMvc.perform(post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest)))
                        .andExpect(status().isOk());
                break;
            case "/auth/register/needy":
                mockMvc.perform(post(url)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(needyRegistrationRequest)))
                        .andExpect(status().isOk());
                break;
            case "/auth/request-reset-password":
                mockMvc.perform(post(url)
                                .param("phoneNumber", PHONE_NUMBER))
                        .andExpect(status().isOk());
                break;
            case "/auth/confirm-reset-password":
                mockMvc.perform(post(url)
                                .param("phoneNumber", PHONE_NUMBER)
                                .param("verificationCode", VERIFICATION_CODE)
                                .param("newPassword", PASSWORD))
                        .andExpect(status().isOk());
                break;
            case "/auth/resend-email":
                mockMvc.perform(post(url)
                                .param("email", EMAIL))
                        .andExpect(status().isOk());
                break;
            default:
                mockMvc.perform(get(url))
                        .andExpect(status().isOk());
        }
    }

}
