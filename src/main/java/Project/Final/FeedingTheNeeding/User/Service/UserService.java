package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeederRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final DonorRepository donorRepository;
    private final NeederRepository neederRepository;

    public UserService(DonorRepository donorRepository, NeederRepository neederRepository) {
        this.donorRepository = donorRepository;
        this.neederRepository = neederRepository;
    }
}