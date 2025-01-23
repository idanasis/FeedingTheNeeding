package Project.Final.FeedingTheNeeding.Cooking.Service;

import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.Exceptions.CookConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Repository.CookConstraintsRepository;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;

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

    @InjectMocks
    private CookingService cookingService;

    private CookConstraints validConstraint;
    private List<CookConstraints> constraintsList;

    private static final long CONSTRAINT_ID = 1L;
    private static final long COOK_ID = 100L;
    private static final LocalDate VALID_DATE = LocalDate.now();
    private static final String START_TIME = "09:00";
    private static final String END_TIME = "17:00";
    private static final String LOCATION = "123 Main St";
    private static final Map<String, Integer> CONSTRAINTS = new HashMap<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test constraints map
        CONSTRAINTS.put("maxPlates", 5);
        CONSTRAINTS.put("availableTime", 120);

        validConstraint = new CookConstraints(
                CONSTRAINT_ID,
                COOK_ID,
                START_TIME,
                END_TIME,
                CONSTRAINTS,
                LOCATION,
                VALID_DATE,
                Status.Pending
        );

        constraintsList = new ArrayList<>();
        constraintsList.add(validConstraint);
    }

    @Test
    void testSubmitConstraints() {
        when(ccr.save(validConstraint)).thenReturn(validConstraint);

        CookConstraints savedConstraint = cookingService.submitConstraints(validConstraint);

        assertNotNull(savedConstraint);
        assertEquals(CONSTRAINT_ID, savedConstraint.getConstraintId());
        assertEquals(COOK_ID, savedConstraint.getCookId());
        assertEquals(START_TIME, savedConstraint.getStartTime());
        assertEquals(END_TIME, savedConstraint.getEndTime());
        assertEquals(LOCATION, savedConstraint.getLocation());
        assertEquals(CONSTRAINTS, savedConstraint.getConstraints());
        verify(ccr, times(1)).save(validConstraint);
    }

    @Test
    void testUpdateConstraint() {
        Map<String, Integer> newConstraints = new HashMap<>();
        newConstraints.put("maxPlates", 8);
        newConstraints.put("prepTime", 45);

        when(ccr.findConstraintsByConstraintId(CONSTRAINT_ID)).thenReturn(constraintsList);
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        cookingService.updateConstraint(CONSTRAINT_ID, newConstraints);

        verify(ccr, times(1)).findConstraintsByConstraintId(CONSTRAINT_ID);
        verify(ccr, times(1)).save(argThat(constraint ->
                constraint.getConstraints().equals(newConstraints)
        ));
    }

    @Test
    void testRemoveConstraint() {
        when(ccr.findByConstraintIdAndDate(CONSTRAINT_ID, VALID_DATE))
                .thenReturn(Optional.of(validConstraint));

        cookingService.removeConstraint(validConstraint);

        verify(ccr, times(1)).delete(validConstraint);
    }

    @Test
    void testRemoveConstraintNotFound() {
        when(ccr.findByConstraintIdAndDate(CONSTRAINT_ID, VALID_DATE))
                .thenReturn(Optional.empty());

        assertThrows(CookConstraintsNotExistException.class,
                () -> cookingService.removeConstraint(validConstraint));
    }

    @Test
    void testGetCookConstraints() {
        when(ccr.findConstraintsByCookId(COOK_ID)).thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getCookConstraints(COOK_ID);

        assertEquals(1, result.size());
        assertEquals(constraintsList, result);
        verify(ccr, times(1)).findConstraintsByCookId(COOK_ID);
    }

    @Test
    void testGetLatestCookConstraints() {
        // Create a future constraint
        LocalDate futureDate = VALID_DATE.plusDays(1);
        CookConstraints futureConstraint = new CookConstraints(
                2L, COOK_ID, START_TIME, END_TIME, CONSTRAINTS, LOCATION, futureDate, Status.Pending
        );

        // Create a past constraint
        LocalDate pastDate = VALID_DATE.minusDays(1);
        CookConstraints pastConstraint = new CookConstraints(
                3L, COOK_ID, START_TIME, END_TIME, CONSTRAINTS, LOCATION, pastDate, Status.Pending
        );

        List<CookConstraints> allConstraints = Arrays.asList(validConstraint, futureConstraint, pastConstraint);

        when(ccr.findConstraintsByCookId(COOK_ID)).thenReturn(allConstraints);

        List<CookConstraints> result = cookingService.getLatestCookConstraints(COOK_ID, VALID_DATE);

        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(c -> c.getDate().isBefore(VALID_DATE)));
        verify(ccr, times(1)).findConstraintsByCookId(COOK_ID);
    }

    @Test
    void testGetAcceptedCookByDate() {
        CookConstraints acceptedConstraint = new CookConstraints(
                CONSTRAINT_ID, COOK_ID, START_TIME, END_TIME, CONSTRAINTS, LOCATION, VALID_DATE, Status.Accepted
        );
        List<CookConstraints> acceptedList = Collections.singletonList(acceptedConstraint);

        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Accepted))
                .thenReturn(acceptedList);

        List<CookConstraints> result = cookingService.getAcceptedCookByDate(VALID_DATE);

        assertEquals(1, result.size());
        assertEquals(Status.Accepted, result.get(0).getStatus());
        verify(ccr, times(1)).findConstraintsByDateAndStatus(VALID_DATE, Status.Accepted);
    }

    @Test
    void testGetPendingConstraints() {
        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Pending))
                .thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getPendingConstraints(VALID_DATE);

        assertEquals(1, result.size());
        assertEquals(Status.Pending, result.get(0).getStatus());
        verify(ccr, times(1)).findConstraintsByDateAndStatus(VALID_DATE, Status.Pending);
    }

    @Test
    void testGetConstraintsByDate() {
        when(ccr.findConstraintsByDate(VALID_DATE)).thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getConstraintsByDate(VALID_DATE);

        assertEquals(1, result.size());
        assertEquals(VALID_DATE, result.get(0).getDate());
        verify(ccr, times(1)).findConstraintsByDate(VALID_DATE);
    }

    @Test
    void testChangeStatusForConstraint_Success() {
        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.of(validConstraint));
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        CookConstraints result = cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted);

        assertNotNull(result);
        assertEquals(Status.Accepted, result.getStatus());
        verify(ccr, times(1)).save(argThat(constraint ->
                constraint.getStatus() == Status.Accepted
        ));
    }

    @Test
    void testChangeStatusForConstraint_Declined() {
        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.of(validConstraint));
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        CookConstraints result = cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Declined);

        assertNotNull(result);
        assertEquals(Status.Declined, result.getStatus());
        verify(ccr, times(1)).save(argThat(constraint ->
                constraint.getStatus() == Status.Declined
        ));
    }

    @Test
    void testChangeStatusForConstraintNotFound() {
        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.empty());

        assertThrows(CookConstraintsNotExistException.class,
                () -> cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted));
    }
}