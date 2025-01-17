package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.Authentication.Exception.UserDoesntExistsException;
import Project.Final.FeedingTheNeeding.Authentication.Service.AuthService;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Repository.DonorRepository;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import jakarta.transaction.Transactional;

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

    public Donor getDonorById(long id) {
        logger.info("getDonorById");
        return donorRepository.findById(id).orElseThrow(() -> new UserDoesntExistsException("User not found"));
    }

    public List<Donor> getDonorsPending(){
        logger.info("getDonorsPending");
        return donorRepository.findByStatus(RegistrationStatus.PENDING);
    }
  
    public List<Donor> getDonorsApproved(){
        logger.info("getDonorsPending");
        return donorRepository.findByStatus(RegistrationStatus.AVAILABLE);
    }
  
    public void updateDonor(Donor donor){
        logger.info("updateDonor");
        Donor upDonor = donorRepository.findById(donor.getId()).orElseThrow(() -> new UserDoesntExistsException("User not found"));
        upDonor.setPhoneNumber(donor.getPhoneNumber());
        upDonor.setFirstName(donor.getFirstName());
        upDonor.setLastName(donor.getLastName());
        upDonor.setAddress(donor.getAddress());
        upDonor.setStatus(donor.getStatus());
        upDonor.setEmail(donor.getEmail());
        upDonor.setRole(donor.getRole());
        upDonor.setLastDonationDate(donor.getLastDonationDate());
        donorRepository.save(upDonor);
        logger.info("Donor "+donor.getId()+" updated");
    }

    @Transactional
    public void deleteDonor(long id){
        logger.info("deleteDonor");
        if (donorRepository.existsById(id)) {
            Donor donor = donorRepository.findById(id).get();
            donor.setUserCredentials(null);
            donorRepository.save(donor);
            donorRepository.deleteById(id);
            logger.info("Donor "+id+" deleted");
        } else {
            logger.error("User "+id+" not found");
            throw new UserDoesntExistsException("User "+id+" not found");
        }
    }
}