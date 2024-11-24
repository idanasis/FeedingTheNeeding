package Project.Final.FeedingTheNeeding.Authentication.Facade;

import Project.Final.FeedingTheNeeding.Authentication.Repository.IAuthRepository;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import Project.Final.FeedingTheNeeding.config.TokenService;
import org.springframework.stereotype.Service;

@Service
public class AuthFacade {

    private TokenService tokenService;
    private IAuthRepository authRepository;
    private UserService userService;

    public AuthFacade(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
