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
}