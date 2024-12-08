package Project.Final.FeedingTheNeeding.User.Repository;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeedyRepository extends UserRepository<Needy> {
        //find needy by needyStatus
        List<Needy> findByConfirmStatus(NeedyStatus needyStatus);
}
