package Project.Final.FeedingTheNeeding.User.Service;

import Project.Final.FeedingTheNeeding.User.Controller.NeederController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NeederService {

    @Autowired
    NeederController neederController;

    public NeederService(NeederController neederController) {
        this.neederController = neederController;
    }

    public void getPendingNeedy() {
        neederController.getPendingNeedy();
    }


}
