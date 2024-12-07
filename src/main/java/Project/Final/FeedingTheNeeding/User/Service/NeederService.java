package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Controller.NeederController;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Repository.NeederRepository;
import aj.org.objectweb.asm.commons.Remapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NeederService {

    NeederRepository neederRepository;

    public NeederService(NeederRepository neederRepository) {
        this.neederRepository=  neederRepository;
    }

    public List<Needy> getPendingNeedy() {
        return neederRepository.findByConfirmStatus(NeedyStatus.PENDING);
    }

    // Create or Update a Needy user
    public Needy saveOrUpdateNeedy(Needy needy) {
        return neederRepository.save(needy);
    }

    // Get all Needy users
    public List<Needy> getAllNeedyUsers() {
        return neederRepository.findAll();
    }

    // Get a specific Needy user by ID
    public Optional<Needy> getNeedyById(Long id) {
        return neederRepository.findById(id);
    }

    // Delete a Needy user by ID
    public void deleteNeedyById(Long id) {
        if (neederRepository.existsById(id)) {
            neederRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Needy user with ID " + id + " does not exist.");
        }
    }


    public Optional<BaseUser> getNeedyByPhoneNumber(String phoneNumber) {
        return neederRepository.findByPhoneNumber(phoneNumber);
    }
}
