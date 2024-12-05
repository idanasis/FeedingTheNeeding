package Project.Final.FeedingTheNeeding.Driving.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import Project.Final.FeedingTheNeeding.driving.Model.Route;
import Project.Final.FeedingTheNeeding.driving.Model.Visit;
import Project.Final.FeedingTheNeeding.driving.Model.VisitStatus;
import Project.Final.FeedingTheNeeding.driving.Repository.RouteRepository;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RouteTest {

    @Autowired
    private RouteRepository routeRepository;
    private Route route;
    private Visit visit;
    final String address = "Ringelbloom 24 Beer Sheva";
    final String firstName = "John";
    final String lastName = "Doe";
    final String phoneNumber = "0541234567";
    final int maxHour = 14;
    final String note = "some Note";
    final long driverId = 1;
    final LocalDate date = LocalDate.now();
    @BeforeEach
    void setUp() {
        route = new Route(driverId, date);
        visit = new Visit(address, firstName, lastName, phoneNumber, maxHour, VisitStatus.Deliver, note,route);
    }
    @Test
    void testCreateFindRoute() {
        routeRepository.save(route);
        Route route1 = routeRepository.findById(route.getRouteId()).get();
        assertEquals(driverId, route1.getDriverId());
        assertEquals(date, route1.getDate());
    }
    @Test
    void testDeleteRoute() {
        routeRepository.save(route);
        routeRepository.delete(route);
        assertEquals(routeRepository.findById(route.getRouteId()).isEmpty(), true);
    }
    @Test
    void testUpdateRoute() {
        routeRepository.save(route);
        route.setDriverId(driverId);
        routeRepository.save(route);
        Route route1 = routeRepository.findById(route.getRouteId()).get();
        assertEquals(driverId,route1.getDriverId());
    }
    @Test
    void testFindAllRoute() {
        routeRepository.save(route);
        assertEquals(routeRepository.findAll().size(), 1);
    }
    @Test
    void testFindByDate() {
        routeRepository.save(route);
        List<Route> routes = routeRepository.findRoutesByDate(date);
        assertEquals(routes.size(), 1);
        Route route1 = routes.get(0);
        assertEquals(driverId,route1.getDriverId());
        assertEquals(route1.getDate(), date);
        assertEquals(false,route1.isSubmitted());
    }
    @Test
    void testSubmitRoute() {
        routeRepository.save(route);
        Route route1 = routeRepository.findById(route.getRouteId()).get();
        route1.setSubmitted(true);
        routeRepository.save(route1);
        Route route2 = routeRepository.findById(route.getRouteId()).get();
        assertEquals( true,route2.isSubmitted());
    }

    @Test
    void testAddVisit() {
        routeRepository.save(route);
        Route route1 = routeRepository.findById(route.getRouteId()).get();
        route1.addVisit(visit);
        routeRepository.save(route1);
        Route route2 = routeRepository.findById(route.getRouteId()).get();
        assertEquals(1,route2.getVisit().size());
        assertEquals(address,route2.getVisit().get(0).getAddress());
        assertEquals(firstName,route2.getVisit().get(0).getFirstName());
        assertEquals(lastName,route2.getVisit().get(0).getLastName());
        assertEquals(phoneNumber,route2.getVisit().get(0).getPhoneNumber());
        assertEquals(maxHour,route2.getVisit().get(0).getMaxHour());
        assertEquals(VisitStatus.Deliver,route2.getVisit().get(0).getStatus());
        assertEquals(note,route2.getVisit().get(0).getNote());
    }
    @Test
    public void testRemoveVisit() {
        routeRepository.save(route);
        Route route1 = routeRepository.findById(route.getRouteId()).get();
        route1.addVisit(visit);
        routeRepository.save(route1);
        Route route2 = routeRepository.findById(route.getRouteId()).get();
        Visit managedVisit = route2.getVisit().stream()
        .filter(v -> v.getAddress().equals(visit.getAddress()))
        .findFirst().get();
        route2.removeVisit(managedVisit);
        routeRepository.save(route2);
        Route route3 = routeRepository.findById(route.getRouteId()).get();
        assertEquals(0,route3.getVisit().size());
    }
    @Test
    void testSetVisit() {
        routeRepository.save(route);
        Route route1 = routeRepository.findById(route.getRouteId()).get();
        route1.addVisit(visit);
        routeRepository.save(route1);
        Route route2 = routeRepository.findById(route.getRouteId()).get();
        List<Visit> visits = new ArrayList<>();
        route2.setVisit(visits);
        routeRepository.save(route2);
        Route route3 = routeRepository.findById(route.getRouteId()).get();
        assertEquals(0,route3.getVisit().size());
    }

}
