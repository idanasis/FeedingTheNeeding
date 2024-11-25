package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Controller.UserController;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private IUserRepository userRepository;

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<BaseUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public BaseUser saveUser(BaseUser baseUser) {
        return userRepository.save(baseUser);
    }
}