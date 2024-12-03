package Project.Final.FeedingTheNeeding.cook.Repository;

import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CookConstraintsRepository extends JpaRepository<CookConstraints,Long> {

    public Optional<DriverConstraint> findByCookIdAndDate(long driverId, LocalDate date);

    public List<DriverConstraint> findConstraintsByDate(LocalDate date);

    public List<DriverConstraint> findConstraintsByDriverId(long driverId);
}
