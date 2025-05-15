package Project.Final.FeedingTheNeeding.Parallelism;

import Project.Final.FeedingTheNeeding.Authentication.Controller.AuthController;
import Project.Final.FeedingTheNeeding.Authentication.DTO.*;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.Authentication.Service.JwtTokenService;
import Project.Final.FeedingTheNeeding.cook.Controller.CookController;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class ParallelismTests {

    private static final int NUMBER_OF_DONORS = 50;
    private static final int THREAD_POOL_SIZE = 50;
    private static final int TIMEOUT_SECONDS = 30;
    private static final String PASSWORD = "password123";
    private static final String EMAIL_PATTERN = "donor%d@test.com";
    private static final String PHONE_PATTERN = "05%08d";
    private static final String NAME_PATTERN = "Donor %d";
    private static final String FIRST_NAME = "First";
    private static final String LAST_NAME = "Second";
    private static final String TOKEN_PREFIX = "token_";
    private static final long TOKEN_EXPIRATION = 3600L;
    private static final String ADDRESS = "test address";
    private static final String STREET = "test street";

    private static final String START_TIME = "08:00";
    private static final String END_TIME = "10:00";
    private static final String LOCATION_PATTERN = "Location %d";
    private static final String MEALS_PATTERN = "Meals%d";
    private static final String TOKEN_PATTERN = "Bearer token_%d";
    private static final int BASE_MEALS = 10;

    @Mock
    private CookingService cookingService;

    private CookController cookController;

    @Mock
    private AuthService authService;

    @Mock
    private JwtTokenService jwtTokenService;

    private AuthController authController;


    private final Map<String, UserCredentials> fakeUserStore = new ConcurrentHashMap<>();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authService, jwtTokenService);
        cookController = new CookController(cookingService);

        when(authService.authenticate(any())).thenAnswer(invocation -> {
            AuthenticationRequest request = invocation.getArgument(0);
            UserCredentials user = fakeUserStore.get(request.getPhoneNumber());
            return user;
        });

        doAnswer(invocation -> {
            RegistrationRequest req = invocation.getArgument(0);
            UserCredentials user = new UserCredentials();
            user.setPhoneNumber(req.getPhoneNumber());
            user.setPasswordHash(req.getPassword()); // Normally you'd hash this
            fakeUserStore.put(req.getPhoneNumber(), user);
            return new ResponseEntity<>(HttpStatus.OK);
        }).when(authService).registerDonor(any());

        when(jwtTokenService.generateToken(any())).thenAnswer(invocation -> {
            UserCredentials user = invocation.getArgument(0);
            return TOKEN_PREFIX + user.getPhoneNumber();
        });

        when(jwtTokenService.getExpirationTime()).thenReturn(TOKEN_EXPIRATION);
    }

    @Test
    public void testConcurrentDonorRegistrationsAndLogins() throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(NUMBER_OF_DONORS);
        Set<String> tokens = Collections.synchronizedSet(new HashSet<>());
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (int i = 51; i < 51+NUMBER_OF_DONORS; i++) {
            final int donorIndex = i;
            executor.submit(() -> {
                try {
                    startSignal.await(); // Wait for all threads to be ready

                    String uniquePhone = String.format(PHONE_PATTERN, donorIndex);
                    String uniqueUsername = String.format(EMAIL_PATTERN,donorIndex);
                    RegistrationRequest request = new RegistrationRequest(
                            uniqueUsername,
                            PASSWORD,
                            PASSWORD,
                            FIRST_NAME,
                            LAST_NAME,
                            uniquePhone,
                            STREET,
                            ADDRESS
                    );

                    ResponseEntity<?> response = authController.registerDonor(request);
                    assertEquals(HttpStatus.OK, response.getStatusCode());

                    AuthenticationRequest authRequest = new AuthenticationRequest();
                    authRequest.setPhoneNumber(uniquePhone);
                    authRequest.setPassword(PASSWORD);

                    ResponseEntity<?> loginResponse = authController.login(authRequest);
                    assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

                    AuthenticationResponse authResponse = (AuthenticationResponse) loginResponse.getBody();
                    assertNotNull(authResponse);
                    tokens.add(authResponse.getToken());

                } catch (Exception e) {
                    fail("Exception occurred: " + e.getMessage());
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown(); // Let all threads proceed
        boolean completed = doneSignal.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "Timed out waiting for all registrations");
        assertEquals(NUMBER_OF_DONORS, tokens.size(), "Each donor should receive a unique token");

        Set<String> uniqueTokens = new HashSet<>(tokens);
        assertEquals(NUMBER_OF_DONORS, uniqueTokens.size(), "All tokens should be unique");
    }

    @Test
    public void testConcurrentConstraintSubmissions() throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(NUMBER_OF_DONORS);
        Set<Long> constraintIds = Collections.synchronizedSet(new HashSet<>());
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        when(cookingService.submitConstraints(any(), any())).thenAnswer(invocation -> {
            CookConstraints constraint = invocation.getArgument(0);
            constraint.setConstraintId(constraintIds.size() + 1L);
            constraintIds.add(constraint.getConstraintId());
            return constraint;
        });

        for (int i = 0; i < NUMBER_OF_DONORS; i++) {
            final int donorIndex = i;
            executor.submit(() -> {
                try {
                    startSignal.await();

                    Map<String, Integer> constraints = new HashMap<>();
                    constraints.put(String.format(MEALS_PATTERN, donorIndex), donorIndex + BASE_MEALS);

                    CookConstraints constraint = new CookConstraints();
                    constraint.setCookId(donorIndex);
                    constraint.setStartTime(START_TIME);
                    constraint.setEndTime(END_TIME);
                    constraint.setConstraints(constraints);
                    constraint.setLocation(String.format(LOCATION_PATTERN, donorIndex));
                    constraint.setDate(LocalDate.now());

                    String authHeader = String.format(TOKEN_PATTERN, donorIndex);
                    ResponseEntity<?> response = cookController.submitConstraints(constraint, authHeader);

                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    CookConstraints submitted = (CookConstraints) response.getBody();
                    assertNotNull(submitted);
                    assertTrue(submitted.getConstraintId() > 0);

                } catch (Exception e) {
                    fail("Exception occurred: " + e.getMessage());
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown();
        boolean completed = doneSignal.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "Timed out waiting for all submissions");
        assertEquals(NUMBER_OF_DONORS, constraintIds.size(), "Should have unique constraint IDs");
    }

}