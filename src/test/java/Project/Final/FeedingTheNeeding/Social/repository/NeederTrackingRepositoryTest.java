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

    @Autowired
    private NeederTrackingRepository repository;

    @Autowired
    NeederRepository NeederRepository;

    @Test
    public void testSaveAndFindNeeder() {

        Needy user =new Needy();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Street");
        user.setCity("City");
        user.setFamilySize(4);
        NeederRepository.save(user);

         //Arrange
        NeederTracking neederTracking = new NeederTracking();
        neederTracking.setNeedy(user);
        neederTracking.setDietaryPreferences("Vegetarian, No Sugar");
        neederTracking.setAdditionalNotes("Requires delivery before noon");

        // Act: Save the entity
        NeederTracking savedNeederTracking = repository.save(neederTracking);

        // Assert: Validate the saved entity
        assertNotNull(savedNeederTracking.getId());
        assertEquals("Vegetarian, No Sugar", savedNeederTracking.getDietaryPreferences());
        assertEquals("Requires delivery before noon", savedNeederTracking.getAdditionalNotes());

        // Act: Retrieve the entity by ID
        Optional<NeederTracking> retrievedNeederTracking = repository.findById(savedNeederTracking.getId());

        // Assert: Validate the retrieved entity
        assertTrue(retrievedNeederTracking.isPresent());
        NeederTracking foundNeederTracking = retrievedNeederTracking.get();
        assertEquals(savedNeederTracking.getId(), foundNeederTracking.getId());
        assertEquals(user.getId(), foundNeederTracking.getNeedy().getId());
        assertEquals("John", foundNeederTracking.getNeedy().getFirstName());
        assertEquals("Doe", foundNeederTracking.getNeedy().getLastName());
        assertEquals("123456789", foundNeederTracking.getNeedy().getPhoneNumber());
        assertEquals("123 Street", foundNeederTracking.getNeedy().getAddress());
        assertEquals("City", foundNeederTracking.getNeedy().getCity());
        assertEquals(4, foundNeederTracking.getNeedy().getFamilySize());
        assertEquals("Vegetarian, No Sugar", foundNeederTracking.getDietaryPreferences());
        assertEquals("Requires delivery before noon", foundNeederTracking.getAdditionalNotes());
    }
}
