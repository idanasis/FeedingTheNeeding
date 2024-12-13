package Project.Final.FeedingTheNeeding.Authentication.Repository;


import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserCredentialsRepoTest {

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    private UserCredentials user;

    final String EMAIL = "email@gmail.com", PASSWORD = "password", EMAIL2 = "email2@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PHONE_NUMBER = "0500000000";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final RegistrationStatus REGISTRATION_STATUS = RegistrationStatus.AVAILABLE;
    private static final String VERIFICATION_CODE = "123456";
    @Autowired
    private DonorRepository donorRepository;

    @BeforeEach
    void setUp() {
        Donor donor = new Donor();
        donor.setEmail(EMAIL);
        donor.setFirstName(FIRST_NAME);
        donor.setLastName(LAST_NAME);
        donor.setPhoneNumber(PHONE_NUMBER);
        donor.setAddress(ADDRESS);
        donor.setCity(CITY);
        donor.setStatus(REGISTRATION_STATUS);
        donor.setVerificationCode(VERIFICATION_CODE);
        donorRepository.save(donor);

        user = new UserCredentials();
        user.setEmail(EMAIL);
        user.setPasswordHash(PASSWORD);
        user.setLastPasswordChangeAt(LocalDateTime.now());
        user.setDonor(donor);

        userCredentialsRepository.save(user);
    }

    @Test
    void shouldFindByEmail() {
        Optional<UserCredentials> retrievedUser = userCredentialsRepository.findByEmail(EMAIL);

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void shouldReturnCredentialsByEmail() {
        UserCredentials retrievedUser = userCredentialsRepository.findCredentialsByEmail(EMAIL);

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void shouldCheckIfEmailExists() {
        boolean exists = userCredentialsRepository.existsByEmail(EMAIL);

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<UserCredentials> retrievedUser = userCredentialsRepository.findByEmail(EMAIL2);

        assertThat(retrievedUser).isNotPresent();
    }

    @Test
    void shouldReturnFalseIfEmailDoesNotExist() {
        boolean exists = userCredentialsRepository.existsByEmail(EMAIL2);

        assertThat(exists).isFalse();
    }
}
