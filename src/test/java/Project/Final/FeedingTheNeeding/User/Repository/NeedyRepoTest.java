package Project.Final.FeedingTheNeeding.User.Repository;


import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NeedyRepoTest {

    @Autowired
    private NeedyRepository needyRepository;

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PHONE_NUMBER1 = "0510000000";
    private static final String PHONE_NUMBER2 = "0520000000";

    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final int FAMILY_SIZE = 4;
    private static final NeedyStatus NEEDY_STATUS = NeedyStatus.COMPLETED;

    @Test
    public void testFindByPhoneNumber() {

        Needy needy = new Needy();
        needy.setFirstName(FIRST_NAME);
        needy.setLastName(LAST_NAME);
        needy.setPhoneNumber(PHONE_NUMBER1);
        needy.setAddress(ADDRESS);
        needy.setCity(CITY);
        needy.setConfirmStatus(NEEDY_STATUS);
        needy.setFamilySize(FAMILY_SIZE);
        needyRepository.save(needy);

        Optional<Needy> foundNeedy = needyRepository.findByPhoneNumber(PHONE_NUMBER1);

        assertTrue(foundNeedy.isPresent());
        assertEquals(FIRST_NAME, foundNeedy.get().getFirstName());
        assertEquals(LAST_NAME, foundNeedy.get().getLastName());
        assertEquals(PHONE_NUMBER1, foundNeedy.get().getPhoneNumber());
        assertEquals(ADDRESS, foundNeedy.get().getAddress());
        assertEquals(CITY, foundNeedy.get().getCity());
        assertEquals(FAMILY_SIZE, foundNeedy.get().getFamilySize());
        assertEquals(NEEDY_STATUS, foundNeedy.get().getConfirmStatus());
    }

    @Test
    public void testFindByConfirmStatus() {
        Needy needy1 = new Needy();
        needy1.setFirstName(FIRST_NAME);
        needy1.setLastName(LAST_NAME);
        needy1.setPhoneNumber(PHONE_NUMBER1);
        needy1.setAddress(ADDRESS);
        needy1.setCity(CITY);
        needy1.setConfirmStatus(NEEDY_STATUS);
        needy1.setFamilySize(FAMILY_SIZE);

        Needy needy2 = new Needy();
        needy2.setFirstName(FIRST_NAME);
        needy2.setLastName(LAST_NAME);
        needy2.setPhoneNumber(PHONE_NUMBER2);
        needy2.setAddress(ADDRESS);
        needy2.setCity(CITY);
        needy2.setConfirmStatus(NeedyStatus.PENDING);
        needy2.setFamilySize(FAMILY_SIZE);

        needyRepository.save(needy1);
        needyRepository.save(needy2);

        List<Needy> approvedNeedy = needyRepository.findByConfirmStatus(NeedyStatus.PENDING);

        assertNotNull(approvedNeedy);
        assertEquals(1, approvedNeedy.size());
        assertEquals(FIRST_NAME, approvedNeedy.get(0).getFirstName());
        assertEquals(LAST_NAME, approvedNeedy.get(0).getLastName());
        assertEquals(PHONE_NUMBER2, approvedNeedy.get(0).getPhoneNumber());
        assertEquals(ADDRESS, approvedNeedy.get(0).getAddress());
        assertEquals(CITY, approvedNeedy.get(0).getCity());
        assertEquals(FAMILY_SIZE, approvedNeedy.get(0).getFamilySize());
        assertEquals(NeedyStatus.PENDING, approvedNeedy.get(0).getConfirmStatus());
    }
}
