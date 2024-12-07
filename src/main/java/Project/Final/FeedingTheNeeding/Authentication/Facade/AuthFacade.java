package Project.Final.FeedingTheNeeding.Authentication.Facade;

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

//    public BaseUserDTO registerUser(RegistrationRequest registrationRequest) {
//        if(userService.existByEmail(registrationRequest.getEmail()))
//            throw new UserAlreadyExistsException("Email already registered to the system.");
//
//        // TODO: check if the email and the password are correct according to the criteria
//        // TODO: check if password == confirm password
//        // TODO: encode the password
//
//        BaseUser user = BaseUser.builder()
//                .firstName(registrationRequest.getFirstName())
//                .lastName(registrationRequest.getLastName())
//                .phoneNumber(registrationRequest.getPhoneNumber())
//                .address(registrationRequest.getAddress())
//                .city(registrationRequest.getCity())
//                .build();
//
//        user = userService.saveUser(user);
//        // TODO: sending a verification email
//
//        return new BaseUserDTO(user);
//    }

//    public LoginResponse login(LoginRequest loginRequest) {
//        BaseUser user = userService.findByEmail(loginRequest.getEmail())
//                .orElseThrow(() -> new InvalidCredentialException("Invalid email."));
//
////        if(!user.getPassword().equals(loginRequest.getPassword())) // TODO: decode the password and check
////            throw new InvalidCredentialException("Invalid password.");
////
////        if(user.getStatus() == UserStatus.ACTIVE)
////            throw new UserAlreadyLoggedInException("User already logged in to the system");
//
//        String token = tokenService.generateToken(user.getFirstName());
//
//        return new LoginResponse(token, new BaseUserDTO(user));
//    }

    public void logout(String token) {
        // TODO: invalidate the token
    }
}