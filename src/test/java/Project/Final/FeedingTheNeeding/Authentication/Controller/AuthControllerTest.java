package Project.Final.FeedingTheNeeding.Authentication.Controller;


import Project.Final.FeedingTheNeeding.Authentication.DTO.AuthenticationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.NeedyRegistrationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.VerifyDonorDTO;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserAlreadyExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.Authentication.Service.JwtTokenService;
import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.Authentication.Exception.InvalidCredentialException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenService jwtTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    final String PHONE_NUMBER = "0501234567", PASSWORD = "password", TOKEN = "token";
    final String DONOR_FIRST_NAME = "Donor", DONOR_LAST_NAME = "Donor";
    final String NEEDY_FIRST_NAME = "Needy", NEEDY_LAST_NAME = "Needy";
    final String DONOR_PHONE_NUMBER = "0500000000", NEEDY_PHONE_NUMBER = "0500000001";
    final String DONOR_ADDRESS = "address", NEEDY_ADDRESS = "address";
    final String DONOR_CITY = "city", NEEDY_CITY = "city";
    final int NEEDY_FAMILY_SIZE = 5;
    final String VERIFICATION_CODE = "123456";
    final String EMAIL = "test@test.com";



    @Test
    void testLogin_Success() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(PHONE_NUMBER, PASSWORD);
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPhoneNumber(PHONE_NUMBER);
        userCredentials.setPasswordHash(PASSWORD);

        when(authService.authenticate(request)).thenReturn(userCredentials);
        when(jwtTokenService.generateToken(userCredentials)).thenReturn(TOKEN);
        when(jwtTokenService.getExpirationTime()).thenReturn(3600L);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TOKEN))
                .andExpect(jsonPath("$.expirationTime").value(3600));
    }

    @Test
    void testLogin_Failure() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(PHONE_NUMBER, "wrongPassword");

        when(authService.authenticate(request)).thenThrow(new InvalidCredentialException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterDonor_Success() throws Exception {
        RegistrationRequest request = new RegistrationRequest(null, PASSWORD, PASSWORD, DONOR_FIRST_NAME, DONOR_LAST_NAME,
                PHONE_NUMBER, DONOR_ADDRESS, DONOR_CITY);

        doNothing().when(authService).registerDonor(request);

        mockMvc.perform(post("/auth/register/donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Donor successfully registered"));
    }

    @Test
    void testRegisterDonor_Failure() throws Exception {
        RegistrationRequest request = new RegistrationRequest(null, PASSWORD, PASSWORD, DONOR_FIRST_NAME, DONOR_LAST_NAME,
                PHONE_NUMBER, DONOR_ADDRESS, DONOR_CITY);

        doThrow(new UserAlreadyExistsException("Donor already exists")).when(authService).registerDonor(request);

        mockMvc.perform(post("/auth/register/donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Donor already exists"));
    }

    @Test
    void testRegisterNeedy_Success() throws Exception {
        NeedyRegistrationRequest request = new NeedyRegistrationRequest(NEEDY_FIRST_NAME, NEEDY_LAST_NAME,
                NEEDY_PHONE_NUMBER, NEEDY_ADDRESS, NEEDY_CITY, NEEDY_FAMILY_SIZE);

        doNothing().when(authService).registerNeedy(request);

        mockMvc.perform(post("/auth/register/needy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Needy successfully registered"));
    }

    @Test
    void testRegisterNeedy_Failure() throws Exception {
        NeedyRegistrationRequest request = new NeedyRegistrationRequest(NEEDY_FIRST_NAME, NEEDY_LAST_NAME,
                NEEDY_PHONE_NUMBER, NEEDY_ADDRESS, NEEDY_CITY, NEEDY_FAMILY_SIZE);

        doThrow(new UserAlreadyExistsException("Needy already exists")).when(authService).registerNeedy(request);

        mockMvc.perform(post("/auth/register/needy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Needy already exists"));
    }

    @Test
    void testResetPassword_Success() throws Exception {
        doNothing().when(authService).resetPassword(PHONE_NUMBER, PASSWORD);

        mockMvc.perform(post("/auth/reset-password")
                        .param("phoneNumber", PHONE_NUMBER)
                        .param("newPassword", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));
    }

    @Test
    void testResetPassword_Failure() throws Exception {
        doThrow(new UserDoesntExistsException("User not found")).when(authService).resetPassword(PHONE_NUMBER, PASSWORD);

        mockMvc.perform(post("/auth/reset-password")
                        .param("phoneNumber", PHONE_NUMBER)
                        .param("newPassword", PASSWORD))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testVerifyDonor_Success() throws Exception {
        VerifyDonorDTO verifyDonorDTO = new VerifyDonorDTO(PHONE_NUMBER, VERIFICATION_CODE);

        doNothing().when(authService).verifyDonor(verifyDonorDTO);

        mockMvc.perform(post("/auth/verify-donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyDonorDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Donor successfully verified"));
    }

    @Test
    void testVerifyDonor_Failure() throws Exception {
        VerifyDonorDTO verifyDonorDTO = new VerifyDonorDTO(EMAIL, "invalidCode");

        doThrow(new RuntimeException("Invalid verification code"))
                .when(authService).verifyDonor(verifyDonorDTO);

        mockMvc.perform(post("/auth/verify-donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyDonorDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid verification code"));
    }
}
