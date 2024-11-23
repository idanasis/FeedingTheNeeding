package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.User.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private IUserRepository userRepository;
}
