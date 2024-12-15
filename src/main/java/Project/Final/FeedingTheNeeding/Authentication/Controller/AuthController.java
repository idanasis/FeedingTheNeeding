package Project.Final.FeedingTheNeeding.Authentication.Controller;

import Project.Final.FeedingTheNeeding.Authentication.DTO.*;
import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.Authentication.Service.JwtTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenService jwtTokenService;

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
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/donor")
    public ResponseEntity<?> registerDonor(@RequestBody RegistrationRequest registrationRequest) {
        try{
            authService.registerDonor(registrationRequest);
            return ResponseEntity.ok("Donor successfully registered");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
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

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            authService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyDonor(@RequestBody VerifyDonorDTO verifyDonorDTO) {
        try{
            authService.verifyDonor(verifyDonorDTO);
            return ResponseEntity.ok("Donor successfully verified");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try{
            authService.resendVerificationEmail(email);
            return ResponseEntity.ok("Email code resend successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}