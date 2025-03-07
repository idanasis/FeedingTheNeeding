package Project.Final.FeedingTheNeeding.Authentication.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.*;
import Project.Final.FeedingTheNeeding.Authentication.Exception.AccountNotVerifiedException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserAlreadyExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Repository.UserCredentialsRepository;
import Project.Final.FeedingTheNeeding.Authentication.Exception.InvalidCredentialException;
import Project.Final.FeedingTheNeeding.User.Model.*;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private final DonorRepository donorRepository;
    private final NeedyRepository needyRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtTokenService jwtTokenService;

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    public AuthService(DonorRepository donorRepository, NeedyRepository needyRepository, UserCredentialsRepository userCredentialsRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, JwtTokenService jwtTokenService) {
        this.donorRepository = donorRepository;
        this.needyRepository = needyRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.jwtTokenService = jwtTokenService;
    }

    public UserCredentials authenticate(AuthenticationRequest authenticationRequest) {
        logger.info("Start authentication for phone number: {}", authenticationRequest.getPhoneNumber());

        // Retrieve user credentials from the database
        UserCredentials user = userCredentialsRepository.findCredentialsByPhoneNumber(authenticationRequest.getPhoneNumber());
        if (user == null) {
            logger.error("User not found for phone number: {}", authenticationRequest.getPhoneNumber());
            throw new InvalidCredentialException("Invalid credentials");
        }

        if(user.getDonor().getStatus() == RegistrationStatus.PENDING) {
            logger.warn("Donor not verified for phone number: {}", authenticationRequest.getPhoneNumber());
            throw new AccountNotVerifiedException("ACCOUNT_NOT_VERIFIED");
        }

        try {
            // Verify password using PasswordEncoder
            if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPasswordHash())) {
                logger.error("Invalid password for phone number: {}", authenticationRequest.getPhoneNumber());
                throw new InvalidCredentialException("Invalid password");
            }

            // Authenticate using the AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getPhoneNumber(),
                            authenticationRequest.getPassword()
                    )
            );

            logger.info("Authentication successful for phone number: {}", authenticationRequest.getPhoneNumber());
            return user;

        } catch (Exception e) {
            logger.error("Authentication failed for phone number: {}", authenticationRequest.getPhoneNumber(), e);
            throw new InvalidCredentialException("Invalid credentials");
        }
    }

    public void logout(String token) {
        logger.info("start-logout, token: {}", token);
        // TODO: implement a real blacklist or stored invalidation.
        // just logout now, nothing really happen
        logger.info("end-logout, token invalidated.");
    }

    @Transactional
    public void registerDonor(RegistrationRequest registrationRequest) {
        logger.info("start-register donor,  phone number: {}", registrationRequest.getPhoneNumber());
        if(donorRepository.findByPhoneNumber(registrationRequest.getPhoneNumber()).isPresent())
            throw new UserAlreadyExistsException("Donor already exists");

        Donor donor = new Donor();
        donor.setEmail(registrationRequest.getEmail());
        donor.setFirstName(registrationRequest.getFirstName());
        donor.setLastName(registrationRequest.getLastName());
        donor.setPhoneNumber(registrationRequest.getPhoneNumber());
        donor.setAddress(registrationRequest.getAddress());
        donor.setRole(UserRole.DONOR);
        donor.setStatus(RegistrationStatus.PENDING);
        donor.setTimeOfDonation(0);
        donor.setStreet(registrationRequest.getStreet());
        System.out.println(Street.fromHebrewName(registrationRequest.getStreet()));

        Donor savedDonor = donorRepository.save(donor);

        UserCredentials credentials = new UserCredentials();
        credentials.setPhoneNumber(registrationRequest.getPhoneNumber());
        credentials.setPasswordHash(passwordEncoder.encode(registrationRequest.getPassword()));
        credentials.setLastPasswordChangeAt(LocalDateTime.now());
        credentials.setDonor(savedDonor);
        userCredentialsRepository.save(credentials);
        donor.setUserCredentials(credentials);
        donorRepository.save(donor);
        logger.info("end-register donor, phone number: {}", registrationRequest.getPhoneNumber());
    }

    public void registerNeedy(NeedyRegistrationRequest needyRegistrationRequest) {
        logger.info("start-register needy,  phone number: {}", needyRegistrationRequest.getPhoneNumber());
        if(needyRepository.findByPhoneNumber(needyRegistrationRequest.getPhoneNumber()).isPresent())
            throw new UserAlreadyExistsException("Needy already exists");

        Needy needy = new Needy();
        needy.setFirstName(needyRegistrationRequest.getFirstName());
        needy.setLastName(needyRegistrationRequest.getLastName());
        needy.setPhoneNumber(needyRegistrationRequest.getPhoneNumber());
        needy.setAddress(needyRegistrationRequest.getAddress());
        needy.setRole(UserRole.NEEDY);
        needy.setConfirmStatus(NeedyStatus.PENDING);
        needy.setFamilySize(needyRegistrationRequest.getFamilySize());
        needy.setStreet(needyRegistrationRequest.getStreet());

        needyRepository.save(needy);
        logger.info("end-register needy, phone number: {}", needyRegistrationRequest.getPhoneNumber());
    }

    public void initiatePasswordReset(String phoneNumber) {
        logger.info("start-initiatePasswordReset,  phone number: {}", phoneNumber);
        UserCredentials credentials = userCredentialsRepository.findCredentialsByPhoneNumber(phoneNumber);
        if(credentials == null)
            throw new UserDoesntExistsException("User not found");

        Donor donor = credentials.getDonor();

        String code = generateVerificationCode();
        donor.setVerificationCode(code);
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        donorRepository.save(donor);
        sendVerificationEmail(donor);
        logger.info("end-initiatePasswordReset, phone number: {}", phoneNumber);
    }

    public void confirmPasswordReset(String phoneNumber, String verificationCode, String newPassword) {
        logger.info("start-confirmPasswordReset,  phone number: {}", phoneNumber);
        UserCredentials credentials = userCredentialsRepository.findCredentialsByPhoneNumber(phoneNumber);
        if(credentials == null)
            throw new UserDoesntExistsException("User not found");

        Donor donor = credentials.getDonor();

        String donorCode = donor.getVerificationCode();
        if(donorCode == null || donor.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Invalid verification code");

        if(donorCode.equals(verificationCode)){
            donor.setVerificationCode(null);
            donor.setVerificationCodeExpiresAt(null);
            donorRepository.save(donor);

            credentials.setPasswordHash(passwordEncoder.encode(newPassword));
            credentials.setLastPasswordChangeAt(LocalDateTime.now());
            userCredentialsRepository.save(credentials);
            logger.info("end-confirmPasswordReset, phone number: {}", phoneNumber);
        }
        else
            throw new RuntimeException("Invalid verification code");
    }

    public void sendVerificationEmail(Donor donor) {
        logger.info("start-send verification email, for email: {}", donor.getEmail());
        String subject = "איפוס סיסמה";
        String verificationCode = donor.getVerificationCode();
        String htmlMessage = "<!DOCTYPE html>"
                + "<html dir=\"rtl\" lang=\"he\">"
                + "<head>"
                + "    <meta charset=\"UTF-8\">"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "    <title>איפוס סיסמה</title>"
                + "</head>"
                + "<body style=\"margin:0; padding:0; background-color:#f2f4f6; font-family: Arial, sans-serif;\">"
                + "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
                + "        <tr>"
                + "            <td align=\"center\">"
                + "                <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 4px 6px rgba(0,0,0,0.1);\">"
                + "                    <!-- Header -->"
                + "                    <tr>"
                + "                        <td align=\"center\" style=\"background-color:#d32f2f; padding:20px;\">"
                + "                            <span style=\"color:#ffffff; font-size:24px; font-weight:bold;\">להשביע את הלב</span>"
                + "                        </td>"
                + "                    </tr>"
                + "                    <!-- Content -->"
                + "                    <tr>"
                + "                        <td style=\"padding:30px; text-align:right;\">"
                + "                            <h2 style=\"color:#333333;\">איפוס סיסמה</h2>"
                + "                            <p style=\"font-size:16px; line-height:1.5;\">שלום <strong>" + donor.getFirstName() + "</strong>,</p>"
                + "                            <p style=\"font-size:16px; line-height:1.5;\">:אנא הזן את קוד האימות למטה והקלד סיסמה חדשה</p>"
                + "                            <div style=\"background-color:#f9f9f9; border-left:4px solid #d32f2f; padding:15px; margin:20px 0; text-align:center;\">"
                + "                                <p style=\"font-size:24px; font-weight:bold; color:#d32f2f; margin:0;\">" + verificationCode + "</p>"
                + "                            </div>"
                + "                            <p style=\"font-size:16px; line-height:1.5;\">.אם לא ביקשת איפוס סיסמה, אנא התעלם ממייל זה</p>"
                + "                        </td>"
                + "                    </tr>"
                + "                    <!-- Footer -->"
                + "                    <tr>"
                + "                        <td style=\"background-color:#f2f4f6; padding:20px; text-align:center; font-size:12px; color:#888888;\">"
                + "                            <p>© 2025 Your Company. כל הזכויות שמורות.</p>"
                + "                        </td>"
                + "                    </tr>"
                + "                </table>"
                + "            </td>"
                + "        </tr>"
                + "    </table>"
                + "</body>"
                + "</html>";

        try{
            emailService.sendVerificationEmail(donor.getEmail(), subject, htmlMessage);
            logger.info("end-send verification email, for email: {}", donor.getEmail());
        } catch (Exception e) {
            logger.info("end-send verification email - failed, for email: {}", donor.getEmail());
            throw new UserDoesntExistsException("cant send verification email"); // change the error type
        }
    }

    public String generateVerificationCode() {
        logger.info("start-generate verification code");
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        logger.info("end-generate verification code");
        return String.valueOf(code);
    }

    public void resendVerificationEmail(String email) {
        logger.info("start-resend verification code, email: {}", email);
        Optional<Donor> optionalDonor = donorRepository.findByEmail(email);
        if(optionalDonor.isPresent()){
            Donor donor = optionalDonor.get();

            donor.setVerificationCode(generateVerificationCode());
            donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(donor);
            donorRepository.save(donor);
            logger.info("end-resend verification code, email: {}", email);
        }
        else
            throw new UserDoesntExistsException("donor not found");
    }

    public UserRole getUserRoleFromJWT(String token) {
        logger.info("start-get user role from token: {}", token);
        if(token == null)
            throw new IllegalArgumentException("invalid token");
        if(token.startsWith("Bearer "))
            token = token.substring(7);

        String phoneNumber = jwtTokenService.extractUsername(token);
        Optional<Donor> optionalDonor = donorRepository.findByPhoneNumber(phoneNumber);
        if(optionalDonor.isPresent()){
            Donor donor = optionalDonor.get();
            UserRole role = donor.getRole();
            logger.info("end-get user role {}", role);
            return role;
        }
        else
            throw new UserDoesntExistsException("donor not found");
    }

    public long getUserIDFromJWT(String token) {
        logger.info("start-get user id from token: {}", token);
        if(token == null)
            throw new IllegalArgumentException("invalid token");
        if(token.startsWith("Bearer "))
            token = token.substring(7);

        String phoneNumber = jwtTokenService.extractUsername(token);
        Optional<Donor> optionalDonor = donorRepository.findByPhoneNumber(phoneNumber);
        if(optionalDonor.isPresent()){
            Donor donor = optionalDonor.get();
            long id = donor.getId();
            logger.info("end-get user id {}", id);
            return id;
        }
        else
            throw new UserDoesntExistsException("donor not found");
    }
}