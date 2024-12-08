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

@Service
public class AuthService {

    private final DonorRepository donorRepository;
    private final NeedyRepository needyRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    public AuthService(DonorRepository donorRepository, NeedyRepository needyRepository, UserCredentialsRepository userCredentialsRepository, JwtTokenService jwtTokenService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.donorRepository = donorRepository;
        this.needyRepository = needyRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
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

        Donor savedDonor = donorRepository.save(donor);

        UserCredentials credentials = new UserCredentials();
        credentials.setEmail(registrationRequest.getEmail());
        credentials.setPasswordHash(passwordEncoder.encode(registrationRequest.getPassword()));
        credentials.setLastPasswordChangeAt(LocalDateTime.now());
        credentials.setBaseUser(savedDonor);

        userCredentialsRepository.save(credentials);
    }

    public void registerNeedy(NeedyRegistrationRequest needyRegistrationRequest) {
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
        needy.setDietaryPreferences(needyRegistrationRequest.getDietaryPreferences());
        needy.setAdditionalNotes(needyRegistrationRequest.getAdditionalNotes());

        needyRepository.save(needy);
    }

    public void resetPassword(String email, String newPassword) {
        UserCredentials credentials = userCredentialsRepository.findCredentialsByEmail(email);
        if(credentials == null)
            throw new UserDoesntExistsException("User not found");

        credentials.setPasswordHash(passwordEncoder.encode(newPassword));
        credentials.setLastPasswordChangeAt(LocalDateTime.now());

        userCredentialsRepository.save(credentials);
    }

    public boolean validateToken(String token) {
        try{
            String email = jwtTokenService.extractEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            return jwtTokenService.validateToken(token, userDetails);
        }catch (Exception e){
            return false;
        }
    }
}