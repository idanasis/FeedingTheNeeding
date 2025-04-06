package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.Authentication.DTO.NeedyRegistrationRequest;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Model.Street;
import Project.Final.FeedingTheNeeding.User.Service.NeedyService;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*") // Allow cross-origin requests from any source
@RequestMapping("/needer")
public class NeederController {

    @Autowired
    NeedyService needyService;

    public NeederController(NeedyService needyService) {
        this.needyService = needyService;
    }
    // Create or Update a Needy user
    @PostMapping
    public ResponseEntity<Needy> createOrUpdateNeedy(@RequestBody NeedyRegistrationRequest needyRegistrationRequest) {
        try {
            Needy needy =new Needy();
            needy.setFirstName(needyRegistrationRequest.getFirstName());
            needy.setLastName(needyRegistrationRequest.getLastName());
            needy.setPhoneNumber(needyRegistrationRequest.getPhoneNumber());
            needy.setAddress(needyRegistrationRequest.getAddress());
            needy.setStreet(needyRegistrationRequest.getStreet());
            needy.setConfirmStatus(NeedyStatus.PENDING);
            needy.setFamilySize(needyRegistrationRequest.getFamilySize());

            Needy savedNeedy = needyService.saveOrUpdateNeedy(needy);
            return ResponseEntity.ok(savedNeedy);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Needy> updateNeedy(@PathVariable Long id, @RequestBody Needy needy) {
        try {
            Optional<Needy> needyOptional = needyService.getNeedyById(id);
            if (needyOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            needy.setId(id);
            Needy updatedNeedy = needyService.saveOrUpdateNeedy(needy);
            return ResponseEntity.ok(updatedNeedy);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Needy> acceptNeedy(@PathVariable Long id) {
        try {
            Optional<Needy> needy = needyService.getNeedyById(id);
            if (needy.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            needy.get().setConfirmStatus(NeedyStatus.APPROVED);
            needyService.saveOrUpdateNeedy(needy.get());
            return ResponseEntity.ok(needy.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Get all Needy users
    @GetMapping
    public ResponseEntity<List<Needy>> getAllNeedies() {
        try {
            List<Needy> needyUsers = needyService.getAllNeedyUsers();
            return ResponseEntity.ok(needyUsers);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Get a specific Needy user by ID
    @GetMapping("/{id}")
    public ResponseEntity<Needy> getNeedyById(@PathVariable Long id) {
        try {
            return needyService.getNeedyById(id)
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
            needyService.deleteNeedyById(id);
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
            List<Needy> pendingNeedies = needyService.getPendingNeedy();
            return ResponseEntity.ok(pendingNeedies);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/accepted")
    public ResponseEntity<List<Needy>> getAcceptedNeedies() {
        try {
            List<Needy> acceptedNeedies = needyService.getApprovedNeedy();
            return ResponseEntity.ok(acceptedNeedies);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }    
    
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<?> getNeedyByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            return needyService.getNeedyByPhoneNumber(phoneNumber)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/needy-tracking")
    public ResponseEntity<?> getNeedyUsersTrackingByData(@RequestParam("date") String dateStr) {
        try {
            LocalDate localDate = LocalDate.parse(dateStr);  // Parse the date from the request parameter

            List<NeederTracking> trackingList = needyService.getNeedyUsersTrackingByData(localDate);

            return ResponseEntity.ok(trackingList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


}
