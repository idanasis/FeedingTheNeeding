package Project.Final.FeedingTheNeeding.Social.repository;

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

    @Autowired
    private NeederTrackingRepository repository;

    @Test
    public void testSaveAndFindNeeder() {
        // Arrange
//        NeederTracking neederTracking = new NeederTracking();
//        neederTracking.setStatusForWeek("pending");
//        neederTracking.setFamilySize(4);
//        neederTracking.setDietaryPreferences("Vegetarian, No Sugar");
//        neederTracking.setAdditionalNotes("Requires delivery before noon");
//
//        // Act: Save the entity
//        NeederTracking savedNeederTracking = repository.save(neederTracking);
//
//        // Assert: Validate the saved entity
//        assertNotNull(savedNeederTracking.getId());
//        assertEquals("pending", savedNeederTracking.getStatusForWeek());
//        assertEquals(4, savedNeederTracking.getFamilySize());
//        assertEquals("Vegetarian, No Sugar", savedNeederTracking.getDietaryPreferences());
//        assertEquals("Requires delivery before noon", savedNeederTracking.getAdditionalNotes());

//        // Act: Retrieve the entity by ID
//        Optional<NeederTracking> retrievedNeederTracking = repository.findById(savedNeederTracking.getId());
//
//        // Assert: Validate the retrieved entity
//        assertTrue(retrievedNeederTracking.isPresent());
//        NeederTracking foundNeederTracking = retrievedNeederTracking.get();
//        assertEquals(savedNeederTracking.getId(), foundNeederTracking.getId());
//        assertEquals("pending", foundNeederTracking.getStatusForWeek());
//        assertEquals(4, foundNeederTracking.getFamilySize());
//        assertEquals("Vegetarian, No Sugar", foundNeederTracking.getDietaryPreferences());
//        assertEquals("Requires delivery before noon", foundNeederTracking.getAdditionalNotes());
    }
}
