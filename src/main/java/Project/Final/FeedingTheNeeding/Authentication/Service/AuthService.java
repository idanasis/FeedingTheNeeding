package Project.Final.FeedingTheNeeding.Authentication.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.*;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserAlreadyExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Repository.UserCredentialsRepository;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    public AuthService(DonorRepository donorRepository, NeedyRepository needyRepository, UserCredentialsRepository userCredentialsRepository, JwtTokenService jwtTokenService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, EmailService emailService) {
        this.donorRepository = donorRepository;
        this.needyRepository = needyRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        logger.info("start-auth, email: {}", authenticationRequest.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = jwtTokenService.generateToken(userDetails);

        logger.info("end-auth, token: {}", token);
        return AuthenticationResponse.builder().token(token).build();
    }

    @Transactional
    public void registerDonor(RegistrationRequest registrationRequest) {
        logger.info("start-register donor,  email: {}", registrationRequest.getEmail());
        if(donorRepository.findByEmail(registrationRequest.getEmail()).isPresent())
            throw new UserAlreadyExistsException("Donor already exists");

        // TODO: check for valid email, valid password, password == confirmPassword

        Donor donor = new Donor();
        donor.setId(1L); // TODO: change it
        donor.setEmail(registrationRequest.getEmail());
        donor.setFirstName(registrationRequest.getFirstName());
        donor.setLastName(registrationRequest.getLastName());
        donor.setPhoneNumber(registrationRequest.getPhoneNumber());
        donor.setAddress(registrationRequest.getAddress());
        donor.setCity(registrationRequest.getCity());
        donor.setRole(UserRole.DONOR);
        donor.setStatus(RegistrationStatus.PENDING);
        donor.setTimeOfDonation(0);

        donor.setVerificationCode(generateVerificationCode());
        donor.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        donor.setVerified(false);
        sendVerificationEmail(donor);

        Donor savedDonor = donorRepository.save(donor);

        UserCredentials credentials = new UserCredentials();
        credentials.setEmail(registrationRequest.getEmail());
        credentials.setPasswordHash(passwordEncoder.encode(registrationRequest.getPassword()));
        credentials.setLastPasswordChangeAt(LocalDateTime.now());
        credentials.setBaseUser(savedDonor);

        userCredentialsRepository.save(credentials);
        logger.info("end-register donor, email: {}", registrationRequest.getEmail());
    }

    public void registerNeedy(NeedyRegistrationRequest needyRegistrationRequest) {
        logger.info("start-register needy,  phone number: {}", needyRegistrationRequest.getPhoneNumber());
        if(needyRepository.findByPhoneNumber(needyRegistrationRequest.getPhoneNumber()).isPresent())
            throw new UserAlreadyExistsException("Needy already exists");

        Needy needy = new Needy();
        needy.setId(1L); // TODO: change it
        needy.setFirstName(needyRegistrationRequest.getFirstName());
        needy.setLastName(needyRegistrationRequest.getLastName());
        needy.setPhoneNumber(needyRegistrationRequest.getPhoneNumber());
        needy.setAddress(needyRegistrationRequest.getAddress());
        needy.setCity(needyRegistrationRequest.getCity());
        needy.setRole(UserRole.NEEDY);
        needy.setConfirmStatus(NeedyStatus.PENDING);
        needy.setFamilySize(needyRegistrationRequest.getFamilySize());

        needyRepository.save(needy);
        logger.info("end-register needy, phone number: {}", needyRegistrationRequest.getPhoneNumber());
    }

    public void resetPassword(String email, String newPassword) {
        logger.info("start-reset password, for email: {}", email);
        UserCredentials credentials = userCredentialsRepository.findCredentialsByEmail(email);
        if(credentials == null)
            throw new UserDoesntExistsException("User not found");

        credentials.setPasswordHash(passwordEncoder.encode(newPassword));
        credentials.setLastPasswordChangeAt(LocalDateTime.now());

        userCredentialsRepository.save(credentials);
        logger.info("end-reset password, for email: {}", email);
    }

    public boolean validateToken(String token) {
        logger.info("start-reset password, for token: {}", token);
        try{
            String email = jwtTokenService.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            logger.info("end-validate token - success, for token: {}", token);
            return jwtTokenService.validateToken(token, userDetails);
        }catch (Exception e){
            logger.info("end-reset password - failed, for token: {}", token);
            return false;
        }
    }

    private void sendVerificationEmail(Donor donor) {
        logger.info("start-send verification email, for email: {}", donor.getEmail());
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE" + donor.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Feeding The Needing</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
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

    private String generateVerificationCode() {
        logger.info("start-generate verification code");
        Random random = new Random();
        int code = random.nextInt(900000) + 10000;
        logger.info("end-generate verification code");
        return String.valueOf(code);
    }

    public void verifyDonor(VerifyDonorDTO input) {
        logger.info("start-verify donor, email: {}", input.email());
        Optional<Donor> optionalDonor = donorRepository.findByEmail(input.email());
        if(optionalDonor.isPresent()){
            Donor donor = optionalDonor.get();
            if(donor.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Verification code expired");

            if(donor.getVerificationCode().equals(input.verificationCode())){
                donor.setVerified(true);
                donor.setVerificationCode(null);
                donor.setVerificationCodeExpiresAt(null);
                donorRepository.save(donor);
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
        }
        else
            throw new UserDoesntExistsException("donor not found");
    }
}