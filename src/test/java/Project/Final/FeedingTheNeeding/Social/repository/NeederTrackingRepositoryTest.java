package Project.Final.FeedingTheNeeding.Social.repository;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.model.WeekStatus;
import Project.Final.FeedingTheNeeding.social.projection.NeederTrackingProjection;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NeederTrackingRepositoryTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String PHONE_NUMBER = "123456789";
    private static final String ADDRESS = "123 Street";
    private static final int FAMILY_SIZE = 4;
    private static final String DIETARY_PREFERENCES = "Vegetarian, No Sugar";
    private static final String ADDITIONAL_NOTES = "Requires delivery before noon";
    private static final LocalDate DATE = LocalDate.of(2021, 10, 10);

    @Autowired
    private NeederTrackingRepository repository;

    @Autowired
    private NeedyRepository needyRepository;

    @Test
    public void testSaveAndFindNeeder() {
        // Arrange: Create and save a Needy entity
        Needy user = new Needy();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setPhoneNumber(PHONE_NUMBER);
        user.setAddress(ADDRESS);
        user.setFamilySize(FAMILY_SIZE);
        needyRepository.save(user);

        // Arrange: Create a NeederTracking entity
        NeederTracking neederTracking = new NeederTracking();
        neederTracking.setNeedy(user);
        neederTracking.setDietaryPreferences(DIETARY_PREFERENCES);
        neederTracking.setAdditionalNotes(ADDITIONAL_NOTES);

        // Act: Save the entity
        NeederTracking savedNeederTracking = repository.save(neederTracking);

        // Assert: Validate the saved entity
        assertNotNull(savedNeederTracking.getId());
        assertEquals(DIETARY_PREFERENCES, savedNeederTracking.getDietaryPreferences());
        assertEquals(ADDITIONAL_NOTES, savedNeederTracking.getAdditionalNotes());

        // Act: Retrieve the entity by ID
        Optional<NeederTracking> retrievedNeederTracking = repository.findById(savedNeederTracking.getId());

        // Assert: Validate the retrieved entity
        assertTrue(retrievedNeederTracking.isPresent());
        NeederTracking foundNeederTracking = retrievedNeederTracking.get();
        assertEquals(savedNeederTracking.getId(), foundNeederTracking.getId());
        assertEquals(user.getId(), foundNeederTracking.getNeedy().getId());
        assertEquals(FIRST_NAME, foundNeederTracking.getNeedy().getFirstName());
        assertEquals(LAST_NAME, foundNeederTracking.getNeedy().getLastName());
        assertEquals(PHONE_NUMBER, foundNeederTracking.getNeedy().getPhoneNumber());
        assertEquals(ADDRESS, foundNeederTracking.getNeedy().getAddress());
        assertEquals(FAMILY_SIZE, foundNeederTracking.getNeedy().getFamilySize());
        assertEquals(DIETARY_PREFERENCES, foundNeederTracking.getDietaryPreferences());
        assertEquals(ADDITIONAL_NOTES, foundNeederTracking.getAdditionalNotes());
    }

        @Test
        public void testFindByWeekStatus() {
            // Arrange: Create and save a Needy entity
            Needy user = createSampleNeedy();
            needyRepository.save(user);

            // Create multiple NeederTracking entities with different week statuses
            NeederTracking tracking1 = createNeederTracking(user, WeekStatus.Here);
            NeederTracking tracking2 = createNeederTracking(user, WeekStatus.Here);
            NeederTracking tracking3 = createNeederTracking(user, WeekStatus.NotHere);

            repository.save(tracking1);
            repository.save(tracking2);
            repository.save(tracking3);

            // Act: Find all NeederTracking with WeekStatus.Here
            List<NeederTrackingProjection> hereTrackings = repository.findByWeekStatusAndDate(WeekStatus.Here,DATE);

            // Assert
            assertNotNull(hereTrackings);
            assertEquals(2, hereTrackings.size());
        }

        @Test
        public void testDeleteNeederTracking() {
            // Arrange: Create and save a Needy entity
            Needy user = createSampleNeedy();
            needyRepository.save(user);

            // Create a NeederTracking entity
            NeederTracking neederTracking = createNeederTracking(user, WeekStatus.Here);
            NeederTracking savedTracking = repository.save(neederTracking);

            // Act: Delete the NeederTracking
            repository.deleteById(savedTracking.getId());

            // Assert: Verify deletion
            Optional<NeederTracking> deletedTracking = repository.findById(savedTracking.getId());
            assertFalse(deletedTracking.isPresent());
        }

        @Test
        public void testUpdateNeederTracking() {
            // Arrange: Create and save a Needy entity
            Needy user = createSampleNeedy();
            needyRepository.save(user);

            // Create a NeederTracking entity
            NeederTracking neederTracking = createNeederTracking(user, WeekStatus.Here);
            NeederTracking savedTracking = repository.save(neederTracking);

            // Act: Update the NeederTracking
            savedTracking.setDietaryPreferences("Updated Preferences");
            savedTracking.setAdditionalNotes("Updated Notes");
            NeederTracking updatedTracking = repository.save(savedTracking);

            // Assert
            assertEquals("Updated Preferences", updatedTracking.getDietaryPreferences());
            assertEquals("Updated Notes", updatedTracking.getAdditionalNotes());
        }

        @Test
        public void testEntityGraphFindById() {
            // Arrange: Create and save a Needy entity
            Needy user = createSampleNeedy();
            needyRepository.save(user);

            // Create a NeederTracking entity
            NeederTracking neederTracking = createNeederTracking(user, WeekStatus.Here);
            NeederTracking savedTracking = repository.save(neederTracking);

            // Act: Find by ID
            Optional<NeederTracking> foundTracking = repository.findById(savedTracking.getId());

            // Assert
            assertTrue(foundTracking.isPresent());
            assertNotNull(foundTracking.get().getNeedy());
            assertEquals(user.getId(), foundTracking.get().getNeedy().getId());
        }

        // Helper methods to reduce code duplication
        private Needy createSampleNeedy() {
            Needy user = new Needy();
            user.setFirstName(FIRST_NAME);
            user.setLastName(LAST_NAME);
            user.setPhoneNumber(PHONE_NUMBER);
            user.setAddress(ADDRESS);
            user.setFamilySize(FAMILY_SIZE);
            return user;
        }

        private NeederTracking createNeederTracking(Needy user, WeekStatus weekStatus) {
            NeederTracking neederTracking = new NeederTracking();
            neederTracking.setNeedy(user);
            neederTracking.setDietaryPreferences(DIETARY_PREFERENCES);
            neederTracking.setAdditionalNotes(ADDITIONAL_NOTES);
            neederTracking.setWeekStatus(weekStatus);
            neederTracking.setDate(DATE);
            return neederTracking;
        }

    @Test
    public void testFindByDate() {
        // Arrange
        Needy user = createSampleNeedy();
        needyRepository.save(user);

        NeederTracking neederTracking = createNeederTracking(user, WeekStatus.Here);
        neederTracking.setDate(DATE);
        NeederTracking savedTracking = repository.save(neederTracking);

        // Save to repository
        repository.save(savedTracking);

        // Act
        List<NeederTracking> result = repository.findByDate(DATE);

        // Assert
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(DATE, result.get(0).getDate());
    }
}
