package Project.Final.FeedingTheNeeding.driving.Controller;

import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import Project.Final.FeedingTheNeeding.user.Model.NeederContactDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/driving")
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

    @GetMapping("/constraints/date/{date}")
    public ResponseEntity<?> getDateConstraints(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return ResponseEntity.ok(drivingService.getDateConstraints(localDate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/constraints/driver/{driverId}")
    public ResponseEntity<?> getDriverConstraints(@PathVariable long driverId) {
        try {
            return ResponseEntity.ok(drivingService.getDriverConstraints(driverId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/routes/submit/{routeId}")
    public ResponseEntity<?> submitRouteForDriver(@PathVariable long routeId) {
        try {
            drivingService.submitRouteForDriver(routeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/routes/create")
    public ResponseEntity<?> createRoute(@RequestParam LocalDate date) {
        try {
            return ResponseEntity.ok(drivingService.createRoute(date));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/routes/create/driver")
    public ResponseEntity<?> createRouteWithDriver(@RequestParam String driverId, @RequestParam LocalDate date, @RequestParam int startHour) {
        try {
            return ResponseEntity.ok(drivingService.createRoute(driverId, date, startHour));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/routes/{routeId}/driver/{driverId}")
    public ResponseEntity<?> setDriverIdToRoute(@PathVariable long routeId, @PathVariable String driverId) {
        try {
            drivingService.setDriverIdToRoute(routeId, driverId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/routes/{routeId}/startHour")
    public ResponseEntity<?> setStartHourToRoute(@PathVariable long routeId, @RequestParam int startHour) {
        try {
            drivingService.setStartHourToRoute(routeId, startHour);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<?> removeRoute(@PathVariable long routeId) {
        try {
            drivingService.removeRoute(routeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @PostMapping("/routes/{routeId}/address")
    public ResponseEntity<?> addAddressToRoute(@PathVariable long routeId, @RequestBody NeederContactDTO address) {
        try {
            drivingService.addAddressToRoute(routeId, address);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/routes/{routeId}/address")
    public ResponseEntity<?> removeAddressFromRoute(@PathVariable long routeId, @RequestBody NeederContactDTO address) {
        try {
            drivingService.removeAddressFromRoute(routeId, address);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/routes/{routeId}")
    public ResponseEntity<?> getRoute(@PathVariable long routeId) {
        try {
            return ResponseEntity.ok(drivingService.getRoute(routeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/routes")
    public ResponseEntity<?> getRouteByDateAndDriver(@RequestParam LocalDate date, @RequestParam String driverId) {
        try {
            return ResponseEntity.ok(drivingService.getRoute(date, driverId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> viewHistory() {
        try {
            return ResponseEntity.ok(drivingService.viewHistory());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
