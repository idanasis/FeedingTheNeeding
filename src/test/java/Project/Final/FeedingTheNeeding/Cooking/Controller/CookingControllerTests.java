package Project.Final.FeedingTheNeeding.Cooking.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Controller.CookController;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
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

    private CookConstraints validConstraints;
    private CookConstraints invalidConstraints;

    private final long cookId = 1L;
    private static final String VALID_START_TIME = "14:30";
    private static final String VALID_END_TIME = "16:30";
    private static final int PLATES_NUM = 5;
    private static final String ADDR = "123 Main Street";
    private static final LocalDate VALID_DATE = LocalDate.of(2024, 12, 25);

    private static final String INVALID_START_TIME = "18:00";
    private static final String INVALID_END_TIME = "20:00";
    private static final int INVALID_PLATES_NUM = 1;
    private static final String INVALID_ADDR = "Invalid Location";
    private static final LocalDate INVALID_DATE = LocalDate.of(2024, 12, 31);

    @BeforeEach
    void setUp() {
        validConstraints = new CookConstraints(
                cookId,
                VALID_START_TIME,
                VALID_END_TIME,
                PLATES_NUM,
                ADDR,
                VALID_DATE
        );

        invalidConstraints = new CookConstraints(
                cookId,
                INVALID_START_TIME,
                INVALID_END_TIME,
                INVALID_PLATES_NUM,
                INVALID_ADDR,
                INVALID_DATE
        );
    }

    @Test
    void testSubmitConstraints() throws Exception {
        when(cookingService.submitConstraints(any(CookConstraints.class))).thenReturn(validConstraints);

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
                        .content(objectMapper.writeValueAsString(invalidConstraints)))
                .andExpect(status().isBadRequest());

        verify(cookingService, times(1)).submitConstraints(any(CookConstraints.class));
    }

    @Test
    void testRemoveConstraint() throws Exception {
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

        verify(cookingService, times(1)).removeConstraint(any(CookConstraints.class));
    }

    @Test
    void testGetCookConstraints() throws Exception {
        when(cookingService.getCookConstraints(cookId))
                .thenReturn(Collections.singletonList(validConstraints));

        mockMvc.perform(get("/cooking/constraints/cook/{cookId}", cookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cookId").value(cookId))
                .andExpect(jsonPath("$[0].platesNum").value(PLATES_NUM))
                .andExpect(jsonPath("$[0].location").value(ADDR));

        verify(cookingService, times(1)).getCookConstraints(cookId);
    }

    @Test
    void testUpdateCookConstraints() throws Exception {
        mockMvc.perform(put("/cooking/constraints/update/{cookId}", cookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConstraints)))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).updateCookConstraints(eq(cookId), any(CookConstraints.class));
    }

    @Test
    void testUpdateCookConstraintsFail() throws Exception {
        doThrow(new IllegalArgumentException("Update failed"))
                .when(cookingService).updateCookConstraints(eq(cookId), any(CookConstraints.class));

        mockMvc.perform(put("/cooking/constraints/update/{cookId}", cookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidConstraints)))
                .andExpect(status().isBadRequest());

        verify(cookingService, times(1)).updateCookConstraints(eq(cookId), any(CookConstraints.class));
    }

    @Test
    void testGetCookHistory() throws Exception {
        when(cookingService.getCookHistory(cookId))
                .thenReturn(Collections.singletonList(validConstraints));

        mockMvc.perform(get("/cooking/cook/{cookId}", cookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cookId").value(cookId))
                .andExpect(jsonPath("$[0].platesNum").value(PLATES_NUM));

        verify(cookingService, times(1)).getCookHistory(cookId);
    }

    @Test
    void testGetCookHistoryFail() throws Exception {
        when(cookingService.getCookHistory(cookId))
                .thenThrow(new IllegalArgumentException("History not found"));

        mockMvc.perform(get("/cooking/cook/{cookId}", cookId))
                .andExpect(status().isBadRequest());

        verify(cookingService, times(1)).getCookHistory(cookId);
    }
}