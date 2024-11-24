package Project.Final.FeedingTheNeeding.Authentication.Facade;

import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
import Project.Final.FeedingTheNeeding.User.Exception.*;
import Project.Final.FeedingTheNeeding.User.Model.*;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import Project.Final.FeedingTheNeeding.config.TokenService;
import org.springframework.stereotype.Service;

@Service
public class AuthFacade {

    private final TokenService tokenService;
    private UserService userService;

    public AuthFacade(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    public BaseUserDTO registerUser(RegistrationRequest registrationRequest) {
        if(userService.existByEmail(registrationRequest.getEmail()))
            throw new UserAlreadyExistsException("Email already registered to the system.");

        // TODO: check if the email and the password are correct according to the criteria
        // TODO: check if password == confirm password
        // TODO: encode the password

        BaseUser user = BaseUser.builder()
                .email(registrationRequest.getEmail())
                .password(registrationRequest.getPassword())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .address(registrationRequest.getAddress())
                .city(registrationRequest.getCity())
                .status(UserStatus.PENDING)
                .build();

        user = userService.saveUser(user);
        // TODO: sending a verification email

        return new BaseUserDTO(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        BaseUser user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialException("Invalid email."));
        
    }
}
