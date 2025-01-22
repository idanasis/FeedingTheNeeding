package Project.Final.FeedingTheNeeding.cook.Controller;

import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.cook.DTO.LatestConstraintsRequestDto;
import Project.Final.FeedingTheNeeding.cook.DTO.PendingConstraintDTO;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import jakarta.validation.Constraint;
import jakarta.websocket.server.PathParam;
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import Project.Final.FeedingTheNeeding.Authentication.Controller.AuthController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/cooking")
public class CookController {
    private final CookingService cs;
    private RestTemplate restTemplate;

    @Autowired
    private ConstraintMapper mapper;

    public CookController(CookingService cs) {this.cs = cs;}


    @PostMapping("/submit/constraints")
    public ResponseEntity<?> submitConstraints( @RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody CookConstraints constraints){
        try{
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authentication token");
            }

            Donor temp = cs.getDonorFromJwt(authorizationHeader);
            String address = temp.getAddress();
            long id = temp.getId();

            // Set the userId in the constraints object
            constraints.setCookId(id);
            constraints.setLocation(address);

            return ResponseEntity.ok(cs.submitConstraints(constraints));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/constraints")
    public ResponseEntity<?> removeConstraint(@RequestBody CookConstraints constraint) {
        try {
            cs.removeConstraint(constraint);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("updateConstraint")
    public ResponseEntity<?> updateConstraint(@RequestParam long constraintId, @RequestParam Map<String, Integer> constraint){
        try{
            cs.updateConstraint(constraintId, constraint);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/constraints/cook/{cookId}")
    public ResponseEntity<?> getCookConstraints(@PathVariable long cookId) {
        try {
            return ResponseEntity.ok(cs.getCookConstraints(cookId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/allConstraints/cook/{cookId}")
    public ResponseEntity<?> getCookHistory(@PathVariable long cookId){
        try{
            return ResponseEntity.ok(cs.getCookConstraints(cookId));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DONOR')")
    @PostMapping("/constraints/latest")
    public ResponseEntity<?> getLatestConstraints(@RequestHeader("Authorization") String authorizationHeader,
                                                  @RequestBody LatestConstraintsRequestDto request){
        try{
            LocalDate currDate = LocalDate.parse(request.date);
            long id = cs.getDonorFromJwt(authorizationHeader).getId();

            return ResponseEntity.ok(cs.getLatestCookConstraints(id, currDate));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getAccepted/{date}")
    public ResponseEntity<?> getAllAcceptedConstraintsByDate(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable LocalDate date){
        try{
            List<CookConstraints> constraints = cs.getAcceptedCookByDate(date);

            Donor donor = cs.getDonorFromJwt(authorizationHeader);
            String name = donor.getFirstName() + " " + donor.getLastName();
            String phoneNumber = donor.getPhoneNumber();


            List<PendingConstraintDTO> dtos = constraints.stream()
                    .map(constraint -> mapper.toDTO(constraint, cs.getDonorFromId( constraint.getCookId()).getFirstName() +" " +cs.getDonorFromId( constraint.getCookId()).getLastName(), cs.getDonorFromId( constraint.getCookId()).getPhoneNumber()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getPending/{date}")
    public ResponseEntity<?> getPendingConstraintsByDate(@RequestHeader("Authorization") String authorizationHeader,
                                                         @PathVariable LocalDate date){
        try{
            List<CookConstraints> constraints = cs.getPendingConstraints(date);

            Donor donor = cs.getDonorFromJwt(authorizationHeader);
            String name = donor.getFirstName() + " " + donor.getLastName();
            String phoneNumber = donor.getPhoneNumber();

            List<PendingConstraintDTO> dtos = constraints.stream()
                    .map(constraint -> mapper.toDTO(constraint, name, phoneNumber))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getConstraints/{date}")
    public ResponseEntity<?> getConstraintsByDate(@RequestHeader("Authorization") String authorizationHeader,
                                                  @PathVariable LocalDate date){
        try{
            List<CookConstraints> constraints = cs.getConstraintsByDate(date);

            List<PendingConstraintDTO> dtos = constraints.stream()
                    .map(constraint -> mapper.toDTO(constraint, cs.getDonorFromId(constraint.getCookId()).getFirstName() + " " + cs.getDonorFromId(constraint.getCookId()).getLastName(),
                            cs.getDonorFromId(constraint.getCookId()).getPhoneNumber()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/acceptConstraint/{constraintId}")
    public ResponseEntity<?> acceptConstraintStatus(@PathVariable long constraintId){
        try{
            return ResponseEntity.ok(cs.changeStatusForConstraint(constraintId, Status.Accepted));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/rejectConstraint/{constraintId}")
    public ResponseEntity<?> rejectConstraintStatus(@PathVariable long constraintId){
        try{
            return ResponseEntity.ok(cs.changeStatusForConstraint(constraintId, Status.Declined));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/undoConstraint/{constraintId}")
    public ResponseEntity<?> undoConstraint(@PathVariable long constraintId) {
        try{
            return ResponseEntity.ok(cs.changeStatusForConstraint(constraintId, Status.Pending));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
