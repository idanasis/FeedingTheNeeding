package Project.Final.FeedingTheNeeding.Authentication.Controller;

import Project.Final.FeedingTheNeeding.Authentication.DTO.AuthenticationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.AuthenticationResponse;
import Project.Final.FeedingTheNeeding.Authentication.DTO.NeedyRegistrationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationRequest;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try{
            AuthenticationResponse response = authService.authenticate(authenticationRequest);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
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

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        boolean isValid = authService.validateToken(cleanToken);
        return ResponseEntity.ok(isValid);
    }

}
