package Project.Final.FeedingTheNeeding.Authentication.Facade;

import Project.Final.FeedingTheNeeding.Authentication.Exception.InvalidJWTException;
import Project.Final.FeedingTheNeeding.Authentication.Repository.AuthRepository;
import Project.Final.FeedingTheNeeding.User.Model.RegistrationRequest;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import Project.Final.FeedingTheNeeding.config.TokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class AuthFacade {

    private TokenService tokenService;
    private UserService userService;
    private AuthRepository authRepository;

    private static final Logger logger = LogManager.getLogger(AuthFacade.class);

    public AuthFacade(TokenService tokenService, UserService userService, AuthRepository authRepository) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.authRepository = authRepository;
    }

    public String login(String email, String password) {
        logger.info("start-login, email: {}", email);
        String token = auth(email, password);
        userService.login(email, password);
        logger.info("end-login, token: {}", token);
        return token;
    }

    public String auth(String email, String password) {
        logger.info("start-auth, email: {}", email);
        authRepository.login(email, password); // if the user is authenticated, generate a jwt token for him
        String token = tokenService.generateToken(email);
        logger.info("end-auth, token: {}", token);
        return token;
    }

    public String login(String jwt) {
        if(!tokenService.validateToken(jwt))
            throw new InvalidJWTException("Invalid JWT");
        else
            return tokenService.extractUsername(jwt);
    }

    public void register(RegistrationRequest registrationRequest) {
        logger.info("start-register, registrationRequest: {}", registrationRequest);
        // TODO: check if password == confirmPassword
        authRepository.add(registrationRequest.getEmail(), registrationRequest.getPassword());
        userService.register(registrationRequest.getEmail(), registrationRequest.getFirstName(), registrationRequest.getLastName(), registrationRequest.getPhoneNumber(), registrationRequest.getAddress(), registrationRequest.getCity());
        logger.info("end-register, email: {}", registrationRequest.getEmail());
    }

    public void clear(){
        authRepository.clear();
    }

    public void logout(String token) {
        // TODO: invalidate the token
    }
}