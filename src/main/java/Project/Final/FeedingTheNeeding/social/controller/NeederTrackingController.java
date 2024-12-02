package Project.Final.FeedingTheNeeding.social.controller;

import Project.Final.FeedingTheNeeding.social.exception.NeederTrackingNotFoundException;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/social")
public class NeederTrackingController {

    private final NeederTrackingService neederTrackingService;

    public NeederTrackingController(NeederTrackingService neederTrackingService) {
        this.neederTrackingService = neederTrackingService;
    }

    // Get all NeederTracking records
    @GetMapping
    public ResponseEntity<List<NeederTracking>> getAllNeederTrackings() {
        try {
            List<NeederTracking> neederTrackings = neederTrackingService.getAllNeedersTrackings();
            return ResponseEntity.ok(neederTrackings);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Get a specific NeederTracking by ID
    @GetMapping("/{id}")
    public ResponseEntity<NeederTracking> getNeederTrackingById(@PathVariable Long id) {
        try {
            NeederTracking neederTracking = neederTrackingService.getNeederTrackById(id);
            return ResponseEntity.ok(neederTracking);
        } catch (NeederTrackingNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Add a new NeederTracking record
    @PostMapping
    public ResponseEntity<NeederTracking> addNeederTracking(@RequestBody NeederTracking neederTracking) {
        try {
            NeederTracking savedNeederTracking = neederTrackingService.addNeederTracking(neederTracking);
            return ResponseEntity.status(201).body(savedNeederTracking);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Update an existing NeederTracking record by ID
    @PutMapping("/{id}")
    public ResponseEntity<NeederTracking> updateNeederTracking(
            @PathVariable Long id,
            @RequestBody NeederTracking updatedNeederTracking) {
        try {
            NeederTracking updatedNeeder = neederTrackingService.updateNeederTrack(id, updatedNeederTracking);
            return ResponseEntity.ok(updatedNeeder);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    // Delete a specific NeederTracking record by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNeederTracking(@PathVariable Long id) {
        try {
            neederTrackingService.deleteNeederTrack(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


}