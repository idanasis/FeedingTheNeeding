package Project.Final.FeedingTheNeeding.social.service;


import Project.Final.FeedingTheNeeding.social.exception.NeederTrackingNotFoundException;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NeederTrackingService {

    private final NeederTrackingRepository neederTrackingRepository;

    public NeederTrackingService(NeederTrackingRepository neederTrackingRepository) {
        this.neederTrackingRepository = neederTrackingRepository;
    }

    public List<NeederTracking> getAllNeeders() {
        return neederTrackingRepository.findAll();
    }

    public NeederTracking getNeederById(Long id) {
        return neederTrackingRepository.findById(id)
                .orElseThrow(() -> new NeederTrackingNotFoundException("Needer not found with ID: " + id));
    }

    public NeederTracking addNeeder(NeederTracking neederTracking) {
        return neederTrackingRepository.save(neederTracking);
    }

    public NeederTracking updateNeeder(Long id, NeederTracking updatedNeeder) {
        NeederTracking neederTracking = getNeederById(id);

        neederTracking.setName(updatedNeeder.getName());
        neederTracking.setAddress(updatedNeeder.getAddress());
        neederTracking.setPhone(updatedNeeder.getPhone());
        neederTracking.setStatusForWeek(updatedNeeder.getStatusForWeek());
        neederTracking.setFamilySize(updatedNeeder.getFamilySize());
        neederTracking.setDietaryPreferences(updatedNeeder.getDietaryPreferences());
        neederTracking.setAdditionalNotes(updatedNeeder.getAdditionalNotes());

        return neederTrackingRepository.save(neederTracking);
    }

    public void deleteNeeder(Long id) {
        neederTrackingRepository.deleteById(id);
    }
}
