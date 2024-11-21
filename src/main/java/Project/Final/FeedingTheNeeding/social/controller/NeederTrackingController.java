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

    @GetMapping
    public List<NeederTracking> getAllNeeders() {
        return neederTrackingService.getAllNeeders();
    }

    @GetMapping("/{id}")
    public NeederTracking getNeederById(@PathVariable Long id) {
        return neederTrackingService.getNeederById(id);
    }

    @PostMapping
    public NeederTracking addNeeder(@RequestBody NeederTracking neederTracking) {
        return neederTrackingService.addNeeder(neederTracking);
    }

    @PutMapping("/{id}")
    public NeederTracking updateNeeder(@PathVariable Long id, @RequestBody NeederTracking updatedNeeder) {
        return neederTrackingService.updateNeeder(id, updatedNeeder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNeeder(@PathVariable Long id) {
        neederTrackingService.deleteNeeder(id);
        return ResponseEntity.ok().build();
    }
}
