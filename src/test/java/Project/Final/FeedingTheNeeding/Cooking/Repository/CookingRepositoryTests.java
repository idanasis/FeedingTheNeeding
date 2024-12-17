package Project.Final.FeedingTheNeeding.Cooking.Repository;

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
public class CookingRepositoryTests {
    @Autowired
    private DriverConstraintsRepository driverRepository;

    private DriverConstraint validDriverConstraint;
    private DriverConstraint invalidDriverConstraint;

    final long driverId = 1;
    final long invalidDriverId = 999;
    final LocalDate date = LocalDate.now();
    final LocalDate invalidDate = LocalDate.now().plusDays(1);
    final int startHour = 8, endHour = 12;
    final String startLocation = "Rager 5 beer sheva", requests = "Only south country";

    @BeforeEach
    void setUp() {
        validDriverConstraint = new DriverConstraint(driverId, date, startHour, endHour, startLocation, requests);
        invalidDriverConstraint = new DriverConstraint(invalidDriverId, invalidDate, startHour, endHour, startLocation, requests);
    }

    // SUCCESS: Save and find a valid driver constraint
    @Test
    public void testSaveAndFindValidDriverConstraint() {
        driverRepository.save(validDriverConstraint);
        Optional<DriverConstraint> foundConstraint = driverRepository.findByDriverIdAndDate(driverId, date);
        assertTrue(foundConstraint.isPresent());
        assertEquals(foundConstraint.get().getDriverId(), driverId);
    }

    // FAIL: Try to find a constraint that was never saved
    @Test
    public void testFindNonExistentDriverConstraint() {
        Optional<DriverConstraint> foundConstraint = driverRepository.findByDriverIdAndDate(invalidDriverId, invalidDate);
        assertTrue(foundConstraint.isPresent(), "Expected no constraints but found one!");
    }

    // SUCCESS: Delete a saved constraint
    @Test
    public void testDeleteSavedDriverConstraint() {
        driverRepository.save(validDriverConstraint);
        driverRepository.delete(validDriverConstraint);
        Optional<DriverConstraint> foundConstraint = driverRepository.findByDriverIdAndDate(driverId, date);
        assertTrue(foundConstraint.isEmpty());
    }

    // FAIL: Attempt to delete a non-existent constraint
    @Test
    public void testDeleteNonExistentDriverConstraint() {
        driverRepository.delete(invalidDriverConstraint);
        List<DriverConstraint> constraints = driverRepository.findConstraintsByDriverId(invalidDriverId);
        assertEquals(1, constraints.size(), "Expected no constraints, but some were found!");
    }

    // SUCCESS: Find constraints by valid date
    @Test
    public void testFindConstraintsByValidDate() {
        driverRepository.save(validDriverConstraint);
        List<DriverConstraint> constraints = driverRepository.findConstraintsByDate(date);
        assertEquals(1, constraints.size());
        assertEquals(constraints.get(0).getDriverId(), driverId);
    }

    // FAIL: Find constraints by an invalid date
    @Test
    public void testFindConstraintsByInvalidDate() {
        driverRepository.save(validDriverConstraint);
        List<DriverConstraint> constraints = driverRepository.findConstraintsByDate(invalidDate);
        assertEquals(1, constraints.size(), "Expected no constraints for invalid date, but some were found!");
    }

    // SUCCESS: Find constraints by valid driver ID
    @Test
    public void testFindConstraintsByValidDriverId() {
        driverRepository.save(validDriverConstraint);
        List<DriverConstraint> constraints = driverRepository.findConstraintsByDriverId(driverId);
        assertEquals(1, constraints.size());
        assertEquals(constraints.get(0).getDriverId(), driverId);
    }

    // FAIL: Find constraints by invalid driver ID
    @Test
    public void testFindConstraintsByInvalidDriverId() {
        driverRepository.save(validDriverConstraint);
        List<DriverConstraint> constraints = driverRepository.findConstraintsByDriverId(invalidDriverId);
        assertEquals(1, constraints.size(), "Expected no constraints for invalid driver ID, but some were found!");
    }
}
