package Project.Final.FeedingTheNeeding.cook.Controller;

import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Service.CookingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/cooking")
public class CookController {
//    private final CookingService cs;
//
//    public CookController(CookingService cs) {this.cs = cs;}
//
//    @PostMapping("/constraints")
//    public ResponseEntity<?> submitConstraints(@RequestBody CookConstraints constraints){
//        try{
//            return ResponseEntity.ok(cs.submitConstraints(constraints));
//        } catch (Exception e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
}
