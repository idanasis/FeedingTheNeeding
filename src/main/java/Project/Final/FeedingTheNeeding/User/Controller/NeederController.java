package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Repository.NeederRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/needer")
public class NeederController {

    @Autowired
    NeederRepository neederRepository;

    public NeederController(NeederRepository neederRepository) {
        this.neederRepository = neederRepository;
    }

    //get all needers with needy status pending enum
    @GetMapping("/pending")
    public void getPendingNeedy() {
        neederRepository.findByConfirmStatus(NeedyStatus.PENDING);
    }

}
