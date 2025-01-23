package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
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

    // Endpoint Constants
    private static final String BASE_ENDPOINT = "/user";
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
    private static final String GET_DONOR_ADDRESS_ENDPOINT = "/donor/donorLoc/{donorId}";
    private static final String GET_DONOR_NAME_ENDPOINT = "/donor/donorName/{donorId}";
    private static final String GET_DONOR_PHONE_ENDPOINT = "/donor/donorPhone/{donorId}";

    private static final String DONOR_FIRST_NAME_1 = "Alice";
    private static final String DONOR_FIRST_NAME_2 = "Bob";
    private static final String DONOR_PHONE_1 = "0500000000";
    private static final String DONOR_PHONE_2 = "0500000001";
    private static final String DONOR_ADDRESS = "address";


    private static final String NEEDY_FIRST_NAME_1 = "Charlie";
    private static final String NEEDY_FIRST_NAME_2 = "Diana";
    private static final String NEEDY_PHONE_1 = "0510000000";
    private static final String NEEDY_PHONE_2 = "0510000001";

    long donorId1 = 1L;
    long donorId2 = 2L;


    @Test
    @DisplayName("GET /user/Allusers - Retrieve All Users")
    void testGetAllUsers() throws Exception {
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
    @DisplayName("GET /user/donors - Retrieve All Donors")
    void testGetAllDonors() throws Exception {
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
    @DisplayName("GET /user/needy - Retrieve All Needy Users")
    void testGetAllNeedyUsers() throws Exception {
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

    @Test
    @DisplayName("GET /user/donor/pending - Retrieve Pending Donors")
    void testGetDonorPending() throws Exception {
        Donor donor = new Donor();
        List<Donor> pendingDonors = Collections.singletonList(donor);

        when(userService.getDonorsPending()).thenReturn(pendingDonors);

        mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_PENDING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("GET /user/donor/approved - Retrieve Approved Donors")
    void testGetDonorApproved() throws Exception {
        Donor donor = new Donor();
        List<Donor> approvedDonors = Collections.singletonList(donor);

        when(userService.getDonorsApproved()).thenReturn(approvedDonors);

        mockMvc.perform(get(BASE_ENDPOINT + GET_DONOR_APPROVED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("PUT /user/donor - Update Donor (Success)")
    void testUpdateDonor_Success() throws Exception {
        Donor donor = new Donor();
        donor.setId(donorId1);
        donor.setFirstName(DONOR_FIRST_NAME_1);

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

        doThrow(new UserDoesntExistsException("User not found")).when(userService).updateDonor(any(Donor.class));

        mockMvc.perform(put(BASE_ENDPOINT + UPDATE_DONOR_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donor)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

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
