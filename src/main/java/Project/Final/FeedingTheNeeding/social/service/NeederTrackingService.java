package Project.Final.FeedingTheNeeding.social.service;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.social.dto.NeedySimpleDTO;
import Project.Final.FeedingTheNeeding.social.exception.NeederTrackingNotFoundException;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.model.WeekStatus;
import Project.Final.FeedingTheNeeding.social.projection.NeederTrackingProjection;
import Project.Final.FeedingTheNeeding.social.reposiotry.NeederTrackingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NeederTrackingService {

    private final NeederTrackingRepository neederTrackingRepository;
    private static final Logger logger = LoggerFactory.getLogger(NeederTrackingService.class);
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
        existingNeederTracking.setDate(updatedNeeder.getDate());

        return neederTrackingRepository.save(existingNeederTracking);
    }

    // Delete a NeederTracking record
    public void deleteNeederTrack(Long id) {
        if (!neederTrackingRepository.existsById(id)) {
            throw new NeederTrackingNotFoundException("NeederTracking with ID " + id + " not found");
        }
        neederTrackingRepository.deleteById(id);
    }


    public List<NeederTrackingProjection> getNeedersHereByDate(LocalDate date) {
        return neederTrackingRepository.findByWeekStatusAndDate(WeekStatus.Here,date);
    }

    public NeedySimpleDTO getNeedyFromNeederTrackingId(Long id) {
        NeederTracking neederTracking = getNeederTrackById(id);
        return new NeedySimpleDTO(neederTracking.getNeedy(), neederTracking.getAdditionalNotes());
    }

    public List<NeederTracking> getAllNeedersTrackingsByDate(LocalDate date) {
        return neederTrackingRepository.findByDate(date);
    }
    public void addNeederTracking(Needy needer, LocalDate date) {
        logger.info("Adding NeederTracking for Needy={} on date={}",needer.getId(),date);
        NeederTracking neederTracking = new NeederTracking();
        neederTracking.setNeedy(needer);
        neederTracking.setDate(date);
        neederTracking.setWeekStatus(WeekStatus.NotHere);
        neederTrackingRepository.save(neederTracking);
        logger.info("NeederTracking added for Needy={} on date={}",needer.getId(),date);
    }

}