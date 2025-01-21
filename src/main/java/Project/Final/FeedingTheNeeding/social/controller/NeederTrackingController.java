package Project.Final.FeedingTheNeeding.social.controller;

import Project.Final.FeedingTheNeeding.social.dto.NeedySimpleDTO;
import Project.Final.FeedingTheNeeding.social.exception.NeederTrackingNotFoundException;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.projection.NeederTrackingProjection;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/social")
public class NeederTrackingController {

    private final NeederTrackingService neederTrackingService;
    private static final Logger logger = LoggerFactory.getLogger(NeederTrackingController.class);


    public NeederTrackingController(NeederTrackingService neederTrackingService) {
        this.neederTrackingService = neederTrackingService;
    }

    // Get all NeederTracking records
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Add the appropriate roles
    @GetMapping
    public ResponseEntity<?> getAllNeederTrackings() {
        try {
            logger.info("Fetching all NeederTracking records");
            List<NeederTracking> neederTrackings = neederTrackingService.getAllNeedersTrackings();
            logger.info("Fetched all NeederTracking records");
            return ResponseEntity.ok(neederTrackings);
        } catch (Exception e) {
            logger.error("Failed to fetch all NeederTracking records", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get a specific NeederTracking by ID
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Add the appropriate roles
    @GetMapping("/{id}")
    public ResponseEntity<?> getNeederTrackingById(@PathVariable Long id) {
        try {
            logger.info("Fetching NeederTracking record with ID: {}", id);
            NeederTracking neederTracking = neederTrackingService.getNeederTrackById(id);
            logger.info("NeederTracking record found with ID: {}", id);
            return ResponseEntity.ok(neederTracking);
        } catch (NeederTrackingNotFoundException e) {
            logger.error("NeederTracking record with ID: {} not found", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to fetch NeederTracking record with ID: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Add a new NeederTracking record
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Add the appropriate roles
    @PostMapping
    public ResponseEntity<?> addNeederTracking(@RequestBody NeederTracking neederTracking) {
        try {
            logger.info("Adding new NeederTracking record");
            NeederTracking savedNeederTracking = neederTrackingService.addNeederTracking(neederTracking);
            logger.info("New NeederTracking record added with ID: {}", savedNeederTracking.getId());
            return ResponseEntity.status(201).body(savedNeederTracking);
        } catch (Exception e) {
            logger.error("Failed to add new NeederTracking record", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update an existing NeederTracking record by ID
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Add the appropriate roles
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNeederTracking(
            @PathVariable Long id,
            @RequestBody NeederTracking updatedNeederTracking) {
        try {
            logger.info("Updating NeederTracking record with ID: {}", id);
            NeederTracking updatedNeeder = neederTrackingService.updateNeederTrack(id, updatedNeederTracking);
            logger.info("NeederTracking record updated with ID: {}", id);
            return ResponseEntity.ok(updatedNeeder);
        } catch (Exception e) {
            logger.error("Failed to update NeederTracking record with ID: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete a specific NeederTracking record by ID
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Add the appropriate roles
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNeederTracking(@PathVariable Long id) {
        try {
            logger.info("Deleting NeederTracking record with ID: {}", id);
            neederTrackingService.deleteNeederTrack(id);
            logger.info("NeederTracking record deleted with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Failed to delete NeederTracking record with ID: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Add the appropriate roles
    @GetMapping("/getNeedersHere")
    public ResponseEntity<?> getNeedersHere(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            logger.info("Fetching all NeederTracking records with weekStatus HERE for date: {}", date);
            List<NeederTrackingProjection> neederTrackings = neederTrackingService.getNeedersHereByDate(date);
            logger.info("Fetched all NeederTracking records with weekStatus HERE for date: {}", date);
            return ResponseEntity.ok(neederTrackings);
        } catch (Exception e) {
            logger.error("Failed to fetch NeederTracking records with weekStatus HERE for date: {}", date, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Add the appropriate roles
    @GetMapping("/getNeedy/{id}")
    public ResponseEntity<?> getNeedyFromNeederTrackingId(@PathVariable Long id) {
        try {
            logger.info("Fetching Needy from NeederTracking record with ID: {}", id);
            NeederTracking neederTracking = neederTrackingService.getNeederTrackById(id);
            logger.info("Needy found from NeederTracking record with ID: {}", id);
            return ResponseEntity.ok(new NeedySimpleDTO(neederTracking.getNeedy(),neederTracking.getAdditionalNotes()));
        } catch (NeederTrackingNotFoundException e) {
            logger.error("Needy not found from NeederTracking record with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to fetch Needy from NeederTracking record with ID: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Add the appropriate roles
    @GetMapping("/getNeedersByDate")
    public ResponseEntity<?> getAllNeederTrackings(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            logger.info("Fetching all NeederTracking records for date: {}", date);
            List<NeederTracking> neederTrackings = neederTrackingService.getAllNeedersTrackingsByDate(date);
            logger.info("Fetched all NeederTracking records for date: {}", date);
            return ResponseEntity.ok(neederTrackings);
        } catch (Exception e) {
            logger.error("Failed to fetch all NeederTracking records for date: {}", date, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('DONOR', 'ADMIN', 'STAFF')") // Add the appropriate roles
    @GetMapping("/getNeededFoodByDate")
    public ResponseEntity<?> getAllNeededFoodByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            logger.info("Fetching all NeederTracking records for date: {}", date);
            List<NeederTracking> neederTrackings = neederTrackingService.getAllNeedersTrackingsByDate(date);
            logger.info("Fetched all NeederTracking records for date: {}", date);

//            Map<String, Long> dietaryPreferenceCounts = neederTrackings.stream()
//                    .map(NeederTracking::getDietaryPreferences)
//                    .collect(Collectors.groupingBy(
//                            preference -> preference,
//                            Collectors.counting()
//                    ));
            Map<String, Long> dietaryPreferenceCounts = neederTrackings.stream()
                    .collect(Collectors.groupingBy(
                            NeederTracking::getDietaryPreferences,
                            Collectors.summingLong(tracking -> tracking.getNeedy().getFamilySize())
                    ));

            return ResponseEntity.ok(dietaryPreferenceCounts);
        } catch (Exception e) {
            logger.error("Failed to fetch all NeederTracking records for date: {}", date, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}