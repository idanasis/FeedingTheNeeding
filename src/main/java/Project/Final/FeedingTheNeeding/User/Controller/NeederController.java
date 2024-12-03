package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Repository.NeederRepository;
import Project.Final.FeedingTheNeeding.User.Service.NeederService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/needer")
public class NeederController {

    @Autowired
    NeederService neederService;

    public NeederController(NeederService neederService) {
        this.neederService = neederService;
    }
    // Create or Update a Needy user
    @PostMapping
    public ResponseEntity<Needy> createOrUpdateNeedy(@RequestBody Needy needy) {
        try {
            Needy savedNeedy = neederService.saveOrUpdateNeedy(needy);
            return ResponseEntity.ok(savedNeedy);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Get all Needy users
    @GetMapping
    public ResponseEntity<List<Needy>> getAllNeedies() {
        try {
            List<Needy> needyUsers = neederService.getAllNeedyUsers();
            return ResponseEntity.ok(needyUsers);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Get a specific Needy user by ID
    @GetMapping("/{id}")
    public ResponseEntity<Needy> getNeedyById(@PathVariable Long id) {
        try {
            return neederService.getNeedyById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Delete a Needy user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNeedyById(@PathVariable Long id) {
        try {
            neederService.deleteNeedyById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Needy>> getPendingNeedies() {
        try {
            List<Needy> pendingNeedies = neederService.getPendingNeedy();
            return ResponseEntity.ok(pendingNeedies);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Needy> getNeedyByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            return neederService.getNeedyByPhoneNumber(phoneNumber)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


}
