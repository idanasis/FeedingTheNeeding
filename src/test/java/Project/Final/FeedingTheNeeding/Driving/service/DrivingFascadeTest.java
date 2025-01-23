package Project.Final.FeedingTheNeeding.Driving.service;

import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintsDTO;
import Project.Final.FeedingTheNeeding.driving.Model.Route;
import Project.Final.FeedingTheNeeding.driving.Model.Visit;
import Project.Final.FeedingTheNeeding.driving.Model.VisitStatus;
import Project.Final.FeedingTheNeeding.driving.Repository.DriverConstraintsRepository;
import Project.Final.FeedingTheNeeding.driving.Repository.RouteRepository;
import Project.Final.FeedingTheNeeding.driving.exception.DriverConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.driving.exception.RouteNotFoundException;
import Project.Final.FeedingTheNeeding.driving.exception.VisitNotExistException;
import Project.Final.FeedingTheNeeding.social.dto.NeedySimpleDTO;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DrivingFascadeTest {

    @Mock
    private DriverConstraintsRepository driverConstraintsRepository;

    @Mock
    private RouteRepository routeRepository;
    @Mock
    private NeederTrackingService neederTrackingService;
    @Mock
    private UserService userService;

    @InjectMocks
    private DrivingFascade drivingFascade;

    private DriverConstraint driverConstraint;
    private DriverConstraintId driverConstraintId;
    private Route route;
    private Visit visit;
    private List<Route> routes = new ArrayList<>();

    final LocalDate drivingDate = LocalDate.of(2023, 12, 1);
    final long driverId = 2L,routeId = 1L;
    final int startHour = 9,endHour = 17,maxHour = 14,visitId = 1,priority = 1;
    final String location = "Downtown", request = "some description",note = "some Note",address = "Ringelbloom 24 Beer Sheva",
    lastName = "Doe",firstName = "John",phoneNumber = "0541234567";
    final Needy needy = new Needy();
    final Donor donor = new Donor();
    final NeedySimpleDTO needySimpleDTO= new NeedySimpleDTO(needy,note);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        driverConstraintId = new DriverConstraintId(driverId, LocalDate.of(2023, 12, 1));
        driverConstraint = new DriverConstraint(
                driverId,
                drivingDate,
                startHour,
                endHour,
                location,
                request
        );
        route = new Route(driverId, drivingDate);
        visit = new Visit(address, firstName, lastName, phoneNumber, maxHour, VisitStatus.Deliver, note,route,priority);
        routes.add(route);
        when(userService.getDonorById(driverId)).thenReturn(donor);

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
        when(driverConstraintsRepository.findByDriverIdAndDate(driverId, drivingDate))
                .thenReturn(Optional.of(driverConstraint));
        drivingFascade.removeConstraint(driverConstraintId);
        verify(driverConstraintsRepository, times(1)).delete(driverConstraint);
    }

    @Test
    void testRemoveConstraintNotFound() {
        when(driverConstraintsRepository.findByDriverIdAndDate(driverId, drivingDate))
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
        when(driverConstraintsRepository.findConstraintsByDate(drivingDate)).thenReturn(constraints);
        List<DriverConstraintsDTO> result = drivingFascade.getDateConstraints(drivingDate);
        DriverConstraintsDTO dto= new DriverConstraintsDTO(driverConstraint,donor);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetDriverConstraints() {
        List<DriverConstraint> constraints = new ArrayList<>();
        constraints.add(driverConstraint);
        when(driverConstraintsRepository.findConstraintsByDriverId(driverId)).thenReturn(constraints);
        List<DriverConstraint> result = drivingFascade.getDriverConstraints(driverId);
        assertEquals(1, result.size());
        assertEquals(driverConstraint, result.get(0));
    }

    @Test
    void testSubmitRouteForDriver() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        drivingFascade.submitRouteForDriver(routeId);
        assertTrue(route.isSubmitted());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testSubmitRouteForDriverNotFound() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());
        assertThrows(RouteNotFoundException.class, () -> drivingFascade.submitRouteForDriver(routeId));
    }

    @Test
    void testCreateRoute() {
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        Route createdRoute = drivingFascade.createRoute(drivingDate);
        assertNotNull(createdRoute);
        assertEquals(route.getDate(), createdRoute.getDate());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void testSetDriverIdToRoute() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(driverConstraintsRepository.findByDriverIdAndDate(driverId, drivingDate))
                .thenReturn(Optional.of(driverConstraint));
        drivingFascade.setDriverIdToRoute(routeId, driverId);
        assertEquals(driverId, route.getDriverId());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testSetDriverIdToRouteConstraintNotExist() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(driverConstraintsRepository.findByDriverIdAndDate(driverId, drivingDate))
                .thenReturn(Optional.empty());
        assertThrows(
                DriverConstraintsNotExistException.class,
                () -> drivingFascade.setDriverIdToRoute(routeId, driverId)
        );
    }

    @Test
    void testSetDriverIdToRouteRouteNotFound() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());
        assertThrows(
                RouteNotFoundException.class,
                () -> drivingFascade.setDriverIdToRoute(routeId, driverId)
        );
    }
    @Test
    void testRemoveRoute() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        drivingFascade.removeRoute(routeId);
        verify(routeRepository, times(1)).delete(route);
    }
    @Test
    void testRemoveRouteNotFound() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());
        assertThrows(RouteNotFoundException.class, () -> drivingFascade.removeRoute(routeId));
    }
    @Test
    void testGetRoute() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        Route result = drivingFascade.getRoute(routeId);
        assertEquals(route, result);
    }
    @Test
    void testGetHistory() {
        when(routeRepository.findAll()).thenReturn(routes);
        List<Route> result = drivingFascade.viewHistory();
        assertEquals(routes, result);
    }
    @Test
    void testSubmitAllRoutes() {
        when(routeRepository.findRoutesByDate(drivingDate)).thenReturn(routes);
        for (Route r : routes) {
            assertFalse(r.isSubmitted());
        }
        drivingFascade.submitAllRoutes(drivingDate);
        for (Route r : routes) {
            assertTrue(r.isSubmitted());
        }
        verify(routeRepository, times(1)).saveAll(routes);
    }
    @Test
    void testSetDriverIdToRouteSoCanFindIt(){
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(driverConstraintsRepository.findByDriverIdAndDate(driverId, drivingDate))
                .thenReturn(Optional.of(driverConstraint));
        drivingFascade.setDriverIdToRoute(routeId, driverId);
        drivingFascade.getRoute(drivingDate,routeId);
        when(routeRepository.findRouteByDateAndDriverId(drivingDate, driverId)).thenReturn(Optional.of(route));
        Route result = drivingFascade.getRoute(drivingDate, driverId);
        assertEquals(route.getDate(), result.getDate());
    }
    @Test
    void testAddVisit() {
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(neederTrackingService.getNeedyFromNeederTrackingId(anyLong())).thenReturn(needySimpleDTO);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        drivingFascade.addAddressToRoute(routeId, visit);
        assertEquals(1, route.getVisit().size());
        assertEquals(note, route.getVisit().get(0).getNote());
        verify(routeRepository, times(1)).save(route);
    }
    @Test
    void testRemoveVisit(){
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(routeRepository.save(route)).thenReturn(route);
        route.addVisit(visit);
        drivingFascade.removeAddressFromRoute(routeId, visit);
        assertEquals(0, route.getVisit().size());
        verify(routeRepository, times(1)).save(route);
    }
    @Test
    void testRemoveVisitNotFound(){
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        assertThrows(
                VisitNotExistException.class,
                () -> drivingFascade.removeAddressFromRoute(routeId, visit)
        );
    }
    @Test
void testGetDriverFutureConstraintsHaventConfirmed() {
    List<DriverConstraint> constraints = List.of(driverConstraint);
    when(driverConstraintsRepository.findConstraintsByDriverId(driverId)).thenReturn(constraints);

    List<DriverConstraint> result = drivingFascade.getDriverFutureConstraintsHaventConfirmed(driverId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(driverConstraint, result.get(0));

    verify(driverConstraintsRepository, times(1)).findConstraintsByDriverId(driverId);
}

@Test
void testGetDriverFutureConstraintsHaventConfirmedEmpty() {
    when(driverConstraintsRepository.findConstraintsByDriverId(driverId)).thenReturn(Collections.emptyList());

    List<DriverConstraint> result = drivingFascade.getDriverFutureConstraintsHaventConfirmed(driverId);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(driverConstraintsRepository, times(1)).findConstraintsByDriverId(driverId);
}
@Test
void testGetRoutesByDriverId() {
    List<Route> routes = List.of(route);
    when(routeRepository.findRoutesByDriverId(driverId)).thenReturn(routes);

    List<Route> result = drivingFascade.getRoutesByDriverId(driverId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(route, result.get(0));

    verify(routeRepository, times(1)).findRoutesByDriverId(driverId);
}

@Test
void testGetRoutesByDriverIdEmpty() {
    when(routeRepository.findRoutesByDriverId(driverId)).thenReturn(Collections.emptyList());

    List<Route> result = drivingFascade.getRoutesByDriverId(driverId);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(routeRepository, times(1)).findRoutesByDriverId(driverId);
}
@Test
void testGetRoutes() {
    List<Route> routes = List.of(route);
    when(routeRepository.findRoutesByDate(drivingDate)).thenReturn(routes);

    List<Route> result = drivingFascade.getRoutes(drivingDate);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(route, result.get(0));

    verify(routeRepository, times(1)).findRoutesByDate(drivingDate);
}

@Test
void testGetRoutesEmpty() {
    when(routeRepository.findRoutesByDate(drivingDate)).thenReturn(Collections.emptyList());

    List<Route> result = drivingFascade.getRoutes(drivingDate);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(routeRepository, times(1)).findRoutesByDate(drivingDate);
}


}
