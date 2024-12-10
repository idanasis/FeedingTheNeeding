package Project.Final.FeedingTheNeeding.social.service;

import Project.Final.FeedingTheNeeding.social.dto.NeedySimpleDTO;
import Project.Final.FeedingTheNeeding.social.exception.NeederTrackingNotFoundException;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.model.WeekStatus;
import Project.Final.FeedingTheNeeding.social.projection.NeederTrackingProjection;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NeederTrackingService {

    private final NeederTrackingRepository neederTrackingRepository;

    public NeederTrackingService(NeederTrackingRepository neederTrackingRepository) {
        this.neederTrackingRepository = neederTrackingRepository;
    }

    // Fetch all NeederTracking records, including Needy details
    public List<NeederTracking> getAllNeedersTrackings() {
        return neederTrackingRepository.findAll(); // Uses @EntityGraph to fetch Needy
    }

    // Fetch a single NeederTracking record by ID, including Needy details
    public NeederTracking getNeederTrackById(Long id) {
        return neederTrackingRepository.findById(id)
                .orElseThrow(() -> new NeederTrackingNotFoundException("NeederTracking with ID " + id + " not found"));
    }

    // Add a new NeederTracking record
    public NeederTracking addNeederTracking(NeederTracking neederTracking) {
        return neederTrackingRepository.save(neederTracking); // Save the new entity
    }

    // Update an existing NeederTracking record
    public NeederTracking updateNeederTrack(Long id, NeederTracking updatedNeeder) {
        NeederTracking existingNeederTracking = neederTrackingRepository.findById(id)
                .orElseThrow(() -> new NeederTrackingNotFoundException("NeederTracking with ID " + id + " not found"));

        // Update fields
        existingNeederTracking.setNeedy(updatedNeeder.getNeedy());
        existingNeederTracking.setWeekStatus(updatedNeeder.getWeekStatus());
        existingNeederTracking.setDietaryPreferences(updatedNeeder.getDietaryPreferences());
        existingNeederTracking.setAdditionalNotes(updatedNeeder.getAdditionalNotes());

        return neederTrackingRepository.save(existingNeederTracking);
    }

    // Delete a NeederTracking record
    public void deleteNeederTrack(Long id) {
        if (!neederTrackingRepository.existsById(id)) {
            throw new NeederTrackingNotFoundException("NeederTracking with ID " + id + " not found");
        }
        neederTrackingRepository.deleteById(id);
    }


    public List<NeederTrackingProjection> getNeedersHere() {
        return neederTrackingRepository.findByWeekStatus(WeekStatus.Here);
    }

    public NeedySimpleDTO getNeedyFromNeederTrackingId(Long id) {
        NeederTracking neederTracking = getNeederTrackById(id);
        return new NeedySimpleDTO(neederTracking.getNeedy(), neederTracking.getAdditionalNotes());
    }

}