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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    private static final String USER_ENDPOINT = "/user";
    private static final String ALL_USERS_ENDPOINT = "/Allusers";
    private static final String DONORS_ENDPOINT = "/donors";
    private static final String NEEDY_ENDPOINT = "/needy";
    private static final String NEEDY_BY_PHONE_ENDPOINT = "/needy/{phoneNumber}";
    private static final String DONOR_BY_PHONE_ENDPOINT = "/donor/{phoneNumber}";
    private static final String DONOR_BY_ID_ENDPOINT = "/donor/donorId/{donorId}";
    private static final String DONOR_PENDING_ENDPOINT = "/donor/pending";
    private static final String DONOR_APPROVED_ENDPOINT = "/donor/approved";
    private static final String UPDATE_DONOR_ENDPOINT = "/donor";
    private static final String DELETE_DONOR_ENDPOINT = "/donor/{id}";
    private static final String DONOR_ADDRESS_ENDPOINT = "/donor/donorLoc/{donorId}";
    private static final String DONOR_NAME_ENDPOINT = "/donor/donorName/{donorId}";
    private static final String DONOR_PHONE_ENDPOINT = "/donor/donorPhone/{donorId}";

    private static final String DONOR_FIRST_NAME_1 = "Alice";
    private static final String DONOR_FIRST_NAME_2 = "Bob";
    private static final String DONOR_PHONE_1 = "0500000000";
    private static final String DONOR_PHONE_2 = "0500000001";

    private static final String NEEDY_FIRST_NAME_1 = "Charlie";
    private static final String NEEDY_FIRST_NAME_2 = "Diana";
    private static final String NEEDY_PHONE_1 = "0510000000";
    private static final String NEEDY_PHONE_2 = "0510000001";

    @Test
    void testGetAllUsers() throws Exception {
        BaseUser user1 = new Donor();
        user1.setFirstName(DONOR_FIRST_NAME_1);
        BaseUser user2 = new Needy();
        user2.setFirstName(NEEDY_FIRST_NAME_1);

        when(userService.getAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get(USER_ENDPOINT + ALL_USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(DONOR_FIRST_NAME_1))
                .andExpect(jsonPath("$[1].firstName").value(NEEDY_FIRST_NAME_1));
    }

    @Test
    void testGetAllDonors() throws Exception {
        Donor donor1 = new Donor();
        donor1.setPhoneNumber(DONOR_PHONE_1);
        Donor donor2 = new Donor();
        donor2.setPhoneNumber(DONOR_PHONE_2);

        when(userService.getAllDonors()).thenReturn(Arrays.asList(donor1, donor2));

        mockMvc.perform(get(USER_ENDPOINT + DONORS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phoneNumber").value(DONOR_PHONE_1))
                .andExpect(jsonPath("$[1].phoneNumber").value(DONOR_PHONE_2));
    }

    @Test
    void testGetAllNeedyUsers() throws Exception {
        Needy needy1 = new Needy();
        needy1.setPhoneNumber(NEEDY_PHONE_1);
        Needy needy2 = new Needy();
        needy2.setPhoneNumber(NEEDY_PHONE_2);

        when(userService.getAllNeedyUsers()).thenReturn(Arrays.asList(needy1, needy2));

        mockMvc.perform(get(USER_ENDPOINT + NEEDY_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phoneNumber").value(NEEDY_PHONE_1))
                .andExpect(jsonPath("$[1].phoneNumber").value(NEEDY_PHONE_2));
    }

    @Test
    void testGetNeedyByPhoneNumber_Success() throws Exception {
        Needy needy = new Needy();
        needy.setPhoneNumber(NEEDY_PHONE_1);

        when(userService.getNeedyByPhoneNumber(NEEDY_PHONE_1)).thenReturn(needy);

        mockMvc.perform(get(USER_ENDPOINT + NEEDY_BY_PHONE_ENDPOINT, NEEDY_PHONE_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(NEEDY_PHONE_1));
    }

    @Test
    void testGetNeedyByPhoneNumber_NotFound() throws Exception {
        when(userService.getNeedyByPhoneNumber(NEEDY_PHONE_2))
                .thenThrow(new UserDoesntExistsException("User not found"));

        mockMvc.perform(get(USER_ENDPOINT + NEEDY_BY_PHONE_ENDPOINT, NEEDY_PHONE_2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testGetDonorByPhoneNumber_Success() throws Exception {
        Donor donor = new Donor();
        donor.setPhoneNumber(DONOR_PHONE_1);

        when(userService.getDonorByPhoneNumber(DONOR_PHONE_1)).thenReturn(donor);

        mockMvc.perform(get(USER_ENDPOINT + DONOR_BY_PHONE_ENDPOINT, DONOR_PHONE_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(DONOR_PHONE_1));
    }

    @Test
    void testGetDonorByPhoneNumber_NotFound() throws Exception {
        when(userService.getDonorByPhoneNumber(DONOR_PHONE_2))
                .thenThrow(new UserDoesntExistsException("User not found"));

        mockMvc.perform(get(USER_ENDPOINT + DONOR_BY_PHONE_ENDPOINT, DONOR_PHONE_2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testGetDonorById_Success() throws Exception {
        long donorId = 1L;
        Donor donor = new Donor();
        donor.setId(donorId);

        when(userService.getDonorById(donorId)).thenReturn(donor);

        mockMvc.perform(get(USER_ENDPOINT + DONOR_BY_ID_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(donorId));
    }

    @Test
    void testGetDonorById_NotFound() throws Exception {
        long donorId = 2L;
        when(userService.getDonorById(donorId)).thenThrow(new UserDoesntExistsException("User not found"));

        mockMvc.perform(get(USER_ENDPOINT + DONOR_BY_ID_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testGetDonorPending() throws Exception {
        Donor donor = new Donor();
        List<Donor> pendingDonors = Collections.singletonList(donor);

        when(userService.getDonorsPending()).thenReturn(pendingDonors);

        mockMvc.perform(get(USER_ENDPOINT + DONOR_PENDING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testGetDonorApproved() throws Exception {
        Donor donor = new Donor();
        List<Donor> approvedDonors = Collections.singletonList(donor);

        when(userService.getDonorsApproved()).thenReturn(approvedDonors);

        mockMvc.perform(get(USER_ENDPOINT + DONOR_APPROVED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testUpdateDonor() throws Exception {
        Donor donor = new Donor();
        donor.setId(1L);
        donor.setFirstName("UpdatedName");

        doNothing().when(userService).updateDonor(any(Donor.class));

        mockMvc.perform(put(USER_ENDPOINT + UPDATE_DONOR_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donor)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateDonor_Failure() throws Exception {
        Donor donor = new Donor();
        donor.setId(2L);

        doThrow(new UserDoesntExistsException("User not found")).when(userService).updateDonor(any(Donor.class));

        mockMvc.perform(put(USER_ENDPOINT + UPDATE_DONOR_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donor)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testDeleteDonor_Success() throws Exception {
        long donorId = 1L;
        doNothing().when(userService).deleteDonor(donorId);

        mockMvc.perform(delete(USER_ENDPOINT + DELETE_DONOR_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDonor_Failure() throws Exception {
        long donorId = 2L;
        doThrow(new UserDoesntExistsException("User not found")).when(userService).deleteDonor(donorId);

        mockMvc.perform(delete(USER_ENDPOINT + DELETE_DONOR_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testGetDonorAddressById_Success() throws Exception {
        long donorId = 1L;
        Donor donor = new Donor();
        donor.setAddress("123 Main St");

        when(userService.getDonorById(donorId)).thenReturn(donor);

        mockMvc.perform(get(USER_ENDPOINT + DONOR_ADDRESS_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("123 Main St"));
    }

    @Test
    void testGetDonorAddressById_NotFound() throws Exception {
        long donorId = 2L;
        when(userService.getDonorById(donorId)).thenThrow(new UserDoesntExistsException("User not found"));

        mockMvc.perform(get(USER_ENDPOINT + DONOR_ADDRESS_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testGetDonorNameById_Success() throws Exception {
        long donorId = 1L;
        Donor donor = new Donor();
        donor.setFirstName("John");
        donor.setLastName("Doe");

        when(userService.getDonorById(donorId)).thenReturn(donor);

        mockMvc.perform(get(USER_ENDPOINT + DONOR_NAME_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("John Doe"));
    }

    @Test
    void testGetDonorNameById_NotFound() throws Exception {
        long donorId = 2L;
        when(userService.getDonorById(donorId)).thenThrow(new UserDoesntExistsException("User not found"));

        mockMvc.perform(get(USER_ENDPOINT + DONOR_NAME_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testGetDonorPhoneNumberById_Success() throws Exception {
        long donorId = 1L;
        Donor donor = new Donor();
        donor.setPhoneNumber(DONOR_PHONE_1);

        when(userService.getDonorById(donorId)).thenReturn(donor);

        mockMvc.perform(get(USER_ENDPOINT + DONOR_PHONE_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(DONOR_PHONE_1));
    }

    @Test
    void testGetDonorPhoneNumberById_NotFound() throws Exception {
        long donorId = 2L;
        when(userService.getDonorById(donorId)).thenThrow(new UserDoesntExistsException("User not found"));

        mockMvc.perform(get(USER_ENDPOINT + DONOR_PHONE_ENDPOINT, donorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }
}
