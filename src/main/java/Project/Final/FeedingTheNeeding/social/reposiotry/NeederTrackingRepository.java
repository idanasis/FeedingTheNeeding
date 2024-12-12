package Project.Final.FeedingTheNeeding.social.reposiotry;

import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.model.WeekStatus;
import Project.Final.FeedingTheNeeding.social.projection.NeederTrackingProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;

@Repository
public interface NeederTrackingRepository extends JpaRepository<NeederTracking, Long> {


    List<NeederTrackingProjection> findByWeekStatusAndDate(WeekStatus weekStatus, LocalDate date);

    // Add custom queries if necessary
    @EntityGraph(attributePaths = {"needy"}) // Always include the Needy entity
    List<NeederTracking> findAll();

    @EntityGraph(attributePaths = {"needy"}) // Include Needy when finding by ID
    Optional<NeederTracking> findById(Long id);


}
