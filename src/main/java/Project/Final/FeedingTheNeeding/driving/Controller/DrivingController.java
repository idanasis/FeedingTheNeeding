package Project.Final.FeedingTheNeeding.driving.Controller;

import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import Project.Final.FeedingTheNeeding.driving.Model.Route;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;



@RestController
@RequestMapping("/driving")
@CrossOrigin(origins = "*",allowedHeaders = "*") // Allow cross-origin requests from any source
public class DrivingController {

    private final DrivingFascade drivingService;

    public DrivingController(DrivingFascade drivingService) {
        this.drivingService = drivingService;
    }

    @PostMapping("/constraints")
    public ResponseEntity<?> submitConstraint(@RequestBody DriverConstraint constraint) {
        try {
            return ResponseEntity.ok(drivingService.submitConstraint(constraint));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/constraints")
    public ResponseEntity<?> removeConstraint(@RequestBody DriverConstraintId constraintId) {
        try {
            drivingService.removeConstraint(constraintId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/constraints/{date}")
    public ResponseEntity<?> getDateConstraints(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return ResponseEntity.ok(drivingService.getDateConstraints(localDate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/constraints/driver/{driverId}")
    public ResponseEntity<?> getDriverConstraints(@PathVariable long driverId) {
        try {
            return ResponseEntity.ok(drivingService.getDriverConstraints(driverId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/routes/submit/{routeId}")
    public ResponseEntity<?> submitRouteForDriver(@PathVariable long routeId) {
        try {
            drivingService.submitRouteForDriver(routeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/routes/create")
    public ResponseEntity<?> createRoute(@RequestParam("date") String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return ResponseEntity.ok(drivingService.createRoute(localDate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/routes/create/driver")
    public ResponseEntity<?> createRouteWithDriver(@RequestParam Long driverId, @RequestParam LocalDate date) {
        try {
            return ResponseEntity.ok(drivingService.createRoute(driverId, date));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PutMapping("/routes/{routeId}/driver/{driverId}")
    public ResponseEntity<?> setDriverIdToRoute(@PathVariable long routeId, @PathVariable long driverId) {
        try {
            drivingService.setDriverIdToRoute(routeId, driverId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<?> removeRoute(@PathVariable long routeId) {
        try {
            drivingService.removeRoute(routeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/routes/submitAll/{date}")
    public ResponseEntity<?> submitAllRoutes(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            drivingService.submitAllRoutes(localDate);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/routes/{routeId}")
    public ResponseEntity<?> getRoute(@PathVariable long routeId) {
        try {
            return ResponseEntity.ok(drivingService.getRoute(routeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/routes")
    public ResponseEntity<?> getRouteByDateAndDriver(@RequestParam LocalDate date, @RequestParam long driverId) {
        try {
            return ResponseEntity.ok(drivingService.getRoute(date, driverId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/routes/getRoutes")
    public ResponseEntity<?> getRoutes(@RequestParam("date") String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return ResponseEntity.ok(drivingService.getRoutes(localDate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/history")
    public ResponseEntity<?> viewHistory() {
        try {
            return ResponseEntity.ok(drivingService.viewHistory());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PatchMapping("/routes/updateRoute")
    public ResponseEntity<?> updateRoute(@RequestBody Route route) {
        try {
            return ResponseEntity.ok(drivingService.updateRoute(route));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DONOR')")
    @GetMapping("/constraints/driver/futureNotApproved")
    public ResponseEntity<?> getDriverFutureConstraintsHaventConfirmed(@RequestParam String driverId) {
        try {
            return ResponseEntity.ok(drivingService.getDriverFutureConstraintsHaventConfirmed(Long.parseLong(driverId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
