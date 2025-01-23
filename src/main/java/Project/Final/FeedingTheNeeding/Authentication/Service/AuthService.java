package Project.Final.FeedingTheNeeding.Authentication.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.*;
import Project.Final.FeedingTheNeeding.Authentication.Exception.AccountNotVerifiedException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserAlreadyExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Repository.UserCredentialsRepository;
import Project.Final.FeedingTheNeeding.Authentication.Exception.InvalidCredentialException;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
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

        // Check if the user account is enabled (if applicable)
        if (!user.isEnabled()) {
            logger.warn("Account not verified for phone number: {}", authenticationRequest.getPhoneNumber());
            throw new AccountNotVerifiedException("Account not verified. Please verify your account.");
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
        donor.setVerificationCode(generateVerificationCode());
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        donor.setVerified(true);
        donor.setFirstName(registrationRequest.getFirstName());
        donor.setLastName(registrationRequest.getLastName());
        donor.setPhoneNumber(registrationRequest.getPhoneNumber());
        donor.setAddress(registrationRequest.getAddress());
        donor.setRole(UserRole.DONOR);
        donor.setStatus(RegistrationStatus.PENDING);
        donor.setTimeOfDonation(0);

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

        needyRepository.save(needy);
        logger.info("end-register needy, phone number: {}", needyRegistrationRequest.getPhoneNumber());
    }

    public void initiatePasswordReset(String phoneNumber) {
        logger.info("start-initiatePasswordReset,  phone number: {}", phoneNumber);
        UserCredentials credentials = userCredentialsRepository.findCredentialsByPhoneNumber(phoneNumber);
        if(credentials == null)
            throw new UserDoesntExistsException("User not found");

        String code = generateVerificationCode();
        credentials.getDonor().setVerificationCode(code);
        credentials.getDonor().setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        donorRepository.save(credentials.getDonor());
        // SEND THE CODE TO THE DONOR PHONE

        logger.info("end-initiatePasswordReset, phone number: {}", phoneNumber);
    }

    public void confirmPasswordReset(String phoneNumber, String verificationCode, String newPassword) {
        logger.info("start-confirmPasswordReset,  phone number: {}", phoneNumber);
        UserCredentials credentials = userCredentialsRepository.findCredentialsByPhoneNumber(phoneNumber);
        if(credentials == null)
            throw new UserDoesntExistsException("User not found");

        String code = credentials.getDonor().getVerificationCode();
        if(code == null || credentials.getDonor().getVerificationCodeExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Invalid verification code");

        credentials.setPasswordHash(passwordEncoder.encode(newPassword));
        credentials.setLastPasswordChangeAt(LocalDateTime.now());
        userCredentialsRepository.save(credentials);

        credentials.getDonor().setVerificationCode(null);
        credentials.getDonor().setVerificationCodeExpiresAt(null);
        donorRepository.save(credentials.getDonor());
        logger.info("end-confirmPasswordReset, phone number: {}", phoneNumber);
    }

    public void resetPassword(String phoneNumber, String newPassword) {
        logger.info("start-reset password, for phoneNumber: {}", phoneNumber);
        UserCredentials credentials = userCredentialsRepository.findCredentialsByPhoneNumber(phoneNumber);
        if(credentials == null)
            throw new UserDoesntExistsException("User not found");

        credentials.setPasswordHash(passwordEncoder.encode(newPassword));
        credentials.setLastPasswordChangeAt(LocalDateTime.now());

        userCredentialsRepository.save(credentials);
        logger.info("end-reset password, for phoneNumber: {}", phoneNumber);
    }

    public void sendVerificationEmail(Donor donor) {
        logger.info("start-send verification email, for email: {}", donor.getEmail());
        String subject = "Account Verification";
        String verificationCode = donor.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Feeding The Needing</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code: </h3>"
                + "<p style=\"font-size: 30px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
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

    public void verifyDonor(VerifyDonorDTO input) {
        logger.info("start-verify donor, phoneNumber: {}", input.phoneNumber());
        Optional<Donor> optionalDonor = donorRepository.findByPhoneNumber(input.phoneNumber());
        if(optionalDonor.isPresent()){
            Donor donor = optionalDonor.get();
            if(donor.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Verification code expired");

            if(donor.getVerificationCode().equals(input.verificationCode())){
                donor.setVerified(true);
                donor.setVerificationCode(null);
                donor.setVerificationCodeExpiresAt(null);
                donor.setStatus(RegistrationStatus.PENDING);
                donorRepository.save(donor);
                logger.info("end-verify donor, phoneNumber: {}", input.phoneNumber());
            }
            else
                throw new RuntimeException("Invalid verification code");
        }
        else
            throw new UserDoesntExistsException("User not found");
    }

    public void resendVerificationEmail(String email) {
        logger.info("start-resend verification code, email: {}", email);
        Optional<Donor> optionalDonor = donorRepository.findByEmail(email);
        if(optionalDonor.isPresent()){
            Donor donor = optionalDonor.get();
            if(donor.isVerified())
                throw new RuntimeException("account is already verified");

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