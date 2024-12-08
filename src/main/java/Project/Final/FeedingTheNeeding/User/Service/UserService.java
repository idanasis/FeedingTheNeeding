package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final DonorRepository donorRepository;
    private final NeedyRepository needyRepository;

    public UserService(DonorRepository donorRepository, NeedyRepository needyRepository) {
        this.donorRepository = donorRepository;
        this.needyRepository = needyRepository;
    }
}