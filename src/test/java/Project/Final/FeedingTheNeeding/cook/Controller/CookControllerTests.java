package Project.Final.FeedingTheNeeding.cook.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.cook.DTO.LatestConstraintsRequestDto;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Model.Map2JsonConverter;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@WebMvcTest(CookController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
@WithMockUser(roles = "ADMIN")
public class CookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CookingService cookingService;

    @MockBean
    private ConstraintMapper constraintMapper;

    @MockBean
    private Map2JsonConverter map2JsonConverter;

    private CookConstraints cookConstraints;
    private final Long cookId = 1L;
    private final LocalDate date = LocalDate.now();
    private final String authHeader = "Bearer test-token";

    @BeforeEach
    void setUp() {
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("meals", 10);
        cookConstraints = new CookConstraints(1L, cookId, "09:00", "17:00", constraints, "Test Location", date);

        when(map2JsonConverter.convertToDatabaseColumn(any()))
                .thenReturn("{\"meals\":10}");
        when(map2JsonConverter.convertToEntityAttribute(anyString()))
                .thenReturn(constraints);
    }

//    @Test
//    void testSubmitConstraints() throws Exception {
//        when(cookingService.submitConstraints(any(CookConstraints.class), eq(authHeader)))
//                .thenReturn(cookConstraints);
//
//        String requestBody = objectMapper.writeValueAsString(cookConstraints);
//        System.out.println("Request body: " + requestBody);
//
//        mockMvc.perform(post("/cooking/submit/constraints")
//                        .header("Authorization", authHeader)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//        verify(cookingService).submitConstraints(any(CookConstraints.class), eq(authHeader));
//    }


    @Test
    void testRemoveConstraint() throws Exception {
        mockMvc.perform(delete("/cooking/remove/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cookConstraints)))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).removeConstraint(any(CookConstraints.class));
    }

    @Test
    void testUpdateConstraint() throws Exception {
        Map<String, Integer> updateMap = new HashMap<>();
        updateMap.put("meals", 15);

        mockMvc.perform(post("/cooking/updateConstraint")
                        .param("constraintId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).updateConstraint(eq(1L), any(Map.class));
    }

    @Test
    void testGetCookConstraints() throws Exception {
        when(cookingService.getCookConstraints(cookId)).thenReturn(Collections.singletonList(cookConstraints));

        mockMvc.perform(get("/cooking/constraints/cook/{cookId}", cookId))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).getCookConstraints(cookId);
    }

//    @Test
//    void testGetLatestConstraints() throws Exception {
//        LatestConstraintsRequestDto request = new LatestConstraintsRequestDto();
//        request.date = date.toString();
//
//        when(cookingService.getLatestCookConstraints(date, authHeader))
//                .thenReturn(Collections.singletonList(cookConstraints));
//
//        mockMvc.perform(post("/cooking/constraints/latest")
//                        .header("Authorization", authHeader)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1)).getLatestCookConstraints(date, authHeader);
//    }

    @Test
    void testGetAcceptedConstraintsByDate() throws Exception {
        Donor donor = new Donor();
        donor.setFirstName("John");
        donor.setLastName("Doe");
        donor.setPhoneNumber("1234567890");

        when(cookingService.getAcceptedCookByDate(date))
                .thenReturn(Collections.singletonList(cookConstraints));
        when(cookingService.getDonorFromId(anyLong())).thenReturn(donor);

        mockMvc.perform(get("/cooking/getAccepted/{date}", date))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).getAcceptedCookByDate(date);
    }

    @Test
    void testAcceptConstraintStatus() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Accepted))
                .thenReturn(cookConstraints);

        mockMvc.perform(post("/cooking/acceptConstraint/{constraintId}", 1L))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).changeStatusForConstraint(1L, Status.Accepted);
    }

    @Test
    void testRejectConstraintStatus() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Declined))
                .thenReturn(cookConstraints);

        mockMvc.perform(post("/cooking/rejectConstraint/{constraintId}", 1L))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).changeStatusForConstraint(1L, Status.Declined);
    }

    @Test
    void testUndoConstraint() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Pending))
                .thenReturn(cookConstraints);

        mockMvc.perform(post("/cooking/undoConstraint/{constraintId}", 1L))
                .andExpect(status().isOk());

        verify(cookingService, times(1)).changeStatusForConstraint(1L, Status.Pending);
    }

    // Failure test cases
//    @Test
//    void testSubmitConstraintsFail() throws Exception {
//        when(cookingService.submitConstraints(any(CookConstraints.class), anyString()))
//                .thenThrow(new IllegalArgumentException());
//
//        mockMvc.perform(post("/cooking/submit/constraints")
//                        .header("Authorization", authHeader)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(cookConstraints)))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void testGetCookConstraintsFail() throws Exception {
        when(cookingService.getCookConstraints(cookId))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/cooking/constraints/cook/{cookId}", cookId))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void testGetLatestConstraintsFail() throws Exception {
//        LatestConstraintsRequestDto request = new LatestConstraintsRequestDto();
//        request.date = date.toString();
//
//        when(cookingService.getLatestCookConstraints(any(LocalDate.class), anyString()))
//                .thenThrow(new IllegalArgumentException());
//
//        mockMvc.perform(post("/cooking/constraints/latest")
//                        .header("Authorization", authHeader)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
}