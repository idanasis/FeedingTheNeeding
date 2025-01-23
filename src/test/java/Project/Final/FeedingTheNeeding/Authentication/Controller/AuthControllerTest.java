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
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
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
    final int NEEDY_FAMILY_SIZE = 5;
    final String VERIFICATION_CODE = "123456";
    final String EMAIL = "test@test.com";
    final long ID = 1L;


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
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testRegisterDonor_Success() throws Exception {
        RegistrationRequest request = new RegistrationRequest(null, PASSWORD, PASSWORD, DONOR_FIRST_NAME, DONOR_LAST_NAME,
                PHONE_NUMBER, DONOR_ADDRESS);

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
                PHONE_NUMBER, DONOR_ADDRESS);

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
                NEEDY_PHONE_NUMBER, NEEDY_ADDRESS, NEEDY_FAMILY_SIZE);

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
                NEEDY_PHONE_NUMBER, NEEDY_ADDRESS, NEEDY_FAMILY_SIZE);

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
        VerifyDonorDTO verifyDonorDTO = new VerifyDonorDTO(PHONE_NUMBER, "invalidCode");

        doThrow(new RuntimeException("Invalid verification code"))
                .when(authService).verifyDonor(verifyDonorDTO);

        mockMvc.perform(post("/auth/verify-donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyDonorDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid verification code"));
    }

    // New Tests for Logout Endpoint

    @Test
    void testLogout_Success_WithToken() throws Exception {
        String authorizationHeader = "Bearer " + TOKEN;

        doNothing().when(authService).logout(TOKEN);

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("logged out successfully"));
    }

    @Test
    void testLogout_Success_NoToken() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("No token provided. nothing to invalidate"));
    }

    @Test
    void testLogout_Failure() throws Exception {
        String authorizationHeader = "Bearer " + TOKEN;

        doThrow(new RuntimeException("Invalid token")).when(authService).logout(TOKEN);

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token"));
    }

    // New Tests for Request Reset Password Endpoint

    @Test
    void testRequestResetPassword_Success() throws Exception {
        doNothing().when(authService).initiatePasswordReset(PHONE_NUMBER);

        mockMvc.perform(post("/auth/request-reset-password")
                        .param("phoneNumber", PHONE_NUMBER))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification code sent to your phone number."));
    }

    @Test
    void testRequestResetPassword_Failure() throws Exception {
        doThrow(new RuntimeException("Phone number not found")).when(authService).initiatePasswordReset(PHONE_NUMBER);

        mockMvc.perform(post("/auth/request-reset-password")
                        .param("phoneNumber", PHONE_NUMBER))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Phone number not found"));
    }

    // Updated Tests for Confirm Reset Password Endpoint

    @Test
    void testConfirmResetPassword_Success() throws Exception {
        doNothing().when(authService).confirmPasswordReset(PHONE_NUMBER, VERIFICATION_CODE, PASSWORD);

        mockMvc.perform(post("/auth/confirm-reset-password")
                        .param("phoneNumber", PHONE_NUMBER)
                        .param("verificationCode", VERIFICATION_CODE)
                        .param("newPassword", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully."));
    }

    @Test
    void testConfirmResetPassword_Unauthorized_Failure() throws Exception {
        doThrow(new RuntimeException("Invalid verification code"))
                .when(authService).confirmPasswordReset(PHONE_NUMBER, "wrongCode", PASSWORD);

        mockMvc.perform(post("/auth/confirm-reset-password")
                        .param("phoneNumber", PHONE_NUMBER)
                        .param("verificationCode", "wrongCode")
                        .param("newPassword", PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid verification code"));
    }

    @Test
    void testResendVerificationCode_Success() throws Exception {
        doNothing().when(authService).resendVerificationEmail(EMAIL);

        mockMvc.perform(post("/auth/resend-email")
                        .param("email", EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().string("Email code resend successfully"));
    }

    @Test
    void testResendVerificationCode_Failure() throws Exception {
        doThrow(new RuntimeException("Email not found")).when(authService).resendVerificationEmail(EMAIL);

        mockMvc.perform(post("/auth/resend-email")
                        .param("email", EMAIL))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email not found"));
    }

    @Test
    void testGetUserRoleFromJWT_Success() throws Exception {
        UserRole role = UserRole.ADMIN;
        when(authService.getUserRoleFromJWT(TOKEN)).thenReturn(role);

        mockMvc.perform(get("/auth/user-role")
                        .param("token", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("ADMIN"));
    }

    @Test
    void testGetUserRoleFromJWT_Failure() throws Exception {
        when(authService.getUserRoleFromJWT(TOKEN)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/auth/user-role")
                        .param("token", TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token"));
    }

    @Test
    void testGetUserIDFromJWT_Success() throws Exception {
        when(authService.getUserIDFromJWT(TOKEN)).thenReturn(ID);

        mockMvc.perform(get("/auth/user-id")
                        .param("token", TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(ID)));
    }

    @Test
    void testGetUserIDFromJWT_Failure() throws Exception {
        when(authService.getUserIDFromJWT(TOKEN)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/auth/user-id")
                        .param("token", TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid token"));
    }
}
