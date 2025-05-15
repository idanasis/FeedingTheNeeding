package Project.Final.FeedingTheNeeding.Authentication.Controller;

import Project.Final.FeedingTheNeeding.Authentication.DTO.*;
import Project.Final.FeedingTheNeeding.Authentication.Exception.AccountNotVerifiedException;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserAlreadyExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.Authentication.Service.JwtTokenService;
import Project.Final.FeedingTheNeeding.User.Model.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenService jwtTokenService;

    private static final Logger logger = LogManager.getLogger(AuthService.class);


    public AuthController(AuthService authService, JwtTokenService jwtTokenService) {
        this.authService = authService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try{
            UserCredentials user = authService.authenticate(authenticationRequest);
            String jwtToken = jwtTokenService.generateToken(user);
            AuthenticationResponse response = new AuthenticationResponse(jwtToken, jwtTokenService.getExpirationTime());
            logger.info("token created: {} with expiration time of {}", response.getToken(),response.getExpirationTime());
            return ResponseEntity.ok(response);
        }catch (AccountNotVerifiedException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try{
            if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
                return ResponseEntity.ok("No token provided. nothing to invalidate");

            String token = authorizationHeader.substring(7);
            authService.logout(token);
            return ResponseEntity.ok("logged out successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/donor")
    public ResponseEntity<?> registerDonor(@RequestBody RegistrationRequest registrationRequest) {
        try{
            System.out.println(registrationRequest.getStreet());
            authService.registerDonor(registrationRequest);
            return ResponseEntity.ok("Donor successfully registered");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Registration failed. Please try again later.");
        }
    }

    @PostMapping("/register/needy")
    public ResponseEntity<?> registerNeedy(@RequestBody NeedyRegistrationRequest needyRegistrationRequest) {
        try{
            authService.registerNeedy(needyRegistrationRequest);
            return ResponseEntity.ok("Needy successfully registered");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/request-reset-password")
    public ResponseEntity<?> requestResetPassword(@RequestParam String phoneNumber) {
        try {
            authService.initiatePasswordReset(phoneNumber);
            return ResponseEntity.ok("Verification code sent to your phone number.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm-reset-password")
    public ResponseEntity<?> confirmResetPassword(
            @RequestParam String phoneNumber,
            @RequestParam String verificationCode,
            @RequestParam String newPassword) {
        try {
            authService.confirmPasswordReset(phoneNumber, verificationCode, newPassword);
            return ResponseEntity.ok("Password has been reset successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-email")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try{
            authService.resendVerificationEmail(email);
            return ResponseEntity.ok("Email code resend successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/user-role")
    public ResponseEntity<?> getUserRoleFromJWT(@RequestParam String token) {
        try{
            UserRole role = authService.getUserRoleFromJWT(token);
            return ResponseEntity.ok(role);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/user-id")
    public ResponseEntity<?> getUserIDFromJWT(@RequestParam String token) {
        try{
            long id = authService.getUserIDFromJWT(token);
            return ResponseEntity.ok(id);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}