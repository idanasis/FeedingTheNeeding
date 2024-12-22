package Project.Final.FeedingTheNeeding.User.Controller;


import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import Project.Final.FeedingTheNeeding.social.controller.NeederTrackingController;
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

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    final String NAME1 = "name1", NAME2 = "name2";
    final String PHONE1 = "0500000000", PHONE2 = "0500000001";



    @Test
    void testGetAllUsers() throws Exception {
        BaseUser user1 = new Donor();
        BaseUser user2 = new Needy();

        user1.setFirstName(NAME1);
        user2.setFirstName(NAME2);

        when(userService.getAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user/Allusers").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(NAME1))
                .andExpect(jsonPath("$[1].firstName").value(NAME2));
    }

    @Test
    void testGetAllDonors() throws Exception {
        Donor donor1 = new Donor();
        donor1.setPhoneNumber(PHONE1);
        Donor donor2 = new Donor();
        donor2.setPhoneNumber(PHONE2);

        when(userService.getAllDonors()).thenReturn(Arrays.asList(donor1, donor2));

        mockMvc.perform(get("/user/donors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phoneNumber").value(PHONE1))
                .andExpect(jsonPath("$[1].phoneNumber").value(PHONE2));
    }

    @Test
    void testGetAllNeedyUsers() throws Exception {
        Needy needy1 = new Needy();
        needy1.setPhoneNumber(PHONE1);
        Needy needy2 = new Needy();
        needy2.setPhoneNumber(PHONE2);

        when(userService.getAllNeedyUsers()).thenReturn(Arrays.asList(needy1, needy2));

        mockMvc.perform(get("/user/needy")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phoneNumber").value(PHONE1))
                .andExpect(jsonPath("$[1].phoneNumber").value(PHONE2));
    }

    @Test
    void testGetNeedyByPhoneNumber_Success() throws Exception {
        Needy needy = new Needy();
        needy.setPhoneNumber(PHONE1);

        when(userService.getNeedyByPhoneNumber(PHONE1)).thenReturn(needy);

        mockMvc.perform(get("/user/needy/{phoneNumber}", PHONE1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(PHONE1));
    }

    @Test
    void testGetNeedyByPhoneNumber_NotFound() throws Exception {
        when(userService.getNeedyByPhoneNumber(PHONE2))
                .thenThrow(new UserDoesntExistsException("User not found"));

        mockMvc.perform(get("/user/needy/{phoneNumber}", PHONE2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));
    }

    @Test
    void testGetDonorByPhoneNumber_Success() throws Exception {
        Donor donor = new Donor();
        donor.setPhoneNumber(PHONE1);

        when(userService.getDonorByPhoneNumber(PHONE1)).thenReturn(donor);

        mockMvc.perform(get("/user/donor/{phoneNumber}", PHONE1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value(PHONE1));
    }

    @Test
    void testGetDonorByPhoneNumber_NotFound() throws Exception {
        when(userService.getDonorByPhoneNumber(PHONE2))
                .thenThrow(new UserDoesntExistsException("User not found"));

        mockMvc.perform(get("/user/donor/{phoneNumber}", PHONE2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));
    }
}
