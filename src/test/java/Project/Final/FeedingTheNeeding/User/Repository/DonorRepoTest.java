package Project.Final.FeedingTheNeeding.User.Repository;


import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DonorRepoTest {

    @Autowired
    private DonorRepository donorRepository;

    private static final String EMAIL1 = "email1@gmail.com", EMAIL2 = "email2@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PHONE_NUMBER = "0500000000";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final RegistrationStatus REGISTRATION_STATUS = RegistrationStatus.AVAILABLE;
    private static final String VERIFICATION_CODE = "123456";

    @Test
    public void testFindByEmail() {

        Donor donor = new Donor();
        donor.setEmail(EMAIL1);
        donor.setFirstName(FIRST_NAME);
        donor.setLastName(LAST_NAME);
        donor.setPhoneNumber(PHONE_NUMBER);
        donor.setAddress(ADDRESS);
        donor.setCity(CITY);
        donor.setStatus(REGISTRATION_STATUS);
        donorRepository.save(donor);

        Optional<Donor> foundDonor = donorRepository.findByEmail(EMAIL1);


        assertTrue(foundDonor.isPresent());
        assertEquals(EMAIL1, foundDonor.get().getEmail());
        assertEquals(FIRST_NAME, foundDonor.get().getFirstName());
        assertEquals(LAST_NAME, foundDonor.get().getLastName());
        assertEquals(PHONE_NUMBER, foundDonor.get().getPhoneNumber());
        assertEquals(ADDRESS, foundDonor.get().getAddress());
        assertEquals(CITY, foundDonor.get().getCity());
    }

    @Test
    public void testFindByVerificationCode() {
        // Arrange
        Donor donor = new Donor();
        donor.setEmail(EMAIL2);
        donor.setFirstName(FIRST_NAME);
        donor.setLastName(LAST_NAME);
        donor.setPhoneNumber(PHONE_NUMBER);
        donor.setAddress(ADDRESS);
        donor.setCity(CITY);
        donor.setStatus(REGISTRATION_STATUS);
        donor.setVerificationCode(VERIFICATION_CODE);
        donorRepository.save(donor);

        Optional<Donor> foundDonor = donorRepository.findByVerificationCode(VERIFICATION_CODE);

        assertTrue(foundDonor.isPresent());
        assertEquals(EMAIL2, foundDonor.get().getEmail());
        assertEquals(VERIFICATION_CODE, foundDonor.get().getVerificationCode());
        assertEquals(FIRST_NAME, foundDonor.get().getFirstName());
        assertEquals(LAST_NAME, foundDonor.get().getLastName());
        assertEquals(PHONE_NUMBER, foundDonor.get().getPhoneNumber());
        assertEquals(ADDRESS, foundDonor.get().getAddress());
        assertEquals(CITY, foundDonor.get().getCity());
    }

}
