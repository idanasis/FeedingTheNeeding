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
import java.time.LocalTime;
import java.time.Year;
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
    private static final int HOURS = 14;
    private static final int MINUTES = 30;
    private static final int YEAR = 2024;
    private static final int MONTH = 12;
    private static final int DAY = 25;
    private static final int PLATES_NUM = 5;
    private static final String ADDR = "123 Main Street";

    private static final int INVALID_HOURS = 18;
    private static final int INVALID_MINUTES = 0;
    private static final int INVALID_YEAR = 2024;
    private static final int INVALID_MONTH = 12;
    private static final int INVALID_DAY = 31;
    private static final int INVALID_PLATES_NUM = 1;
    private static final String INVALID_ADDR = "Invalid Location";

    @BeforeEach
    void setUp() {
        validConstraints = new CookConstraints(
                cookId,
                LocalTime.of(HOURS, MINUTES),
                PLATES_NUM,
                ADDR,
                LocalDate.of(YEAR, MONTH, DAY)
        );

        invalidConstraints = new CookConstraints(
                cookId,
                LocalTime.of(INVALID_HOURS, INVALID_MINUTES),
                INVALID_PLATES_NUM,
                INVALID_ADDR,
                LocalDate.of(INVALID_YEAR, INVALID_MONTH, INVALID_DAY)
        );
    }

    @Test
    void testSubmitConstraints() throws Exception {
        when(cookingService.submitConstraints(any(CookConstraints.class))).thenReturn(validConstraints);

        mockMvc.perform(post("/cooking/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConstraints)))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).submitConstraints(any(CookConstraints.class));
    }

    @Test
    void testSubmitConstraintsFail() throws Exception {
        when(cookingService.submitConstraints(any(CookConstraints.class))).thenThrow(new IllegalArgumentException("Invalid constraints"));

        mockMvc.perform(post("/cooking/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidConstraints)))
                .andExpect(status().isBadRequest());

        verify(cookingService, times(1)).submitConstraints(any(CookConstraints.class));
    }

    @Test
    void testRemoveConstraint() throws Exception {
        mockMvc.perform(delete("/cooking/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConstraints)))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).removeConstraint(validConstraints);
    }

    @Test
    void testRemoveConstraintFail() throws Exception {
        doThrow(new IllegalArgumentException("Constraint not found")).when(cookingService).removeConstraint(validConstraints);

        mockMvc.perform(delete("/cooking/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validConstraints)))
                .andExpect(status().isBadRequest());

        verify(cookingService, times(1)).removeConstraint(validConstraints);
    }

    @Test
    void testGetCookConstraints() throws Exception {
        when(cookingService.getCookConstraints(cookId)).thenReturn(Collections.singletonList(validConstraints));

        mockMvc.perform(get("/cooking/constraints/cook/{cookId}", cookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cookId").value(cookId))
                .andExpect(jsonPath("$[0].platesNum").value(5))
                .andExpect(jsonPath("$[0].location").value("123 Main Street"));

        verify(cookingService, times(1)).getCookConstraints(cookId);
    }

    @Test
    void testGetCookConstraintsFail() throws Exception {
        when(cookingService.getCookConstraints(cookId)).thenThrow(new IllegalArgumentException("Cook not found"));

        mockMvc.perform(get("/cooking/constraints/cook/{cookId}", cookId))
                .andExpect(status().isBadRequest());

        verify(cookingService, times(1)).getCookConstraints(cookId);
    }

    @Test
    void testUpdateCookConstraints() throws Exception {
        mockMvc.perform(put("/cooking/constraints/update/{cookId}", cookId)
                        .contentType("application/json") // Set content type to JSON
                        .content(objectMapper.writeValueAsString(validConstraints))) // Send object in request body
                .andExpect(status().isOk());

        verify(cookingService, times(1)).updateCookConstraints(eq(cookId), any(CookConstraints.class));
    }

    @Test
    void testUpdateCookConstraintsFail() throws Exception {
        // Arrange: Mock the service to throw an exception when invoked
        doThrow(new IllegalArgumentException("Update failed")).when(cookingService).updateCookConstraints(eq(cookId), any(CookConstraints.class));

        // Act: Perform the PUT request with invalid constraints
        mockMvc.perform(put("/cooking/constraints/update/{cookId}", cookId)
                        .contentType("application/json") // Set content type to JSON
                        .content(objectMapper.writeValueAsString(invalidConstraints))) // Pass invalid constraints in the body
                .andExpect(status().isBadRequest()); // Assert that the response status is 400 (Bad Request)

        // Assert: Verify the service method was called exactly once
        verify(cookingService, times(1)).updateCookConstraints(eq(cookId), any(CookConstraints.class));
    }

    @Test
    void testGetCookHistory() throws Exception {
        when(cookingService.getCookHistory(cookId)).thenReturn(Collections.singletonList(validConstraints));

        mockMvc.perform(get("/cooking/cook/{cookId}", cookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cookId").value(cookId))
                .andExpect(jsonPath("$[0].platesNum").value(5));

        verify(cookingService, times(1)).getCookHistory(cookId);
    }

    @Test
    void testGetCookHistoryFail() throws Exception {
        when(cookingService.getCookHistory(cookId)).thenThrow(new IllegalArgumentException("History not found"));

        mockMvc.perform(get("/cooking/cook/{cookId}", cookId))
                .andExpect(status().isBadRequest());

        verify(cookingService, times(1)).getCookHistory(cookId);
    }
}
