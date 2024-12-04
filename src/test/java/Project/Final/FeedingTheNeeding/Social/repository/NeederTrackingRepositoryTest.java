package Project.Final.FeedingTheNeeding.Social.repository;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Repository.NeederRepository;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

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
    private static final String CITY = "City";
    private static final int FAMILY_SIZE = 4;
    private static final String DIETARY_PREFERENCES = "Vegetarian, No Sugar";
    private static final String ADDITIONAL_NOTES = "Requires delivery before noon";

    @Autowired
    private NeederTrackingRepository repository;

    @Autowired
    private NeederRepository neederRepository;

    @Test
    public void testSaveAndFindNeeder() {
        // Arrange: Create and save a Needy entity
        Needy user = new Needy();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setPhoneNumber(PHONE_NUMBER);
        user.setAddress(ADDRESS);
        user.setCity(CITY);
        user.setFamilySize(FAMILY_SIZE);
        neederRepository.save(user);

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
        assertEquals(CITY, foundNeederTracking.getNeedy().getCity());
        assertEquals(FAMILY_SIZE, foundNeederTracking.getNeedy().getFamilySize());
        assertEquals(DIETARY_PREFERENCES, foundNeederTracking.getDietaryPreferences());
        assertEquals(ADDITIONAL_NOTES, foundNeederTracking.getAdditionalNotes());
    }
}
