package Project.Final.FeedingTheNeeding.Authentication.Controller;

import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

//    @PostMapping("/register")
//    public BaseUserDTO register(@RequestBody RegistrationRequest registrationRequest) {
//        return authFacade.registerUser(registrationRequest);
//    }

//    @PostMapping("/login")
//    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
//        return authFacade.login(loginRequest);
//    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
    }
}