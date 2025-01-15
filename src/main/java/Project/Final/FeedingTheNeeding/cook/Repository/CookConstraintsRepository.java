package Project.Final.FeedingTheNeeding.cook.Repository;

import Project.Final.FeedingTheNeeding.cook.DTO.Status;
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

    public Optional<CookConstraints> findByConstraintIdAndDate(long constraintId, LocalDate date);

    public List<CookConstraints> findConstraintsByDate(LocalDate date);
    public List<CookConstraints> findConstraintsByConstraintId(long constraintId);

    public List<CookConstraints> findConstraintsByCookId(long cookId);

    public List<CookConstraints> findConstraintsByDateAndStatus(LocalDate date, Status status);
}
