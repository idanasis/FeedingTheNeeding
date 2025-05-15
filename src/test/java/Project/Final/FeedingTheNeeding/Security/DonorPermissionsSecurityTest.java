package Project.Final.FeedingTheNeeding.Security;

import Project.Final.FeedingTheNeeding.Authentication.Config.SecurityConfig;
import Project.Final.FeedingTheNeeding.cook.Controller.CookController;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.driving.Controller.DrivingController;
import Project.Final.FeedingTheNeeding.social.controller.NeederTrackingController;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import Project.Final.FeedingTheNeeding.cook.Controller.ConstraintMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        NeederTrackingController.class
})
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SecurityConfig.class,})
@DisplayName("Donor Permissions Security Tests")
public class DonorPermissionsSecurityTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

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

    @BeforeEach
    void setup() {
        when(cookingService.submitConstraints(any(CookConstraints.class), anyString()))
                .thenReturn(null);
        when(drivingFascade.submitConstraint(any())).thenReturn(null);
        when(neederTrackingService.getAllNeedersTrackingsByDate(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("DONOR can POST /cooking/submit/constraints")
    @WithMockUser(roles = "DONOR")
    void donorCanSubmitCookingConstraints() throws Exception {
        CookConstraints c = new CookConstraints();
        mockMvc.perform(post("/cooking/submit/constraints")
                        .header("Authorization", "Bearer dummy-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DONOR is FORBIDDEN GET /cooking/constraints/cook/{id}")
    @WithMockUser(roles = "DONOR")
    void donorCannotGetCookConstraints() throws Exception {
        mockMvc.perform(get("/cooking/constraints/cook/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DONOR can POST /driving/constraints")
    @WithMockUser(roles = "DONOR")
    void donorCanSubmitDrivingConstraint() throws Exception {
        mockMvc.perform(post("/driving/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DONOR can GET /driving/constraints/driver/futureNotApproved")
    @WithMockUser(roles = "DONOR")
    void donorCanGetDriverFutureConstraints() throws Exception {
        mockMvc.perform(get("/driving/constraints/driver/futureNotApproved")
                        .param("driverId", "42"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DONOR can GET /social/getNeededFoodByDate")
    @WithMockUser(roles = "DONOR")
    void donorCanGetNeededFoodByDate() throws Exception {
        mockMvc.perform(get("/social/getNeededFoodByDate")
                        .param("date", "2025-05-15"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DONOR is FORBIDDEN GET /social (list all)")
    @WithMockUser(roles = "DONOR")
    void donorCannotListAllSocial() throws Exception {
        mockMvc.perform(get("/social"))
                .andExpect(status().isForbidden());
    }
}
