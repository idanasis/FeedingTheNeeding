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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CookingServiceTests {

    @Mock
    private CookConstraintsRepository ccr;

    @InjectMocks
    private CookingService cookingService;

    private CookConstraints validConstraint;
    private List<CookConstraints> constraintsList;

    private static final long CONSTRAINT_ID = 1L;
    private static final LocalDate VALID_DATE = LocalDate.now();
    private static final String START_TIME = "15:00";
    private static final String END_TIME = "17:00";
    private static final int PLATES_NUM = 3;
    private static final String LOCATION = "123 Main St";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validConstraint = new CookConstraints(
                CONSTRAINT_ID,
                START_TIME,
                END_TIME,
                PLATES_NUM,
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
        assertEquals(validConstraint.getConstraintId(), savedConstraint.getConstraintId());
        assertEquals(validConstraint.getPlatesNum(), savedConstraint.getPlatesNum());
        verify(ccr, times(1)).save(validConstraint);
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

        assertThrows(
                CookConstraintsNotExistException.class,
                () -> cookingService.removeConstraint(validConstraint)
        );
    }

    @Test
    void testGetCookConstraints() {
        when(ccr.findConstraintsByConstraintId(CONSTRAINT_ID))
                .thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getCookConstraints(CONSTRAINT_ID);

        assertEquals(1, result.size());
        assertEquals(constraintsList, result);
    }

    @Test
    void testGetCookHistory() {
        when(ccr.findConstraintsByConstraintId(CONSTRAINT_ID))
                .thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getCookHistory(CONSTRAINT_ID);

        assertEquals(constraintsList, result);
    }

    @Test
    void testGetAcceptedCookByDate() {
        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Accepted))
                .thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getAcceptedCookByDate(VALID_DATE);

        assertEquals(constraintsList, result);
    }

    @Test
    void testGetPendingConstraints() {
        when(ccr.findConstraintsByDateAndStatus(VALID_DATE, Status.Pending))
                .thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getPendingConstraints(VALID_DATE);

        assertEquals(constraintsList, result);
    }

    @Test
    void testChangeStatusForConstraint() {
        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.of(validConstraint));
        when(ccr.save(any(CookConstraints.class))).thenReturn(validConstraint);

        CookConstraints result = cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted);

        assertNotNull(result);
        assertEquals(Status.Accepted, result.getStatus());
        verify(ccr, times(1)).save(any(CookConstraints.class));
    }

    @Test
    void testChangeStatusForConstraintNotFound() {
        when(ccr.findById(CONSTRAINT_ID)).thenReturn(Optional.empty());

        assertThrows(
                CookConstraintsNotExistException.class,
                () -> cookingService.changeStatusForConstraint(CONSTRAINT_ID, Status.Accepted)
        );
    }
}