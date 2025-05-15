package Project.Final.FeedingTheNeeding.Security;

import Project.Final.FeedingTheNeeding.Authentication.Config.SecurityConfig;
import Project.Final.FeedingTheNeeding.cook.Controller.CookController;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import Project.Final.FeedingTheNeeding.cook.Controller.ConstraintMapper;
import Project.Final.FeedingTheNeeding.driving.Controller.DrivingController;
import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import Project.Final.FeedingTheNeeding.social.controller.NeederTrackingController;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import Project.Final.FeedingTheNeeding.User.Controller.NeederController;
import Project.Final.FeedingTheNeeding.User.Service.NeedyService;
import Project.Final.FeedingTheNeeding.User.Controller.UserController;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        CookController.class,
        DrivingController.class,
        NeederTrackingController.class,
        NeederController.class,
        UserController.class
})
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SecurityConfig.class})
@DisplayName("Admin Permissions Endpoints Security Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminPermissionsSecurityTest extends BaseSecurityTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CookingService cookingService;
    @MockBean
    private DrivingFascade drivingFascade;
    @MockBean
    private NeederTrackingService neederTrackingService;
    @MockBean
    private ConstraintMapper constraintMapper;
    @MockBean
    private NeedyService needyService;
    @MockBean
    private UserService userService;

    private final String PHONE_NUMBER = "0500000000", PASSWORD = "123123123";
    private final String EMAIL = "test@test.com", FIRSTNAME = "firstname", LASTNAME = "lastname", ADDRESS = "address";
    private final int FAMILY_SIZE = 2;

    @BeforeEach
    void setup() {
        when(cookingService.getCookConstraints(anyLong()))
                .thenReturn(Collections.emptyList());
        when(drivingFascade.getDateConstraints(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(neederTrackingService.getAllNeedersTrackings())
                .thenReturn(Collections.emptyList());
        when(needyService.getAllNeedyUsers())
                .thenReturn(Collections.emptyList());
        when(userService.getAll())
                .thenReturn(Collections.emptyList());
        when(userService.getAllDonors())
                .thenReturn(Collections.emptyList());
        when(userService.getAllNeedyUsers())
                .thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("ADMIN can GET /cooking/constraints/cook/{id}")
    @WithMockUser(roles = "ADMIN")
    void adminCanGetCookConstraints() throws Exception {
        mockMvc.perform(get("/cooking/constraints/cook/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can GET /driving/constraints/{date}")
    @WithMockUser(roles = "ADMIN")
    void adminCanGetDrivingDateConstraints() throws Exception {
        mockMvc.perform(get("/driving/constraints/2025-05-15"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can GET /social")
    @WithMockUser(roles = "ADMIN")
    void adminCanGetAllSocial() throws Exception {
        mockMvc.perform(get("/social"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can POST /needer")
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateOrUpdateNeedy() throws Exception {
        String body = "{\"firstName\":\"a\",\"lastName\":\"b\",\"phoneNumber\":\"0500000000\",\"address\":\"addr\",\"street\":\"st\",\"familySize\":3}";
        mockMvc.perform(post("/needer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can GET /needer")
    @WithMockUser(roles = "ADMIN")
    void adminCanListAllNeedies() throws Exception {
        mockMvc.perform(get("/needer"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can GET /user/Allusers")
    @WithMockUser(roles = "ADMIN")
    void adminCanListAllUsers() throws Exception {
        mockMvc.perform(get("/user/Allusers"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can GET /user/donors")
    @WithMockUser(roles = "ADMIN")
    void adminCanListDonors() throws Exception {
        mockMvc.perform(get("/user/donors"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can GET /user/needy")
    @WithMockUser(roles = "ADMIN")
    void adminCanListNeedyUsers() throws Exception {
        mockMvc.perform(get("/user/needy"))
                .andExpect(status().isOk());
    }
}
