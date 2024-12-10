package Project.Final.FeedingTheNeeding.driving.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;

@Repository
public interface DriverConstraintsRepository extends JpaRepository<DriverConstraint, DriverConstraintId> {

    //@Query(value = "SELECT * FROM DriverConstraint AS d WHERE d.driverId = ?1 AND d.date = ?2", nativeQuery = true)
    public Optional<DriverConstraint> findByDriverIdAndDate(long driverId, LocalDate date);

    public List<DriverConstraint> findConstraintsByDate(LocalDate date);

    public List<DriverConstraint> findConstraintsByDriverId(long driverId);
}
