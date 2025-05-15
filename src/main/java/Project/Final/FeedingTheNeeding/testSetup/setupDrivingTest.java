package Project.Final.FeedingTheNeeding.testSetup;

import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Repository.UserCredentialsRepository;
import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Repository.RouteRepository;
import Project.Final.FeedingTheNeeding.driving.Repository.DriverConstraintsRepository;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.Repository.CookConstraintsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Profile("test")
@RestController
@RequestMapping("/test/setup")
public class setupDrivingTest {

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private NeedyRepository needyRepository;

    @Autowired
    private DriverConstraintsRepository drivingConstraintRepository;

    @Autowired
    private CookConstraintsRepository cookConstraintRepository;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public setupDrivingTest(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public String insertTestData() {

        // 1. Needy
        Needy needy = new Needy();
        needy.setFirstName("TestNeedy");
        needy.setLastName("User");
        needy.setPhoneNumber("123456789");
        needy.setAddress("Needy Address");
        needy.setStreet("ה'");
        needy.setFamilySize(3);
        needy.setConfirmStatus(NeedyStatus.APPROVED);
        needyRepository.save(needy);

        // 2. Donor
        Donor donor = new Donor();
        donor.setFirstName("TestDonor");
        donor.setLastName("User");
        donor.setPhoneNumber("0531223421");
        donor.setEmail("donor@example.com");
        donor.setAddress("Donor Address");
        donor.setStreet("ד'");
        donor.setStatus(RegistrationStatus.AVAILABLE);
        donorRepository.save(donor);

        // 3. Admin with UserCredentials
        Donor admin = new Donor();
        admin.setFirstName("admin");
        admin.setLastName("Admin");
        admin.setPhoneNumber("0531223423");
        admin.setEmail("admin@example.com");
        admin.setAddress("admin Address");
        admin.setStreet("ד'");
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(RegistrationStatus.AVAILABLE);

        // Create UserCredentials for admin
        UserCredentials adminCredentials = new UserCredentials();
        adminCredentials.setPhoneNumber(admin.getPhoneNumber());

        // Encode the password before saving
        String rawPassword = "adminPassword123";  // Example raw password
        String hashedPassword = passwordEncoder.encode(rawPassword);  // Hash the password
        adminCredentials.setPasswordHash(hashedPassword);  // Set the hashed password

        adminCredentials.setDonor(admin);

        userCredentialsRepository.save(adminCredentials);
        donorRepository.save(admin);  // Save donor after setting UserCredentials

        // 4. Driving Constraint
        DriverConstraint constraint = new DriverConstraint();
        constraint.setDriverId(donor.getId());
        constraint.setDate(LocalDate.now().with(java.time.DayOfWeek.FRIDAY));
        constraint.setStartHour("10:00");
        constraint.setEndHour("17:00");
        constraint.setStartLocation("Tel Aviv");
        constraint.setRequests("Available all day");
        drivingConstraintRepository.save(constraint);

        // 5. Cook Constraint
        Map<String, Integer> cookPrefs = new HashMap<>();
        cookPrefs.put("vegetarian", 1);
        cookPrefs.put("noSugar", 1);

        CookConstraints cookConstraint = new CookConstraints();
        cookConstraint.setCookId(donor.getId());
        cookConstraint.setDate(LocalDate.now());
        cookConstraint.setStartTime("08:00");
        cookConstraint.setEndTime("13:00");
        cookConstraint.setConstraints(cookPrefs);
        cookConstraint.setLocation("Jerusalem");
        cookConstraint.setStreet("ג'");
        cookConstraint.setStatus(Status.Pending);
        cookConstraintRepository.save(cookConstraint);

        return "Test data inserted ✅";
    }
}
