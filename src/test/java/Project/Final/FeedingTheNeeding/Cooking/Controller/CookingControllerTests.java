//package Project.Final.FeedingTheNeeding.cook.Controller;
//
//import static org.hamcrest.Matchers.emptyString;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
//import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
//import Project.Final.FeedingTheNeeding.User.Model.Donor;
//import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
//import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
//import Project.Final.FeedingTheNeeding.cook.DTO.Status;
//import Project.Final.FeedingTheNeeding.cook.DTO.PendingConstraintDTO;
//import Project.Final.FeedingTheNeeding.cook.DTO.LatestConstraintsRequestDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import static org.hamcrest.Matchers.not;
//
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//@WebMvcTest(CookController.class)
//@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:application-test.properties")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import(TestSecurityConfig.class)
//public class CookingControllerTests {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private CookingService cookingService;
//
//    @MockBean
//    private ConstraintMapper mapper;
//
//    private CookConstraints validConstraints;
//    private PendingConstraintDTO validConstraintDTO;
//    private Donor validDonor;
//    private String authHeader;
//    private Map<String, Integer> constraintsMap;
//
//    private static final long CONSTRAINT_ID = 1L;
//    private static final long COOK_ID = 1L;
//    private static final String VALID_START_TIME = "14:30";
//    private static final String VALID_END_TIME = "16:30";
//    private static final String LOCATION = "123 Main Street";
//    private static final LocalDate VALID_DATE = LocalDate.of(2024, 12, 25);
//    private static final String DONOR_NAME = "John Doe";
//    private static final String PHONE_NUMBER = "1234567890";
//
//    @BeforeEach
//    void setUp() {
//        // Initialize constraints map
//        constraintsMap = new HashMap<>();
//        constraintsMap.put("platesNum", 5);
//        constraintsMap.put("maxDistance", 10);
//
//        // Initialize valid constraints
//        validConstraints = new CookConstraints(
//                CONSTRAINT_ID,
//                COOK_ID,
//                VALID_START_TIME,
//                VALID_END_TIME,
//                constraintsMap,
//                LOCATION,
//                VALID_DATE,
//                Status.Pending
//        );
//
//        // Initialize valid donor with all required fields
//        validDonor = new Donor();
//        validDonor.setId(COOK_ID);
//        validDonor.setFirstName("John");
//        validDonor.setLastName("Doe");
//        validDonor.setPhoneNumber(PHONE_NUMBER);
//        validDonor.setAddress(LOCATION);
//        validDonor.setStatus(RegistrationStatus.AVAILABLE);
//        validDonor.setEmail("john.doe@example.com");
//        validDonor.setTimeOfDonation(0.0);
//        validDonor.setVerified(true);
//        validDonor.setVerificationCodeExpiresAt(LocalDateTime.now().plusDays(1));
//
//        // Initialize validConstraintDTO with the full constructor
//        validConstraintDTO = new PendingConstraintDTO(
//                CONSTRAINT_ID,
//                DONOR_NAME,
//                VALID_START_TIME,
//                VALID_END_TIME,
//                constraintsMap,
//                LOCATION,
//                VALID_DATE,
//                Status.Pending,
//                PHONE_NUMBER
//        );
//
//        authHeader = "Bearer mock-jwt-token";
//
//        // Setup default behavior for getDonorFromJwt
//        when(cookingService.getDonorFromJwt(authHeader)).thenReturn(validDonor);
//    }
//
//    @Test
//    void testSubmitConstraints() throws Exception {
//        // Setup the expected constraints with donor information
//        CookConstraints expectedConstraints = validConstraints;
//        expectedConstraints.setLocation(validDonor.getAddress());
//        expectedConstraints.setCookId(validDonor.getId());
//
//        // Mock both method calls
//        when(cookingService.getDonorFromJwt(authHeader)).thenReturn(validDonor);
//        when(cookingService.submitConstraints(any(CookConstraints.class))).thenReturn(expectedConstraints);
//
//        mockMvc.perform(post("/cooking/submit/constraints")
//                        .header("Authorization", authHeader)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(expectedConstraints)))
//                .andExpect(status().isOk());
//                // Remove JSON content comparison if response is empty
//
//        // Verify both method calls
//        //verify(cookingService, times(1)).getDonorFromJwt(authHeader);
//        verify(cookingService, times(1)).submitConstraints(any(CookConstraints.class));
//    }
//
//    @Test
//    void testSubmitConstraintsFail() throws Exception {
//        when(cookingService.submitConstraints(any(CookConstraints.class)))
//                .thenThrow(new IllegalArgumentException("Invalid constraints"));
//
//        mockMvc.perform(post("/cooking/submit/constraints")
//                        .header("Authorization", authHeader)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validConstraints)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testSubmitConstraintsWithInvalidToken() throws Exception {
//        when(cookingService.getDonorFromJwt("Bearer invalid-token"))
//                .thenThrow(new IllegalArgumentException("invalid token"));
//
//        mockMvc.perform(post("/cooking/submit/constraints")
//                        .header("Authorization", "Bearer invalid-token")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validConstraints)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testRemoveConstraint() throws Exception {
//        doNothing().when(cookingService).removeConstraint(any(CookConstraints.class));
//
//        mockMvc.perform(delete("/cooking/remove/constraints")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validConstraints)))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1)).removeConstraint(any(CookConstraints.class));
//    }
//
//    @Test
//    void testUpdateConstraint() throws Exception {
//        Map<String, Integer> updates = new HashMap<>();
//        updates.put("platesNum", 10);
//
//        mockMvc.perform(post("/cooking/updateConstraint")
//                        .param("constraintId", String.valueOf(CONSTRAINT_ID))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updates)))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1)).updateConstraint(eq(CONSTRAINT_ID), any(Map.class));
//    }
//
//    @Test
//    void testGetLatestConstraints() throws Exception {
//        LatestConstraintsRequestDto request = new LatestConstraintsRequestDto();
//        request.date = VALID_DATE.toString();
//
//        when(cookingService.getDonorFromJwt(authHeader)).thenReturn(validDonor);
//        when(cookingService.getLatestCookConstraints(eq(COOK_ID), any(LocalDate.class)))
//                .thenReturn(Collections.singletonList(validConstraints));
//
//        mockMvc.perform(post("/cooking/constraints/latest")
//                        .header("Authorization", authHeader)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1)).getLatestCookConstraints(eq(COOK_ID), any(LocalDate.class));
//    }
//
//    @Test
//    void testGetAcceptedConstraintsByDate() throws Exception {
//        when(cookingService.getDonorFromJwt(authHeader)).thenReturn(validDonor);
//        when(cookingService.getAcceptedCookByDate(VALID_DATE))
//                .thenReturn(Collections.singletonList(validConstraints));
//        when(mapper.toDTO(any(CookConstraints.class), eq(DONOR_NAME), eq(PHONE_NUMBER)))
//                .thenReturn(validConstraintDTO);
//
//        mockMvc.perform(get("/cooking/getAccepted/{date}", VALID_DATE)
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1)).getAcceptedCookByDate(VALID_DATE);
//        verify(mapper, times(1)).toDTO(eq(validConstraints), eq(DONOR_NAME), eq(PHONE_NUMBER));
//    }
//
//    @Test
//    void testGetPendingConstraintsByDate() throws Exception {
//        when(cookingService.getDonorFromJwt(authHeader)).thenReturn(validDonor);
//        when(cookingService.getPendingConstraints(VALID_DATE))
//                .thenReturn(Collections.singletonList(validConstraints));
//        when(mapper.toDTO(any(CookConstraints.class), eq(DONOR_NAME), eq(PHONE_NUMBER)))
//                .thenReturn(validConstraintDTO);
//
//        mockMvc.perform(get("/cooking/getPending/{date}", VALID_DATE)
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1)).getPendingConstraints(VALID_DATE);
//        verify(mapper, times(1)).toDTO(eq(validConstraints), eq(DONOR_NAME), eq(PHONE_NUMBER));
//    }
//
//    @Test
//    void testGetConstraintsByDate() throws Exception {
//        when(cookingService.getDonorFromJwt(authHeader)).thenReturn(validDonor);
//        when(cookingService.getConstraintsByDate(VALID_DATE))
//                .thenReturn(Collections.singletonList(validConstraints));
//        when(cookingService.getDonorFromId(COOK_ID)).thenReturn(validDonor);
//        when(mapper.toDTO(any(CookConstraints.class), eq(DONOR_NAME), eq(PHONE_NUMBER)))
//                .thenReturn(validConstraintDTO);
//
//        mockMvc.perform(get("/cooking/getConstraints/{date}", VALID_DATE)
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1)).getConstraintsByDate(VALID_DATE);
//        verify(mapper, times(1)).toDTO(eq(validConstraints), eq(DONOR_NAME), eq(PHONE_NUMBER));
//    }
//
//    @Test
//    void testAcceptConstraintStatus() throws Exception {
//        when(cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted))
//                .thenReturn(validConstraints);
//
//        mockMvc.perform(post("/cooking/acceptConstraint/{constraintId}", CONSTRAINT_ID))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1))
//                .changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted);
//    }
//
//    @Test
//    void testRejectConstraintStatus() throws Exception {
//        when(cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Declined))
//                .thenReturn(validConstraints);
//
//        mockMvc.perform(post("/cooking/rejectConstraint/{constraintId}", CONSTRAINT_ID))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1))
//                .changeStatusForConstraint(CONSTRAINT_ID, Status.Declined);
//    }
//
//    @Test
//    void testUndoConstraintStatus() throws Exception {
//        when(cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Pending))
//                .thenReturn(validConstraints);
//
//        mockMvc.perform(post("/cooking/undoConstraint/{constraintId}", CONSTRAINT_ID))
//                .andExpect(status().isOk());
//
//        verify(cookingService, times(1))
//                .changeStatusForConstraint(CONSTRAINT_ID, Status.Pending);
//    }
//}