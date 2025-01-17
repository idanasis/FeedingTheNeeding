package Project.Final.FeedingTheNeeding.cook.Controller;

import Project.Final.FeedingTheNeeding.cook.DTO.LatestConstraintsRequestDto;
import Project.Final.FeedingTheNeeding.cook.DTO.PendingConstraintDTO;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import jakarta.validation.Constraint;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/cooking")
public class CookController {
    private final CookingService cs;

    @Autowired
    private ConstraintMapper mapper;

    public CookController(CookingService cs) {this.cs = cs;}

    @PostMapping("/submit/constraints")
    public ResponseEntity<?> submitConstraints(@RequestBody CookConstraints constraints){
        try{
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

    @GetMapping("/constraints/cook/{cookId}")
    public ResponseEntity<?> getCookConstraints(@PathVariable long cookId) {
        try {
            return ResponseEntity.ok(cs.getCookConstraints(cookId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/allConstraints/cook/{cookId}")
    public ResponseEntity<?> getCookHistory(@PathVariable long cookId){
        try{
            return ResponseEntity.ok(cs.getCookConstraints(cookId));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/constraints/latest")
    public ResponseEntity<?> getLatestConstraints(@RequestBody LatestConstraintsRequestDto dto){
        try{
            return ResponseEntity.ok(cs.getLatestCookConstraints(dto.cookId, dto.date));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("getAccepted/{date}")
    public ResponseEntity<?> getAllAcceptedConstraintsByDate(@PathVariable LocalDate date){
        try{
            return ResponseEntity.ok(cs.getAcceptedCookByDate(date));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("getPending/{date}")
    public ResponseEntity<?> getPendingConstraintsByDate(@PathVariable LocalDate date){
        try{
            List<CookConstraints> constraints = cs.getPendingConstraints(date);
            List<PendingConstraintDTO> dtos = constraints.stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("acceptConstraint/{constraintId}")
    public ResponseEntity<?> acceptConstraintStatus(@PathVariable long constraintId){
        try{
            return ResponseEntity.ok(cs.changeStatusForConstraint(constraintId, Status.Accepted));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("rejectConstraint/{constraintId}")
    public ResponseEntity<?> rejectConstraintStatus(@PathVariable long constraintId){
        try{
            return ResponseEntity.ok(cs.changeStatusForConstraint(constraintId, Status.Declined));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
