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
        NeederController.class
})
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SecurityConfig.class})
@DisplayName("Staff Permissions Endpoints Security Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StaffPermissionsSecurityTest extends BaseSecurityTest {

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

    private final String PHONE_NUMBER = "0500000000", PASSWORD = "123123123";
    private final String EMAIL = "test@test.com", FIRSTNAME = "firstname", LASTNAME = "lastname", ADDRESS = "address";
    private final int FAMILY_SIZE = 2;

    @BeforeEach
    void setup() {
        when(cookingService.getCookConstraints(anyLong()))
                .thenReturn(Collections.emptyList());
        when(drivingFascade.getDateConstraints(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(neederTrackingService.getAllNeedersTrackingsByDate(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(needyService.getAllNeedyUsers())
                .thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("STAFF can GET /cooking/constraints/cook/{id}")
    @WithMockUser(roles = "STAFF")
    void staffCanGetCookConstraints() throws Exception {
        mockMvc.perform(get("/cooking/constraints/cook/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("STAFF can POST /cooking/submit/constraints")
    @WithMockUser(roles = "STAFF")
    void staffCanSubmitCookingConstraints() throws Exception {
        CookConstraints c = new CookConstraints();
        mockMvc.perform(post("/cooking/submit/constraints")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("STAFF can GET /driving/constraints/{date}")
    @WithMockUser(roles = "STAFF")
    void staffCanGetDrivingDateConstraints() throws Exception {
        mockMvc.perform(get("/driving/constraints/2025-05-15"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("STAFF can POST /needer")
    @WithMockUser(roles = "STAFF")
    void staffCanCreateOrUpdateNeedy() throws Exception {
        String body = "{\"firstName\":\"x\",\"lastName\":\"y\",\"phoneNumber\":\"0500000001\",\"address\":\"addr\",\"street\":\"st\",\"familySize\":4}";
        mockMvc.perform(post("/needer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
