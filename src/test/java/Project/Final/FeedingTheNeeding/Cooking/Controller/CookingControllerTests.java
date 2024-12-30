package Project.Final.FeedingTheNeeding.Cooking.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Controller.CookController;
import Project.Final.FeedingTheNeeding.cook.Controller.ConstraintMapper;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.DTO.PendingConstraintDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;
import java.util.Collections;

@WebMvcTest(CookController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
public class CookingControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CookingService cookingService;

    @MockBean
    private ConstraintMapper mapper;

    private CookConstraints validConstraints;
    private PendingConstraintDTO validConstraintDTO;

    private static final long CONSTRAINT_ID = 1L;
    private static final String VALID_START_TIME = "14:30";
    private static final String VALID_END_TIME = "16:30";
    private static final int PLATES_NUM = 5;
    private static final String LOCATION = "123 Main Street";
    private static final LocalDate VALID_DATE = LocalDate.of(2024, 12, 25);

    @BeforeEach
    void setUp() {
        validConstraints = new CookConstraints(
                CONSTRAINT_ID,
                VALID_START_TIME,
                VALID_END_TIME,
                PLATES_NUM,
                LOCATION,
                VALID_DATE,
                Status.Pending
        );

        validConstraintDTO = new PendingConstraintDTO(
                CONSTRAINT_ID,
                VALID_START_TIME,
                VALID_END_TIME,
                PLATES_NUM,
                LOCATION,
                VALID_DATE,
                Status.Pending
        );
    }

    @Test
    void testSubmitConstraints() throws Exception {
        when(cookingService.submitConstraints(any(CookConstraints.class)))
                .thenReturn(validConstraints);

        mockMvc.perform(post("/cooking/submit/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConstraints)))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).submitConstraints(any(CookConstraints.class));
    }

    @Test
    void testSubmitConstraintsFail() throws Exception {
        when(cookingService.submitConstraints(any(CookConstraints.class)))
                .thenThrow(new IllegalArgumentException("Invalid constraints"));

        mockMvc.perform(post("/cooking/submit/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConstraints)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveConstraint() throws Exception {
        doNothing().when(cookingService).removeConstraint(any(CookConstraints.class));

        mockMvc.perform(delete("/cooking/remove/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConstraints)))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).removeConstraint(any(CookConstraints.class));
    }

    @Test
    void testRemoveConstraintFail() throws Exception {
        doThrow(new IllegalArgumentException("Constraint not found"))
                .when(cookingService).removeConstraint(any(CookConstraints.class));

        mockMvc.perform(delete("/cooking/remove/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConstraints)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCookConstraints() throws Exception {
        when(cookingService.getCookConstraints(CONSTRAINT_ID))
                .thenReturn(Collections.singletonList(validConstraints));

        mockMvc.perform(get("/cooking/constraints/cook/{cookId}", CONSTRAINT_ID))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).getCookConstraints(CONSTRAINT_ID);
    }

    @Test
    void testGetCookHistory() throws Exception {
        when(cookingService.getCookHistory(CONSTRAINT_ID))
                .thenReturn(Collections.singletonList(validConstraints));

        mockMvc.perform(get("/cooking/cook/{cookId}", CONSTRAINT_ID))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).getCookHistory(CONSTRAINT_ID);
    }

    @Test
    void testGetAcceptedConstraintsByDate() throws Exception {
        when(cookingService.getAcceptedCookByDate(VALID_DATE))
                .thenReturn(Collections.singletonList(validConstraints));

        mockMvc.perform(get("/cooking/getAccepted/{date}", VALID_DATE))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).getAcceptedCookByDate(VALID_DATE);
    }

    @Test
    void testGetPendingConstraintsByDate() throws Exception {
        when(cookingService.getPendingConstraints(VALID_DATE))
                .thenReturn(Collections.singletonList(validConstraints));
        when(mapper.toDTO(any(CookConstraints.class)))
                .thenReturn(validConstraintDTO);

        mockMvc.perform(get("/cooking/getPending/{date}", VALID_DATE))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).getPendingConstraints(VALID_DATE);
        verify(mapper, times(1)).toDTO(any(CookConstraints.class));
    }

    @Test
    void testAcceptConstraintStatus() throws Exception {
        when(cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted))
                .thenReturn(validConstraints);

        mockMvc.perform(post("/cooking/acceptConstraint/{constraintId}", CONSTRAINT_ID))
                .andExpect(status().isOk());

        verify(cookingService, times(1))
                .changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted);
    }

    @Test
    void testRejectConstraintStatus() throws Exception {
        when(cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Declined))
                .thenReturn(validConstraints);

        mockMvc.perform(post("/cooking/rejectConstraint/{constraintId}", CONSTRAINT_ID))
                .andExpect(status().isOk());

        verify(cookingService, times(1))
                .changeStatusForConstraint(CONSTRAINT_ID, Status.Declined);
    }
}