package Project.Final.FeedingTheNeeding.User.Repository;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<BaseUser, Long> {

    List<BaseUser> findByCity(String city);

}