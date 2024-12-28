package Project.Final.FeedingTheNeeding.cook.Controller;

import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;


@RestController
@RequestMapping("/cooking")
public class CookController {
    private final CookingService cs;

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

    @PutMapping("/constraints/update/{cookId}")
    public ResponseEntity<?> updateCookConstraints(@PathVariable long cookId, @RequestBody CookConstraints newConstraints){
        try{
            cs.updateCookConstraints(cookId, newConstraints);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cook/{cookId}")
    public ResponseEntity<?> getCookHistory(@PathVariable long cookId){
        try{
            return ResponseEntity.ok(cs.getCookHistory(cookId));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("cook/getDate/{date}")
    public ResponseEntity<?> getAllConstraintsByDate(@PathVariable LocalDate date){
        try{
            return ResponseEntity.ok(cs.getAllCookOnDate(date));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
