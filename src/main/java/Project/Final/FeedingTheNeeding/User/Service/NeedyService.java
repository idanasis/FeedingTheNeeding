package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NeedyService {

    NeedyRepository needyRepository;

    public NeedyService(NeedyRepository needyRepository) {
        this.needyRepository = needyRepository;
    }

    public List<Needy> getPendingNeedy() {
        return needyRepository.findByConfirmStatus(NeedyStatus.PENDING);
    }

    // Create or Update a Needy user
    public Needy saveOrUpdateNeedy(Needy needy) {
        return needyRepository.save(needy);
    }

    // Get all Needy users
    public List<Needy> getAllNeedyUsers() {
        return needyRepository.findAll();
    }

    // Get a specific Needy user by ID
    public Optional<Needy> getNeedyById(Long id) {
        return needyRepository.findById(id);
    }

    // Delete a Needy user by ID
    public void deleteNeedyById(Long id) {
        if (needyRepository.existsById(id)) {
            needyRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Needy user with ID " + id + " does not exist.");
        }
    }


    public Optional<Needy> getNeedyByPhoneNumber(String phoneNumber) {
        return needyRepository.findByPhoneNumber(phoneNumber);
    }
}
