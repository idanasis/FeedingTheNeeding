package Project.Final.FeedingTheNeeding.Social.repository;

import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

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
        NeederTracking needer = new NeederTracking();
        needer.setName("John Doe");
        needer.setPhone("123456789");

        NeederTracking savedNeeder = repository.save(needer);
        assertNotNull(savedNeeder.getId());

        NeederTracking foundNeeder = repository.findById(savedNeeder.getId()).orElse(null);
        assertNotNull(foundNeeder);
        assertEquals("John Doe", foundNeeder.getName());
    }
}