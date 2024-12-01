package Project.Final.FeedingTheNeeding.User.Repository;

import Project.Final.FeedingTheNeeding.User.Model.Donator;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonatorRepository extends JpaRepository<Donator, Long> {

}
