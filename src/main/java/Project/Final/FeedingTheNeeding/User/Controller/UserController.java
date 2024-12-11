package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/authDonor")
    public ResponseEntity<?> authenticatedDonor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Donor donor = (Donor) authentication.getPrincipal();
            return ResponseEntity.ok(donor);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/Allusers")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<BaseUser> users = userService.getAll();
            return ResponseEntity.ok(users);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/donors")
    public ResponseEntity<?> allDonors() {
        try {
            List<Donor> donors = userService.getAllDonors();
            return ResponseEntity.ok(donors);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/needy")
    public ResponseEntity<?> allNeedy() {
        try {
            List<Needy> needyList = userService.getAllNeedyUsers();
            return ResponseEntity.ok(needyList);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/needy/{phoneNumber}")
    public ResponseEntity<?> getNeedyByPhoneNumber(@PathVariable String phoneNumber) {
        try{
            Needy needy = userService.getNeedyByPhoneNumber(phoneNumber);
            return ResponseEntity.ok(needy);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/donor/{email}")
    public ResponseEntity<?> getDonorByEmail(@PathVariable String email) {
        try{
            Donor donor = userService.getDonorByEmail(email);
            return ResponseEntity.ok(donor);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}