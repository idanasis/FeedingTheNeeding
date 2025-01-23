package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_ENDPOINT = "/user";
    private static final String AUTH_DONOR_ENDPOINT = "/authDonor";
    private static final String GET_ALL_USERS_ENDPOINT = "/Allusers";
    private static final String GET_ALL_DONORS_ENDPOINT = "/donors";
    private static final String GET_ALL_NEEDY_ENDPOINT = "/needy";
    private static final String GET_NEEDY_BY_PHONE_ENDPOINT = "/needy/{phoneNumber}";
    private static final String GET_DONOR_BY_PHONE_ENDPOINT = "/donor/{phoneNumber}";
    private static final String GET_DONOR_BY_ID_ENDPOINT = "/donor/donorId/{donorId}";
    private static final String GET_DONOR_PENDING_ENDPOINT = "/donor/pending";
    private static final String GET_DONOR_APPROVED_ENDPOINT = "/donor/approved";
    private static final String UPDATE_DONOR_ENDPOINT = "/donor";
    private static final String DELETE_DONOR_ENDPOINT = "/donor/{id}";

    private static final String DONOR_FIRST_NAME_1 = "Alice";
    private static final String DONOR_FIRST_NAME_2 = "Bob";
    private static final String DONOR_PHONE_1 = "0500000000";
    private static final String DONOR_PHONE_2 = "0500000001";
    private static final String DONOR_ADDRESS = "123 Main St";

    private static final String NEEDY_FIRST_NAME_1 = "Charlie";
    private static final String NEEDY_FIRST_NAME_2 = "Diana";
    private static final String NEEDY_PHONE_1 = "0510000000";
    private static final String NEEDY_PHONE_2 = "0510000001";

    long donorId1 = 1L;
    long donorId2 = 2L;

    @Nested
    @DisplayName("Authenticated Donor Endpoint")
    class AuthenticatedDonorTests {

        @Test
        @DisplayName("GET /user/authDonor - Retrieve Authenticated Donor (Success)")
        void testAuthenticatedDonor_Success() throws Exception {
            UserCredentials mockUserCredentials = mock(UserCredentials.class);
            Donor mockDonor = new Donor();
            mockDonor.setId(donorId1);
            mockDonor.setFirstName(DONOR_FIRST_NAME_1);
            when(mockUserCredentials.getDonor()).thenReturn(mockDonor);

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(mockUserCredentials);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            mockMvc.perform(get(BASE_ENDPOINT + AUTH_DONOR_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(donorId1))
                    .andExpect(jsonPath("$.firstName").value(DONOR_FIRST_NAME_1));
        }

        @Test
        @DisplayName("GET /user/authDonor - Retrieve Authenticated Donor (Failure)")
        void testAuthenticatedDonor_Failure() throws Exception {
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenThrow(new RuntimeException("Authentication failed"));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            mockMvc.perform(get(BASE_ENDPOINT + AUTH_DONOR_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Authentication failed"));
        }
    }

    @Nested
    @DisplayName("Get All Users")
    class GetAllUsersTests {

        @Test
        @DisplayName("GET /user/Allusers - Retrieve All Users")
        void testGetAllUsers_Success() throws Exception {
            BaseUser donorUser = new Donor();
            donorUser.setFirstName(DONOR_FIRST_NAME_1);

            BaseUser needyUser = new Needy();
            needyUser.setFirstName(NEEDY_FIRST_NAME_1);

            when(userService.getAll()).thenReturn(Arrays.asList(donorUser, needyUser));

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_USERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].firstName").value(DONOR_FIRST_NAME_1))
                    .andExpect(jsonPath("$[1].firstName").value(NEEDY_FIRST_NAME_1));
        }

        @Test
        @DisplayName("GET /user/Allusers - Retrieve All Users (Failure)")
        void testGetAllUsers_Failure() throws Exception {
            when(userService.getAll()).thenThrow(new RuntimeException("Failed to retrieve users"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_USERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Failed to retrieve users"));
        }
    }

    @Nested
    @DisplayName("Get All Donors")
    class GetAllDonorsTests {

        @Test
        @DisplayName("GET /user/donors - Retrieve All Donors")
        void testGetAllDonors_Success() throws Exception {
            Donor donor1 = new Donor();
            donor1.setPhoneNumber(DONOR_PHONE_1);

            Donor donor2 = new Donor();
            donor2.setPhoneNumber(DONOR_PHONE_2);

            when(userService.getAllDonors()).thenReturn(Arrays.asList(donor1, donor2));

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_DONORS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].phoneNumber").value(DONOR_PHONE_1))
                    .andExpect(jsonPath("$[1].phoneNumber").value(DONOR_PHONE_2));
        }

        @Test
        @DisplayName("GET /user/donors - Retrieve All Donors (Failure)")
        void testGetAllDonors_Failure() throws Exception {
            when(userService.getAllDonors()).thenThrow(new RuntimeException("Failed to retrieve donors"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_DONORS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Failed to retrieve donors"));
        }
    }

    @Nested
    @DisplayName("Get All Needy Users")
    class GetAllNeedyUsersTests {

        @Test
        @DisplayName("GET /user/needy - Retrieve All Needy Users")
        void testGetAllNeedyUsers_Success() throws Exception {
            Needy needy1 = new Needy();
            needy1.setPhoneNumber(NEEDY_PHONE_1);

            Needy needy2 = new Needy();
            needy2.setPhoneNumber(NEEDY_PHONE_2);

            when(userService.getAllNeedyUsers()).thenReturn(Arrays.asList(needy1, needy2));

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_NEEDY_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].phoneNumber").value(NEEDY_PHONE_1))
                    .andExpect(jsonPath("$[1].phoneNumber").value(NEEDY_PHONE_2));
        }

        @Test
        @DisplayName("GET /user/needy - Retrieve All Needy Users (Failure)")
        void testGetAllNeedyUsers_Failure() throws Exception {
            when(userService.getAllNeedyUsers()).thenThrow(new RuntimeException("Failed to retrieve needy users"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_NEEDY_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Failed to retrieve needy users"));
        }
    }

    @Nested
    @DisplayName("Get Needy by Phone Number")
    class GetNeedyByPhoneNumberTests {

        @Test
        @DisplayName("GET /user/needy/{phoneNumber} - Retrieve Needy by Phone Number (Success)")
        void testGetNeedyByPhoneNumber_Success() throws Exception {
            Needy needy = new Needy();
            needy.setPhoneNumber(NEEDY_PHONE_1);

            when(userService.getNeedyByPhoneNumber(NEEDY_PHONE_1)).thenReturn(needy);

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_BY_PHONE_ENDPOINT, NEEDY_PHONE_1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.phoneNumber").value(NEEDY_PHONE_1));
        }

        @Test
        @DisplayName("GET /user/needy/{phoneNumber} - Retrieve Needy by Phone Number (Not Found)")
        void testGetNeedyByPhoneNumber_NotFound() throws Exception {
            when(userService.getNeedyByPhoneNumber(NEEDY_PHONE_2))
                    .thenThrow(new UserDoesntExistsException("User not found"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_BY_PHONE_ENDPOINT, NEEDY_PHONE_2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"));
        }
    }

    @Nested
    @DisplayName("Get Donor by Phone Number")
    class GetDonorByPhoneNumberTests {

        @Test
        @DisplayName("GET /user/donor/{phoneNumber} - Retrieve Donor by Phone Number (Success)")
        void testGetDonorByPhoneNumber_Success() throws Exception {
            Donor donor = new Donor();
            donor.setPhoneNumber(DONOR_PHONE_1);

            when(userService.getDonorByPhoneNumber(DONOR_PHONE_1)).thenReturn(donor);

            mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_BY_PHONE_ENDPOINT, DONOR_PHONE_1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.phoneNumber").value(DONOR_PHONE_1));
        }

        @Test
        @DisplayName("GET /user/donor/{phoneNumber} - Retrieve Donor by Phone Number (Not Found)")
        void testGetDonorByPhoneNumber_NotFound() throws Exception {
            when(userService.getDonorByPhoneNumber(DONOR_PHONE_2))
                    .thenThrow(new UserDoesntExistsException("User not found"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_BY_PHONE_ENDPOINT, DONOR_PHONE_2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"));
        }
    }

    @Nested
    @DisplayName("Get Donor by ID")
    class GetDonorByIdTests {

        @Test
        @DisplayName("GET /user/donor/donorId/{donorId} - Retrieve Donor by ID (Success)")
        void testGetDonorById_Success() throws Exception {
            Donor donor = new Donor();
            donor.setId(donorId1);

            when(userService.getDonorById(donorId1)).thenReturn(donor);

            mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_BY_ID_ENDPOINT, donorId1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(donorId1));
        }

        @Test
        @DisplayName("GET /user/donor/donorId/{donorId} - Retrieve Donor by ID (Not Found)")
        void testGetDonorById_NotFound() throws Exception {
            when(userService.getDonorById(donorId2)).thenThrow(new UserDoesntExistsException("User not found"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_BY_ID_ENDPOINT, donorId2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"));
        }
    }

    @Nested
    @DisplayName("Get Pending Donors")
    class GetDonorPendingTests {

        @Test
        @DisplayName("GET /user/donor/pending - Retrieve Pending Donors")
        void testGetDonorPending_Success() throws Exception {
            Donor donor = new Donor();
            List<Donor> pendingDonors = Collections.singletonList(donor);

            when(userService.getDonorsPending()).thenReturn(pendingDonors);

            mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_PENDING_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").exists());
        }

        @Test
        @DisplayName("GET /user/donor/pending - Retrieve Pending Donors (Failure)")
        void testGetDonorPending_Failure() throws Exception {
            when(userService.getDonorsPending()).thenThrow(new RuntimeException("Failed to retrieve pending donors"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_PENDING_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Failed to retrieve pending donors"));
        }
    }

    @Nested
    @DisplayName("Get Approved Donors")
    class GetDonorApprovedTests {

        @Test
        @DisplayName("GET /user/donor/approved - Retrieve Approved Donors")
        void testGetDonorApproved_Success() throws Exception {
            Donor donor = new Donor();
            List<Donor> approvedDonors = Collections.singletonList(donor);

            when(userService.getDonorsApproved()).thenReturn(approvedDonors);

            mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_APPROVED_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").exists());
        }

        @Test
        @DisplayName("GET /user/donor/approved - Retrieve Approved Donors (Failure)")
        void testGetDonorApproved_Failure() throws Exception {
            when(userService.getDonorsApproved()).thenThrow(new RuntimeException("Failed to retrieve approved donors"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_APPROVED_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Failed to retrieve approved donors"));
        }
    }

    @Nested
    @DisplayName("Update Donor")
    class UpdateDonorTests {

        @Test
        @DisplayName("PUT /user/donor - Update Donor (Success)")
        void testUpdateDonor_Success() throws Exception {
            Donor donor = new Donor();
            donor.setId(donorId1);
            donor.setFirstName(DONOR_FIRST_NAME_1);
            donor.setPhoneNumber(DONOR_PHONE_1);
            donor.setAddress(DONOR_ADDRESS);

            doNothing().when(userService).updateDonor(any(Donor.class));

            mockMvc.perform(put(BASE_ENDPOINT + UPDATE_DONOR_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(donor)))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).updateDonor(any(Donor.class));
        }

        @Test
        @DisplayName("PUT /user/donor - Update Donor (Failure)")
        void testUpdateDonor_Failure() throws Exception {
            Donor donor = new Donor();
            donor.setId(donorId1);
            donor.setFirstName(DONOR_FIRST_NAME_1);

            doThrow(new UserDoesntExistsException("User not found")).when(userService).updateDonor(any(Donor.class));

            mockMvc.perform(put(BASE_ENDPOINT + UPDATE_DONOR_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(donor)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"));

            verify(userService, times(1)).updateDonor(any(Donor.class));
        }
    }

    @Nested
    @DisplayName("Delete Donor")
    class DeleteDonorTests {

        @Test
        @DisplayName("DELETE /user/donor/{id} - Delete Donor (Success)")
        void testDeleteDonor_Success() throws Exception {
            doNothing().when(userService).deleteDonor(donorId1);

            mockMvc.perform(delete(BASE_ENDPOINT + DELETE_DONOR_ENDPOINT, donorId1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).deleteDonor(donorId1);
        }

        @Test
        @DisplayName("DELETE /user/donor/{id} - Delete Donor (Failure)")
        void testDeleteDonor_Failure() throws Exception {
            doThrow(new UserDoesntExistsException("User not found")).when(userService).deleteDonor(donorId2);

            mockMvc.perform(delete(BASE_ENDPOINT + DELETE_DONOR_ENDPOINT, donorId2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("User not found"));

            verify(userService, times(1)).deleteDonor(donorId2);
        }
    }
}
