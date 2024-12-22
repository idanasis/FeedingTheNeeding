package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final DonorRepository donorRepository;
    private final NeedyRepository needyRepository;

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    public UserService(DonorRepository donorRepository, NeedyRepository needyRepository) {
        this.donorRepository = donorRepository;
        this.needyRepository = needyRepository;
    }

    public List<Donor> getAllDonors() {
        logger.info("getAllDonors");
        return new ArrayList<>(donorRepository.findAll());
    }

    public List<Needy> getAllNeedyUsers() {
        logger.info("getAllNeedyUsers");
        return new ArrayList<>(needyRepository.findAll());
    }

    public List<BaseUser> getAll() {
        logger.info("getAllUsers");
        List<BaseUser> users = new ArrayList<>();
        donorRepository.findAll().forEach(users::add);
        needyRepository.findAll().forEach(users::add);
        return users;
    }

    public Needy getNeedyByPhoneNumber(String phoneNumber) {
        logger.info("getNeedyByPhoneNumber");
        return needyRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UserDoesntExistsException("User not found"));
    }

    public Donor getDonorByPhoneNumber(String phoneNumber) {
        logger.info("getDonorByPhoneNumber");
        return donorRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UserDoesntExistsException("User not found"));
    }
}