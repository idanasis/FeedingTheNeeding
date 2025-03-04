package Project.Final.FeedingTheNeeding.Driving.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Repository.DriverConstraintsRepository;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DriverConstraintTest {
    
    @Autowired
    private DriverConstraintsRepository driverRepository;
    private DriverConstraint driverConstraint;
    final long driverId = 1;
    final LocalDate date = LocalDate.now();
    final String startHour = "8:00",endHour = "12:00";
    final String startLocation = "Rager 5 beer sheva", requests = "Only south country";
    
    @BeforeEach
    void setUp() {
        driverRepository.deleteAll(); // Clear all driver constraints before each test
        driverConstraint = new DriverConstraint(driverId, date, startHour, endHour, startLocation, requests);
    }

    @Test
    public void testSaveAndFindDriverConstraint() {
        driverRepository.save(driverConstraint);
        Optional<DriverConstraint> newDriverConstraint=driverRepository.findByDriverIdAndDate(driverId, date);
        assertTrue(newDriverConstraint.get() instanceof DriverConstraint);
        assertEquals(newDriverConstraint.get().getDriverId(),driverId);
        assertEquals(newDriverConstraint.get().getDate(),date);
        assertEquals(newDriverConstraint.get().getStartHour(),startHour);
        assertEquals(newDriverConstraint.get().getEndHour(),endHour);
        assertEquals(newDriverConstraint.get().getStartLocation(),startLocation);
        assertEquals(newDriverConstraint.get().getRequests(),requests);
    }
    @Test
    public void testDriverConstraintNotFound() {
        Optional<DriverConstraint> newDriverConstraint=driverRepository.findByDriverIdAndDate(driverId, date);
        assertTrue(newDriverConstraint.isEmpty());
    }
    @Test
    public void testDeleteDriverConstraint() {
        driverRepository.save(driverConstraint);
        driverRepository.delete(driverConstraint);
        Optional<DriverConstraint> newDriverConstraint=driverRepository.findByDriverIdAndDate(driverId, date);
        assertTrue(newDriverConstraint.isEmpty());
    }

    @Test
    public void testFindConstraintsByDate() {
        driverRepository.save(driverConstraint);
        List<DriverConstraint> driverConstraintsForDate = driverRepository.findConstraintsByDate(date);
        DriverConstraint driverConstraint = driverConstraintsForDate.get(0);
        assertEquals(driverConstraint.getDate(),date);
        assertEquals(driverConstraint.getDriverId(), driverId);
        assertEquals(driverConstraint.getStartHour(), startHour);
        assertEquals(driverConstraint.getEndHour(), endHour);
        assertEquals(driverConstraint.getStartLocation(), startLocation);
    }
    @Test
    public void testFindConstraintsByDriverId() {
        driverRepository.save(driverConstraint);
        List<DriverConstraint> driverConstraintsForDriver = driverRepository.findConstraintsByDriverId(driverId);
        DriverConstraint driverConstraint = driverConstraintsForDriver.get(0);
        assertEquals(driverConstraint.getDate(),date);
        assertEquals(driverConstraint.getDriverId(), driverId);
        assertEquals(driverConstraint.getStartHour(), startHour);
        assertEquals(driverConstraint.getEndHour(), endHour);
        assertEquals(driverConstraint.getStartLocation(), startLocation);
    }
    @Test
    public void testFindConstraintsByDriverIdNotFound() {
        List<DriverConstraint> driverConstraintsForDriver = driverRepository.findConstraintsByDriverId(driverId);
        assertTrue(driverConstraintsForDriver.isEmpty());
    }
    @Test
    public void testFindConstraintsByDateNotFound() {
        List<DriverConstraint> driverConstraintsForDate = driverRepository.findConstraintsByDate(date);
        System.out.println(driverConstraintsForDate);
        assertTrue(driverConstraintsForDate.isEmpty());
    }

}
