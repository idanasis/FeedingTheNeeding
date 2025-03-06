package Project.Final.FeedingTheNeeding.cook.Service;

import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.DTO.UserDTO;
import Project.Final.FeedingTheNeeding.cook.Exceptions.CookConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Repository.CookConstraintsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CookingServiceTests {

    @Mock
    private CookConstraintsRepository ccr;

    @Mock
    private AuthService authService;

    @Mock
    private DonorRepository donorRepository;

    @InjectMocks
    private CookingService cookingService;

    private CookConstraints validConstraint;
    private Donor validDonor;
    private UserDTO validUserDTO;
    private static final long CONSTRAINT_ID = 1L;
    private static final long COOK_ID = 100L;
    private static final LocalDate VALID_DATE = LocalDate.now();
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.token";
    private static final String START_TIME = "09:00";
    private static final String END_TIME = "17:00";
    private static final String STREET = "ד'";
    private static final String LOCATION = "123 Main St";
    private static final Map<String, Integer> CONSTRAINTS = new HashMap<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        CONSTRAINTS.put("maxPlates", 5);
        CONSTRAINTS.put("availableTime", 120);

        validDonor = new Donor();
        validDonor.setId(COOK_ID);
        validDonor.setAddress(LOCATION);

        validConstraint = new CookConstraints(
                CONSTRAINT_ID,
                COOK_ID,
                START_TIME,
                END_TIME,
                CONSTRAINTS,
                LOCATION,
                VALID_DATE,
                Status.Pending,
                STREET
        );

        validUserDTO = new UserDTO();
        validUserDTO.setDonorId(COOK_ID);
        validUserDTO.setStartTime(START_TIME);
        validUserDTO.setEndTime(END_TIME);
        validUserDTO.setConstraints(CONSTRAINTS);
        validUserDTO.setDate(VALID_DATE);

        when(authService.getUserIDFromJWT(VALID_TOKEN)).thenReturn(COOK_ID);
        when(donorRepository.findById(COOK_ID)).thenReturn(Optional.of(validDonor));
    }

    // Token and User Tests
    @Test
    void testGetDonorIdFromJwt_Success() {
        long id = cookingService.getDonorIdFromJwt(VALID_TOKEN);
        assertEquals(COOK_ID, id);
        verify(authService).getUserIDFromJWT(VALID_TOKEN);
    }

    @Test
    void testGetDonorIdFromJwt_InvalidToken() {
        when(authService.getUserIDFromJWT(INVALID_TOKEN)).thenThrow(new RuntimeException("Invalid token"));
        assertThrows(RuntimeException.class, () -> cookingService.getDonorIdFromJwt(INVALID_TOKEN));
    }

    @Test
    void testGetDonorFromId_Success() {
        Donor donor = cookingService.getDonorFromId(COOK_ID);
        assertEquals(validDonor, donor);
        verify(donorRepository).findById(COOK_ID);
    }

    @Test
    void testGetDonorFromId_NotFound() {
        when(donorRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(UserDoesntExistsException.class, () -> cookingService.getDonorFromId(999L));
    }

    // Constraint Submission Tests
    @Test
    void testSubmitConstraints_WithToken_Success() {
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        CookConstraints savedConstraint = cookingService.submitConstraints(validConstraint, VALID_TOKEN);

        assertNotNull(savedConstraint);
        assertEquals(COOK_ID, savedConstraint.getCookId());
        assertEquals(LOCATION, savedConstraint.getLocation());
        verify(ccr).save(any(CookConstraints.class));
    }

    @Test
    void testSubmitConstraints_WithToken_InvalidToken() {
        when(authService.getUserIDFromJWT(INVALID_TOKEN)).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class,
                () -> cookingService.submitConstraints(validConstraint, INVALID_TOKEN));
    }

    @Test
    void testSubmitConstraints_Direct_Success() {
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        CookConstraints result = cookingService.submitConstraints(validConstraint);

        assertNotNull(result);
        verify(ccr).save(validConstraint);
    }

    @Test
    void testSubmitConstraints_UserDTO_Success() {
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        CookConstraints result = cookingService.submitConstraints(validUserDTO);

        assertNotNull(result);
        assertEquals(COOK_ID, result.getCookId());
        assertEquals(LOCATION, result.getLocation());
        verify(ccr).save(any(CookConstraints.class));
    }

    @Test
    void testSubmitConstraints_UserDTO_UserNotFound() {
        when(donorRepository.findById(COOK_ID)).thenReturn(Optional.empty());

        assertThrows(UserDoesntExistsException.class,
                () -> cookingService.submitConstraints(validUserDTO));
    }

    // Update Constraint Tests
    @Test
    void testUpdateConstraint_Success() {
        Map<String, Integer> newConstraints = new HashMap<>();
        newConstraints.put("maxPlates", 8);

        when(ccr.findConstraintsByConstraintId(CONSTRAINT_ID))
                .thenReturn(Collections.singletonList(validConstraint));
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        cookingService.updateConstraint(CONSTRAINT_ID, newConstraints);

        verify(ccr).save(argThat(constraint ->
                constraint.getConstraints().equals(newConstraints)));
    }

    @Test
    void testUpdateConstraint_ConstraintNotFound() {
        when(ccr.findConstraintsByConstraintId(CONSTRAINT_ID))
                .thenReturn(Collections.emptyList());

        assertThrows(IndexOutOfBoundsException.class,
                () -> cookingService.updateConstraint(CONSTRAINT_ID, new HashMap<>()));
    }

    // Remove Constraint Tests
    @Test
    void testRemoveConstraint_Success() {
        when(ccr.findByConstraintIdAndDate(CONSTRAINT_ID, VALID_DATE))
                .thenReturn(Optional.of(validConstraint));

        cookingService.removeConstraint(validConstraint);
        verify(ccr).delete(validConstraint);
    }

    @Test
    void testRemoveConstraint_NotFound() {
        when(ccr.findByConstraintIdAndDate(CONSTRAINT_ID, VALID_DATE))
                .thenReturn(Optional.empty());

        assertThrows(CookConstraintsNotExistException.class,
                () -> cookingService.removeConstraint(validConstraint));
    }

    // Get Cook Constraints Tests
    @Test
    void testGetCookConstraints_Success() {
        List<CookConstraints> expectedConstraints = Collections.singletonList(validConstraint);
        when(ccr.findConstraintsByCookId(COOK_ID)).thenReturn(expectedConstraints);

        List<CookConstraints> result = cookingService.getCookConstraints(COOK_ID);

        assertEquals(expectedConstraints, result);
        verify(ccr).findConstraintsByCookId(COOK_ID);
    }

    @Test
    void testGetCookConstraints_Empty() {
        when(ccr.findConstraintsByCookId(COOK_ID)).thenReturn(Collections.emptyList());

        List<CookConstraints> result = cookingService.getCookConstraints(COOK_ID);

        assertTrue(result.isEmpty());
        verify(ccr).findConstraintsByCookId(COOK_ID);
    }

    // Get Latest Cook Constraints Tests
    @Test
    void testGetLatestCookConstraints_Success() {
        LocalDate futureDate = VALID_DATE.plusDays(1);
        CookConstraints futureConstraint = new CookConstraints(
                2L, COOK_ID, START_TIME, END_TIME, CONSTRAINTS, LOCATION, futureDate, Status.Pending,STREET
        );

        List<CookConstraints> allConstraints = Arrays.asList(validConstraint, futureConstraint);
        when(ccr.findConstraintsByCookId(COOK_ID)).thenReturn(allConstraints);

        List<CookConstraints> result = cookingService.getLatestCookConstraints(VALID_DATE, VALID_TOKEN);

        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(c -> c.getDate().isBefore(VALID_DATE)));
    }

    @Test
    void testGetLatestCookConstraints_InvalidToken() {
        when(authService.getUserIDFromJWT(INVALID_TOKEN)).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class,
                () -> cookingService.getLatestCookConstraints(VALID_DATE, INVALID_TOKEN));
    }

    // Get Accepted Cook By Date Tests
    @Test
    void testGetAcceptedCookByDate_Success() {
        List<CookConstraints> expectedConstraints = Collections.singletonList(validConstraint);
        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Accepted))
                .thenReturn(expectedConstraints);

        List<CookConstraints> result = cookingService.getAcceptedCookByDate(VALID_DATE);

        assertEquals(expectedConstraints, result);
        verify(ccr).findConstraintsByDateAndStatus(VALID_DATE, Status.Accepted);
    }

    @Test
    void testGetAcceptedCookByDate_Empty() {
        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Accepted))
                .thenReturn(Collections.emptyList());

        List<CookConstraints> result = cookingService.getAcceptedCookByDate(VALID_DATE);

        assertTrue(result.isEmpty());
    }

    // Get Pending Constraints Tests
    @Test
    void testGetPendingConstraints_Success() {
        List<CookConstraints> expectedConstraints = Collections.singletonList(validConstraint);
        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Pending))
                .thenReturn(expectedConstraints);

        List<CookConstraints> result = cookingService.getPendingConstraints(VALID_DATE);

        assertEquals(expectedConstraints, result);
        verify(ccr).findConstraintsByDateAndStatus(VALID_DATE, Status.Pending);
    }

    @Test
    void testGetPendingConstraints_Empty() {
        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Pending))
                .thenReturn(Collections.emptyList());

        List<CookConstraints> result = cookingService.getPendingConstraints(VALID_DATE);

        assertTrue(result.isEmpty());
    }

    // Get Constraints By Date Tests
    @Test
    void testGetConstraintsByDate_Success() {
        List<CookConstraints> expectedConstraints = Collections.singletonList(validConstraint);
        when(ccr.findConstraintsByDate(VALID_DATE)).thenReturn(expectedConstraints);

        List<CookConstraints> result = cookingService.getConstraintsByDate(VALID_DATE);

        assertEquals(expectedConstraints, result);
        verify(ccr).findConstraintsByDate(VALID_DATE);
    }

    @Test
    void testGetConstraintsByDate_Empty() {
        when(ccr.findConstraintsByDate(VALID_DATE)).thenReturn(Collections.emptyList());

        List<CookConstraints> result = cookingService.getConstraintsByDate(VALID_DATE);

        assertTrue(result.isEmpty());
    }

    // Change Status For Constraint Tests
    @Test
    void testChangeStatusForConstraint_Success() {
        CookConstraints updatedConstraint = new CookConstraints(
                CONSTRAINT_ID, COOK_ID, START_TIME, END_TIME, CONSTRAINTS, LOCATION, VALID_DATE, Status.Accepted,STREET
        );

        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.of(validConstraint));
        when(ccr.save(any(CookConstraints.class))).thenReturn(updatedConstraint);

        CookConstraints result = cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted);

        assertNotNull(result);
        assertEquals(Status.Accepted, result.getStatus());
        verify(ccr).save(any(CookConstraints.class));
    }

    @Test
    void testChangeStatusForConstraint_NotFound() {
        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.empty());

        assertThrows(CookConstraintsNotExistException.class,
                () -> cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted));
    }
}