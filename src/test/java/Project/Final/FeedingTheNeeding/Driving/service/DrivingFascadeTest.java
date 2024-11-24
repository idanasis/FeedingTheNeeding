package Project.Final.FeedingTheNeeding.Driving.service;

import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import Project.Final.FeedingTheNeeding.driving.Model.Route;
import Project.Final.FeedingTheNeeding.driving.Repository.DriverConstraintsRepository;
import Project.Final.FeedingTheNeeding.driving.Repository.RouteRepository;
import Project.Final.FeedingTheNeeding.driving.exception.DriverConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.driving.exception.RouteNotFoundException;

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

class DrivingFascadeTest {

    @Mock
    private DriverConstraintsRepository driverConstraintsRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private DrivingFascade drivingFascade;

    private DriverConstraint driverConstraint;
    private DriverConstraintId driverConstraintId;
    private Route route;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        driverConstraintId = new DriverConstraintId(1L, LocalDate.of(2023, 12, 1));
        driverConstraint = new DriverConstraint(
                1L,
                LocalDate.of(2023, 12, 1),
                9,
                17,
                "Downtown",
                "Deliver parcels"
        );

        route = new Route("1", LocalDate.of(2023, 12, 1), 10);
    }

    @Test
    void testSubmitConstraint() {
        when(driverConstraintsRepository.save(driverConstraint)).thenReturn(driverConstraint);

        DriverConstraint savedConstraint = drivingFascade.submitConstraint(driverConstraint);

        assertNotNull(savedConstraint);
        assertEquals(driverConstraint.getDriverId(), savedConstraint.getDriverId());
        verify(driverConstraintsRepository, times(1)).save(driverConstraint);
    }

    @Test
    void testRemoveConstraint() {
        when(driverConstraintsRepository.findByDriverIdAndDate(1L, LocalDate.of(2023, 12, 1)))
                .thenReturn(Optional.of(driverConstraint));

        drivingFascade.removeConstraint(driverConstraintId);

        verify(driverConstraintsRepository, times(1)).delete(driverConstraint);
    }

    @Test
    void testRemoveConstraintNotFound() {
        when(driverConstraintsRepository.findByDriverIdAndDate(1L, LocalDate.of(2023, 12, 1)))
                .thenReturn(Optional.empty());

        assertThrows(
                DriverConstraintsNotExistException.class,
                () -> drivingFascade.removeConstraint(driverConstraintId)
        );
    }

    @Test
    void testGetDateConstraints() {
        List<DriverConstraint> constraints = new ArrayList<>();
        constraints.add(driverConstraint);

        when(driverConstraintsRepository.findConstraintsByDate(LocalDate.of(2023, 12, 1))).thenReturn(constraints);

        List<DriverConstraint> result = drivingFascade.getDateConstraints(LocalDate.of(2023, 12, 1));

        assertEquals(1, result.size());
        assertEquals(driverConstraint, result.get(0));
    }

    @Test
    void testGetDriverConstraints() {
        List<DriverConstraint> constraints = new ArrayList<>();
        constraints.add(driverConstraint);

        when(driverConstraintsRepository.findConstraintsByDriverId(1L)).thenReturn(constraints);

        List<DriverConstraint> result = drivingFascade.getDriverConstraints(1L);

        assertEquals(1, result.size());
        assertEquals(driverConstraint, result.get(0));
    }

    @Test
    void testSubmitRouteForDriver() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        drivingFascade.submitRouteForDriver(1L);

        assertTrue(route.isSubmitted());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testSubmitRouteForDriverNotFound() {
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> drivingFascade.submitRouteForDriver(1L));
    }

    @Test
    void testCreateRoute() {
        when(routeRepository.save(any(Route.class))).thenReturn(route);

        Route createdRoute = drivingFascade.createRoute(LocalDate.of(2023, 12, 1));

        assertNotNull(createdRoute);
        assertEquals(route.getDate(), createdRoute.getDate());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void testSetDriverIdToRoute() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(driverConstraintsRepository.findByDriverIdAndDate(1L, LocalDate.of(2023, 12, 1)))
                .thenReturn(Optional.of(driverConstraint));

        drivingFascade.setDriverIdToRoute(1L, "1");

        assertEquals("1", route.getDriverId());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testSetDriverIdToRouteConstraintNotExist() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(driverConstraintsRepository.findByDriverIdAndDate(1L, LocalDate.of(2023, 12, 1)))
                .thenReturn(Optional.empty());

        assertThrows(
                DriverConstraintsNotExistException.class,
                () -> drivingFascade.setDriverIdToRoute(1L, "1")
        );
    }

    @Test
    void testSetDriverIdToRouteRouteNotFound() {
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RouteNotFoundException.class,
                () -> drivingFascade.setDriverIdToRoute(1L, "1")
        );
    }
}
