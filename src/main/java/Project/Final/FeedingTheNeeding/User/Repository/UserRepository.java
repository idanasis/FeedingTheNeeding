package Project.Final.FeedingTheNeeding.User.Repository;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BaseUser, Long> {

    Optional<BaseUser> findByPhoneNumber(String phoneNumber);
}