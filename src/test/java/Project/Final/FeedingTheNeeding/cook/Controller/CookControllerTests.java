package Project.Final.FeedingTheNeeding.cook.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.cook.DTO.PendingConstraintDTO;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.DTO.UserDTO;
import Project.Final.FeedingTheNeeding.cook.Exceptions.CookConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@WebMvcTest(CookController.class)
@Import(TestSecurityConfig.class)
class CookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CookingService cookingService;

    @MockBean
    private ConstraintMapper constraintMapper;

    private CookConstraints cookConstraints;
    private Donor testDonor;
    private UserDTO testUserDTO;
    private static final Long COOK_ID = 1L;
    private static final LocalDate TEST_DATE = LocalDate.now();

    @BeforeEach
    void setUp() {
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("meals", 10);

        cookConstraints = new CookConstraints(
                1L, COOK_ID, "09:00", "17:00",
                constraints, "Test Location", TEST_DATE, Status.Pending
        );

        testDonor = new Donor();
        testDonor.setId(COOK_ID);
        testDonor.setFirstName("John");
        testDonor.setLastName("Doe");
        testDonor.setPhoneNumber("1234567890");

        testUserDTO = new UserDTO();
        testUserDTO.setDonorId(COOK_ID);
        testUserDTO.setStartTime("09:00");
        testUserDTO.setEndTime("17:00");
        testUserDTO.setConstraints(constraints);
        testUserDTO.setDate(TEST_DATE);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeConstraint_Success() throws Exception {
        doNothing().when(cookingService).removeConstraint(any(CookConstraints.class));

        mockMvc.perform(delete("/cooking/remove/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cookConstraints)))
                .andExpect(status().isOk());

        verify(cookingService).removeConstraint(any(CookConstraints.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeConstraint_Failure() throws Exception {
        doThrow(new CookConstraintsNotExistException("Not found"))
                .when(cookingService).removeConstraint(any(CookConstraints.class));

        mockMvc.perform(delete("/cooking/remove/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cookConstraints)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateConstraint_Success() throws Exception {
        Map<String, Integer> updateMap = new HashMap<>();
        updateMap.put("meals", 15);

        mockMvc.perform(post("/cooking/updateConstraint")
                        .param("constraintId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isOk());

        verify(cookingService).updateConstraint(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateConstraint_Failure() throws Exception {
        doThrow(new RuntimeException("Update failed"))
                .when(cookingService).updateConstraint(anyLong(), any());

        mockMvc.perform(post("/cooking/updateConstraint")
                        .param("constraintId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HashMap<>())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCookConstraints_Success() throws Exception {
        when(cookingService.getCookConstraints(COOK_ID))
                .thenReturn(Collections.singletonList(cookConstraints));

        mockMvc.perform(get("/cooking/constraints/cook/{cookId}", COOK_ID))
                .andExpect(status().isOk());

        verify(cookingService).getCookConstraints(COOK_ID);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCookConstraints_Failure() throws Exception {
        when(cookingService.getCookConstraints(COOK_ID))
                .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/cooking/constraints/cook/{cookId}", COOK_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCookHistory_Success() throws Exception {
        when(cookingService.getCookConstraints(COOK_ID))
                .thenReturn(Collections.singletonList(cookConstraints));

        mockMvc.perform(get("/cooking/allConstraints/cook/{cookId}", COOK_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCookHistory_Failure() throws Exception {
        when(cookingService.getCookConstraints(COOK_ID))
                .thenThrow(new RuntimeException("History not found"));

        mockMvc.perform(get("/cooking/allConstraints/cook/{cookId}", COOK_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAcceptedConstraintsByDate_Success() throws Exception {
        when(cookingService.getAcceptedCookByDate(TEST_DATE))
                .thenReturn(Collections.singletonList(cookConstraints));
        when(cookingService.getDonorFromId(COOK_ID)).thenReturn(testDonor);
        when(constraintMapper.toDTO(any(), anyString(), anyString()))
                .thenReturn(new PendingConstraintDTO());

        mockMvc.perform(get("/cooking/getAccepted/{date}", TEST_DATE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAcceptedConstraintsByDate_Failure() throws Exception {
        when(cookingService.getAcceptedCookByDate(TEST_DATE))
                .thenThrow(new RuntimeException("Failed to get accepted constraints"));

        mockMvc.perform(get("/cooking/getAccepted/{date}", TEST_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingConstraintsByDate_Success() throws Exception {
        when(cookingService.getPendingConstraints(TEST_DATE))
                .thenReturn(Collections.singletonList(cookConstraints));
        when(cookingService.getDonorFromId(COOK_ID)).thenReturn(testDonor);
        when(constraintMapper.toDTO(any(), anyString(), anyString()))
                .thenReturn(new PendingConstraintDTO());

        mockMvc.perform(get("/cooking/getPending/{date}", TEST_DATE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingConstraintsByDate_Failure() throws Exception {
        when(cookingService.getPendingConstraints(TEST_DATE))
                .thenThrow(new RuntimeException("Failed to get pending constraints"));

        mockMvc.perform(get("/cooking/getPending/{date}", TEST_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void acceptConstraintStatus_Success() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Accepted))
                .thenReturn(cookConstraints);

        mockMvc.perform(post("/cooking/acceptConstraint/{constraintId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void acceptConstraintStatus_Failure() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Accepted))
                .thenThrow(new RuntimeException("Failed to accept constraint"));

        mockMvc.perform(post("/cooking/acceptConstraint/{constraintId}", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectConstraintStatus_Success() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Declined))
                .thenReturn(cookConstraints);

        mockMvc.perform(post("/cooking/rejectConstraint/{constraintId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rejectConstraintStatus_Failure() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Declined))
                .thenThrow(new RuntimeException("Failed to reject constraint"));

        mockMvc.perform(post("/cooking/rejectConstraint/{constraintId}", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void undoConstraint_Success() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Pending))
                .thenReturn(cookConstraints);

        mockMvc.perform(post("/cooking/undoConstraint/{constraintId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void undoConstraint_Failure() throws Exception {
        when(cookingService.changeStatusForConstraint(1L, Status.Pending))
                .thenThrow(new RuntimeException("Failed to undo constraint"));

        mockMvc.perform(post("/cooking/undoConstraint/{constraintId}", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addConstraint_Success() throws Exception {
        when(cookingService.submitConstraints(any(UserDTO.class)))
                .thenReturn(cookConstraints);

        mockMvc.perform(post("/cooking/add/constraint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addConstraint_Failure() throws Exception {
        when(cookingService.submitConstraints(any(UserDTO.class)))
                .thenThrow(new RuntimeException("Failed to add constraint"));

        mockMvc.perform(post("/cooking/add/constraint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isBadRequest());
    }



}