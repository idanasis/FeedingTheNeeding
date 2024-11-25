package Project.Final.FeedingTheNeeding.Authentication.Controller;

import Project.Final.FeedingTheNeeding.Authentication.Facade.AuthFacade;
import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
import Project.Final.FeedingTheNeeding.User.Model.LoginRequest;
import Project.Final.FeedingTheNeeding.User.Model.LoginResponse;
import Project.Final.FeedingTheNeeding.User.Model.RegistrationRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/register")
    public BaseUserDTO register(@RequestBody RegistrationRequest registrationRequest) {
        return authFacade.registerUser(registrationRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authFacade.login(loginRequest);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authFacade.logout(token);
    }
}