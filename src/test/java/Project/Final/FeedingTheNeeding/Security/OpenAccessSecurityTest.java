package Project.Final.FeedingTheNeeding.Security;

import Project.Final.FeedingTheNeeding.Authentication.Config.SecurityConfig;
import Project.Final.FeedingTheNeeding.Authentication.Controller.AuthController;
import Project.Final.FeedingTheNeeding.Authentication.DTO.AuthenticationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationRequest;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SecurityConfig.class)
@DisplayName("Open Access Endpoints Security Tests")
public class OpenAccessSecurityTest extends BaseSecurityTest{

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final long ID = 1L;
    private final String PHONE_NUMBER = "0500000000", PASSWORD = "123123123", HASHED_PASSWORD = "hashedPassword";
    private final String EMAIL = "test@test.com", FIRSTNAME = "firstname", LASTNAME = "lastname", ADDRESS = "address";
    private final String TOKEN = "token";

    UserCredentials mockCredentials;
    Donor mockDonor;
    AuthenticationRequest authRequest;
    RegistrationRequest registrationRequest;

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
        registrationRequest = new RegistrationRequest(EMAIL, PASSWORD, PASSWORD, FIRSTNAME, LASTNAME, PHONE_NUMBER, ADDRESS);

        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(mockCredentials);
        when(jwtTokenService.generateToken(mockCredentials)).thenReturn(TOKEN);
        when(jwtTokenService.getExpirationTime()).thenReturn(3600L); // 1 hour
        doNothing().when(authService).registerDonor(any(RegistrationRequest.class));
    }

    public static Stream<String> openAccessEndpoints() {
        return Stream.of(
                "/auth/login",
                "/auth/register/donor"
        );
    }

    @ParameterizedTest(name = "Accessing {0} should be permitted without authentication")
    @MethodSource("openAccessEndpoints")
    @WithAnonymousUser
    void testOpenAccessEndpoints(String url) throws Exception {
        if (url.equals("/auth/login")) {
            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authRequest)))
                    .andExpect(status().isOk());
        }
        else if (url.equals("/auth/register/donor")) {
            mockMvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(get(url))
                    .andExpect(status().isOk());
        }
    }

}
