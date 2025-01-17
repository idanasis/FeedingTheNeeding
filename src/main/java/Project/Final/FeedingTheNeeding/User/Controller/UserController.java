package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.Authentication.Model.UserCredentials;
import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
            UserCredentials currentUser = (UserCredentials) authentication.getPrincipal();
            Donor donor = currentUser.getDonor();
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
    public ResponseEntity<?> getAllDonors() {
        try {
            List<Donor> donors = userService.getAllDonors();
            return ResponseEntity.ok(donors);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/needy")
    public ResponseEntity<?> getAllNeedyUsers() {
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

    @GetMapping("/donor/{phoneNumber}")
    public ResponseEntity<?> getDonorByPhoneNumber(@PathVariable String phoneNumber) {
        try{
            Donor donor = userService.getDonorByPhoneNumber(phoneNumber);
            return ResponseEntity.ok(donor);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/donor/donorId/{donorId}")
    public ResponseEntity<?> getDonorById(@PathVariable long donorId) {
       try{
           Donor donor = userService.getDonorById(donorId);
           return ResponseEntity.ok(donor);
    }catch (Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
    @GetMapping("/donor/pending")
    public ResponseEntity<?> getDonorPending() {
        try{
            List<Donor> donors = userService.getDonorsPending();
            return ResponseEntity.ok(donors);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
  
    @PutMapping("/donor")
    public ResponseEntity<?> updateDonor(@RequestBody Donor entity) {
        try{
            userService.updateDonor(entity);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/donor/{id}")
    public ResponseEntity<?> deleteDonor(@PathVariable Long id) {
        try{
            userService.deleteDonor(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/donors/volunteeredLastMonth")
    public ResponseEntity<?> getAllDonorsVolunteeredLastMonth() {
        try{
            List<Donor> donors = userService.getAllVolunteeredDuringTheLastMonth();
            return ResponseEntity.ok(donors);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/donor/approved")
    public ResponseEntity<?> getDonorApproved() {
        try{
            List<Donor> donors = userService.getDonorsApproved();
            return ResponseEntity.ok(donors);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}