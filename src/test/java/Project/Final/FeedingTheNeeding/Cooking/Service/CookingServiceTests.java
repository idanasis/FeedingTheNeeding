package Project.Final.FeedingTheNeeding.Cooking.Service;

import Project.Final.FeedingTheNeeding.cook.Exceptions.CookConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Repository.CookConstraintsRepository;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
    private CookConstraintsRepository cookConstraintsRepository;

    @InjectMocks
    private CookingService cookingService;

    private CookConstraints cookConstraint1;
    private CookConstraints cookConstraint2;
    private List<CookConstraints> constraintsList;

    private final long cookId = 1L;
    private final LocalDate today = LocalDate.now();
    private final LocalDate yesterday = today.minusDays(1);
    private final String startTime = "15:00";
    private final String endTime = "17:00";
    private final int platesNum = 3;
    private final String location = "123 Main St";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cookConstraint1 = new CookConstraints(
                cookId,
                startTime,
                endTime,
                platesNum,
                location,
                today
        );

        cookConstraint2 = new CookConstraints(
                cookId,
                "16:00",
                "18:00",
                platesNum + 1,
                "456 Elm St",
                yesterday
        );

        constraintsList = new ArrayList<>();
        constraintsList.add(cookConstraint1);
        constraintsList.add(cookConstraint2);
    }

    @Test
    void testSubmitConstraints() {
        when(cookConstraintsRepository.save(cookConstraint1)).thenReturn(cookConstraint1);

        CookConstraints savedConstraint = cookingService.submitConstraints(cookConstraint1);

        assertNotNull(savedConstraint);
        assertEquals(cookConstraint1.getCookId(), savedConstraint.getCookId());
        assertEquals(cookConstraint1.getPlatesNum(), savedConstraint.getPlatesNum());
        assertEquals(cookConstraint1.getStartTime(), savedConstraint.getStartTime());
        assertEquals(cookConstraint1.getEndTime(), savedConstraint.getEndTime());
        verify(cookConstraintsRepository, times(1)).save(cookConstraint1);
    }

    @Test
    void testRemoveConstraint() {
        when(cookConstraintsRepository.findByCookIdAndDate(cookId, today))
                .thenReturn(Optional.of(cookConstraint1));

        cookingService.removeConstraint(cookConstraint1);

        verify(cookConstraintsRepository, times(1)).delete(cookConstraint1);
    }

    @Test
    void testRemoveConstraintNotFound() {
        when(cookConstraintsRepository.findByCookIdAndDate(cookId, today))
                .thenReturn(Optional.empty());

        assertThrows(
                CookConstraintsNotExistException.class,
                () -> cookingService.removeConstraint(cookConstraint1)
        );
    }

    @Test
    void testGetCookConstraints() {
        when(cookConstraintsRepository.findConstraintsByCookId(cookId))
                .thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getCookConstraints(cookId);

        assertEquals(2, result.size());
        assertEquals(constraintsList, result);
    }

    @Test
    void testGetCookConstraintsEmpty() {
        when(cookConstraintsRepository.findConstraintsByCookId(cookId))
                .thenReturn(new ArrayList<>());

        List<CookConstraints> result = cookingService.getCookConstraints(cookId);

        assertEquals(0, result.size());
    }

    @Test
    void testUpdateCookConstraints() {
        when(cookConstraintsRepository.findConstraintsByCookId(cookId))
                .thenReturn(constraintsList);
        when(cookConstraintsRepository.save(any(CookConstraints.class)))
                .thenReturn(cookConstraint1);

        cookingService.updateCookConstraints(cookId, cookConstraint1);

        verify(cookConstraintsRepository, times(1)).delete(any(CookConstraints.class));
        verify(cookConstraintsRepository, times(1)).save(cookConstraint1);
    }

    @Test
    void testUpdateCookConstraintsFail() {
        when(cookConstraintsRepository.findConstraintsByCookId(cookId))
                .thenReturn(new ArrayList<>());

        assertThrows(
                CookConstraintsNotExistException.class,
                () -> cookingService.updateCookConstraints(cookId, cookConstraint1)
        );
    }

    @Test
    void testGetLastConstraints() {
        when(cookConstraintsRepository.findConstraintsByCookId(cookId))
                .thenReturn(constraintsList);

        CookConstraints result = cookingService.getLastConstraints(cookId);

        assertEquals(today, result.getDate());
        assertEquals(cookConstraint1, result);
    }

    @Test
    void testGetLastConstraintsEmpty() {
        when(cookConstraintsRepository.findConstraintsByCookId(cookId))
                .thenReturn(new ArrayList<>());

        CookConstraints result = cookingService.getLastConstraints(cookId);

        assertNull(result);
    }

    @Test
    void testGetCookHistory() {
        when(cookConstraintsRepository.findConstraintsByCookId(cookId))
                .thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getCookHistory(cookId);

        assertEquals(constraintsList, result);
    }

    @Test
    void testGetAllCookOnDate() {
        when(cookConstraintsRepository.findConstraintsByDate(today))
                .thenReturn(constraintsList);

        List<CookConstraints> result = cookingService.getAllCookOnDate(today);

        assertEquals(constraintsList, result);
    }

    @Test
    void testGetAllCookOnDateEmpty() {
        when(cookConstraintsRepository.findConstraintsByDate(today))
                .thenReturn(new ArrayList<>());

        List<CookConstraints> result = cookingService.getAllCookOnDate(today);

        assertEquals(0, result.size());
    }
}