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

import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.model.WeekStatus;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
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

    @Autowired
    private NeederTrackingRepository neederTrackingRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public setupDrivingTest(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private final LocalDate friday = LocalDate.now().with(java.time.DayOfWeek.FRIDAY);

    @PostMapping
    public String insertTestData() {

        // 1. Needy
        Needy needy = new Needy();
        needy.setFirstName("TestNeedy");
        needy.setLastName("User");
        needy.setPhoneNumber("0531234567");
        needy.setAddress("Needy Address");
        needy.setStreet("ה'");
        needy.setFamilySize(3);
        needy.setRole(UserRole.NEEDY);
        needy.setConfirmStatus(NeedyStatus.APPROVED);
        needyRepository.save(needy);

        Needy pendingNeedy = new Needy();
        pendingNeedy.setFirstName("TestNeedy2");
        pendingNeedy.setLastName("User2");
        pendingNeedy.setPhoneNumber("0531234568");
        pendingNeedy.setAddress("Needy Address2");
        pendingNeedy.setStreet("ד'");
        pendingNeedy.setRole(UserRole.NEEDY);
        pendingNeedy.setFamilySize(4);
        pendingNeedy.setConfirmStatus(NeedyStatus.PENDING);
        needyRepository.save(pendingNeedy);

        // 2. Donor
        Donor driver = new Donor();
        driver.setFirstName("TestDonor");
        driver.setLastName("User");
        driver.setPhoneNumber("0531223421");
        driver.setEmail("donor@example.com");
        driver.setAddress("Donor Address");
        driver.setStreet("ד'");
        driver.setRole(UserRole.DONOR);
        driver.setStatus(RegistrationStatus.AVAILABLE);

        // Create UserCredentials for admin
        UserCredentials DonorCredentials = new UserCredentials();
        DonorCredentials.setPhoneNumber(driver.getPhoneNumber());

        // Encode the password before saving
        String rawPassword = "donorPassword123";  // Example raw password
        String hashedPassword = passwordEncoder.encode(rawPassword);  // Hash the password
        DonorCredentials.setPasswordHash(hashedPassword);  // Set the hashed password

        DonorCredentials.setDonor(driver);

        userCredentialsRepository.save(DonorCredentials);
        donorRepository.save(driver);  // Save donor after setting UserCredentials

        // 2. Donor2
        Donor cheff = new Donor();
        cheff.setFirstName("TestDonor2");
        cheff.setLastName("User2");
        cheff.setPhoneNumber("0531223422");
        cheff.setEmail("donor2@example.com");
        cheff.setAddress("Donor2 Address");
        cheff.setStreet("ד'");
        cheff.setRole(UserRole.DONOR);
        cheff.setStatus(RegistrationStatus.AVAILABLE);
        donorRepository.save(cheff);

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
        String rawPassword2 = "adminPassword123";  // Example raw password
        String hashedPassword2 = passwordEncoder.encode(rawPassword2);  // Hash the password
        adminCredentials.setPasswordHash(hashedPassword2);  // Set the hashed password

        adminCredentials.setDonor(admin);

        userCredentialsRepository.save(adminCredentials);
        donorRepository.save(admin);  // Save donor after setting UserCredentials

        // 4. Driving Constraint
        DriverConstraint constraint = new DriverConstraint();
        constraint.setDriverId(driver.getId());
        constraint.setDate(friday);
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
        cookConstraint.setCookId(cheff.getId());
        cookConstraint.setDate(friday);
        cookConstraint.setStartTime("08:00");
        cookConstraint.setEndTime("13:00");
        cookConstraint.setConstraints(cookPrefs);
        cookConstraint.setLocation("Jerusalem");
        cookConstraint.setStreet("ג'");
        cookConstraint.setStatus(Status.Accepted);
        cookConstraintRepository.save(cookConstraint);


        NeederTracking neederTracking = new NeederTracking();
        neederTracking.setNeedy(needy);
        neederTracking.setWeekStatus(WeekStatus.Here);
        neederTracking.setDietaryPreferences("Vegan");
        neederTracking.setAdditionalNotes("No nuts");
        neederTracking.setDate(friday);
        neederTrackingRepository.save(neederTracking);



        return "Test data inserted ✅";
    }
}
