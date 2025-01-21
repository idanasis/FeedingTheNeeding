package Project.Final.FeedingTheNeeding.Security;

import Project.Final.FeedingTheNeeding.Authentication.Controller.AuthController;
import Project.Final.FeedingTheNeeding.Authentication.Config.SecurityConfig; // Import your SecurityConfig
import Project.Final.FeedingTheNeeding.Authentication.Exception.InvalidCredentialException;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.Authentication.Service.JwtTokenService;
import Project.Final.FeedingTheNeeding.Authentication.Config.JwtAuthenticationFilter;
import Project.Final.FeedingTheNeeding.Authentication.DTO.AuthenticationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.AuthenticationResponse;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import; // Import annotation
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider; // Import AuthenticationProvider
import org.springframework.security.test.context.support.WithAnonymousUser; // Optional but recommended
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({TestSecurityConfig.class})
public class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the JwtTokenService
    @MockBean
    private JwtTokenService jwtTokenService;

    // Mock the AuthService
    @MockBean
    private AuthService authService;

    // Mock the JwtAuthenticationFilter to prevent it from interfering with /auth/login
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Mock the AuthenticationProvider to satisfy SecurityConfig's dependency
    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Test
    @DisplayName("POST /auth/login - Public Access")
    @WithAnonymousUser // Simulate an anonymous user
    public void testLoginPublicAccess() throws Exception {
        // Prepare the authentication request
        AuthenticationRequest request = new AuthenticationRequest();
        request.setPhoneNumber("0500000000");
        request.setPassword("123123123");

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPhoneNumber("0500000000");
        userCredentials.setPasswordHash("123123123"); // Note: This is mocked, so the actual value is irrelevant

        // Prepare the authenticated user (Donor)
        Donor donor = new Donor();
        donor.setId(1L);
        donor.setPhoneNumber("0500000000");
        donor.setUserCredentials(userCredentials);
        userCredentials.setDonor(donor);
        userCredentials.setEnabled(true); // Ensure the user is enabled


        // Mock the AuthService to return the userCredentials when authenticate is called
        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(userCredentials);


        // Perform the POST request to /auth/login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
