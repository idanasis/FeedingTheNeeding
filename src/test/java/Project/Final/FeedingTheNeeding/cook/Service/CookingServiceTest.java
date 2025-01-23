package Project.Final.FeedingTheNeeding.cook.Service;

import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
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
    private static final long CONSTRAINT_ID = 1L;
    private static final long COOK_ID = 100L;
    private static final LocalDate VALID_DATE = LocalDate.now();
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String START_TIME = "09:00";
    private static final String END_TIME = "17:00";
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
                Status.Pending
        );

        when(authService.getUserIDFromJWT(VALID_TOKEN)).thenReturn(COOK_ID);
        when(donorRepository.findById(COOK_ID)).thenReturn(Optional.of(validDonor));
    }

    @Test
    void testGetDonorIdFromJwt() {
        long id = cookingService.getDonorIdFromJwt(VALID_TOKEN);
        assertEquals(COOK_ID, id);
        verify(authService).getUserIDFromJWT(VALID_TOKEN);
    }

    @Test
    void testGetDonorFromId() {
        Donor donor = cookingService.getDonorFromId(COOK_ID);
        assertEquals(validDonor, donor);
        verify(donorRepository).findById(COOK_ID);
    }

    @Test
    void testGetDonorFromId_NotFound() {
        when(donorRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(UserDoesntExistsException.class, () -> cookingService.getDonorFromId(999L));
    }

    @Test
    void testSubmitConstraints() {
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        CookConstraints savedConstraint = cookingService.submitConstraints(validConstraint, VALID_TOKEN);

        assertNotNull(savedConstraint);
        assertEquals(COOK_ID, savedConstraint.getCookId());
        assertEquals(LOCATION, savedConstraint.getLocation());
        verify(ccr).save(any(CookConstraints.class));
    }

    @Test
    void testUpdateConstraint() {
        Map<String, Integer> newConstraints = new HashMap<>();
        newConstraints.put("maxPlates", 8);

        when(ccr.findConstraintsByConstraintId(CONSTRAINT_ID)).thenReturn(Collections.singletonList(validConstraint));

        cookingService.updateConstraint(CONSTRAINT_ID, newConstraints);

        verify(ccr).save(argThat(constraint ->
                constraint.getConstraints().equals(newConstraints)
        ));
    }

    @Test
    void testRemoveConstraint() {
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

    @Test
    void testGetLatestCookConstraints() {
        LocalDate futureDate = VALID_DATE.plusDays(1);
        CookConstraints futureConstraint = new CookConstraints(
                2L, COOK_ID, START_TIME, END_TIME, CONSTRAINTS, LOCATION, futureDate, Status.Pending
        );

        List<CookConstraints> allConstraints = Arrays.asList(validConstraint, futureConstraint);
        when(ccr.findConstraintsByCookId(COOK_ID)).thenReturn(allConstraints);

        List<CookConstraints> result = cookingService.getLatestCookConstraints(VALID_DATE, VALID_TOKEN);

        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(c -> c.getDate().isBefore(VALID_DATE)));
    }

    @Test
    void testGetAcceptedCookByDate() {
        CookConstraints acceptedConstraint = new CookConstraints(
                CONSTRAINT_ID, COOK_ID, START_TIME, END_TIME, CONSTRAINTS, LOCATION, VALID_DATE, Status.Accepted
        );

        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Accepted))
                .thenReturn(Collections.singletonList(acceptedConstraint));

        List<CookConstraints> result = cookingService.getAcceptedCookByDate(VALID_DATE);

        assertEquals(1, result.size());
        assertEquals(Status.Accepted, result.get(0).getStatus());
    }

    @Test
    void testChangeStatusForConstraint() {
        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.of(validConstraint));
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        CookConstraints result = cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted);

        assertNotNull(result);
        assertEquals(Status.Accepted, result.getStatus());
    }

    @Test
    void testChangeStatusForConstraint_NotFound() {
        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.empty());

        assertThrows(CookConstraintsNotExistException.class,
                () -> cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted));
    }
}