package Project.Final.FeedingTheNeeding.social.controller;

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
        List<NeederTracking> neederTrackings = neederTrackingService.getAllNeedersTrackings();
        return ResponseEntity.ok(neederTrackings);
    }

    // Get a specific NeederTracking by ID
    @GetMapping("/{id}")
    public ResponseEntity<NeederTracking> getNeederTrackingById(@PathVariable Long id) {
        NeederTracking neederTracking = neederTrackingService.getNeederTrackById(id);
        return ResponseEntity.ok(neederTracking);
    }

    // Add a new NeederTracking record
    @PostMapping
    public ResponseEntity<NeederTracking> addNeederTracking(@RequestBody NeederTracking neederTracking) {
        NeederTracking savedNeederTracking = neederTrackingService.addNeederTracking(neederTracking);
        return ResponseEntity.status(201).body(savedNeederTracking);
    }

    // Update an existing NeederTracking record by ID
    @PutMapping("/{id}")
    public ResponseEntity<NeederTracking> updateNeederTracking(
            @PathVariable Long id,
            @RequestBody NeederTracking updatedNeederTracking) {
        NeederTracking updatedNeeder = neederTrackingService.updateNeederTrack(id, updatedNeederTracking);
        return ResponseEntity.ok(updatedNeeder);
    }

    // Delete a specific NeederTracking record by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNeederTracking(@PathVariable Long id) {
        neederTrackingService.deleteNeederTrack(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping
//    public ResponseEntity<List<NeederTracking>> getAllWaitingNeeders() {
//        List<NeederTracking> neederTrackings = neederTrackingService.getAllWaitingNeeders();
//        return ResponseEntity.ok(neederTrackings);
//    }
}