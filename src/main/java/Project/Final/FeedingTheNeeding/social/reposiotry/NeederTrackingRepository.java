package Project.Final.FeedingTheNeeding.social.reposiotry;

import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeederTrackingRepository extends JpaRepository<NeederTracking, Long> {
    // Add custom queries if necessary
}
