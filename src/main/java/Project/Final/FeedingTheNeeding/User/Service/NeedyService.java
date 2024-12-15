package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Repository.NeedyRepository;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NeedyService {

    NeedyRepository needyRepository;
    NeederTrackingRepository neederTrackingRepository;

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

    public List<NeederTracking> getNeedyUsersTrackingByData(LocalDate localDate) {
        List<NeederTracking> allTracking = neederTrackingRepository.findByDate(localDate);
        List<Needy> neeedyList = allTracking.stream()
                .map(NeederTracking::getNeedy)
                .toList();
        List<Needy> allNeedyList = needyRepository.findAll();

        List<Needy> diff = allNeedyList.stream()
                .filter(item -> !neeedyList.contains(item))
                .toList();

        for(Needy needy : diff) {
            NeederTracking neederTracking = new NeederTracking();
            neederTracking.setNeedy(needy);
            neederTracking.setDate(localDate);
            neederTracking.setDietaryPreferences(null);
            neederTracking.setAdditionalNotes(null);
            neederTrackingRepository.save(neederTracking);
            allTracking.add(neederTracking);
        }

        return allTracking;
    }
}
