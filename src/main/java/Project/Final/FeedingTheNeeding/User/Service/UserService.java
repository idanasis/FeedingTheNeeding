package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Repository.DonatorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeederRepository;
import Project.Final.FeedingTheNeeding.User.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final DonatorRepository donatorRepository;
    private final NeederRepository neederRepository;

    public UserService(DonatorRepository donatorRepository, NeederRepository neederRepository) {
        this.donatorRepository = donatorRepository;
        this.neederRepository = neederRepository;
    }

    public void login(String email, String password) {

    }

    public void register(String email, String firstName, String lastName, String phoneNumber, String address, String city) {

    }
}