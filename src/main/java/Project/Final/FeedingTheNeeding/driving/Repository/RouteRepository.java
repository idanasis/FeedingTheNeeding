package Project.Final.FeedingTheNeeding.driving.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Project.Final.FeedingTheNeeding.driving.Model.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findRoutesByDate(LocalDate date);
    Optional<Route> findRouteByDateAndDriverId(LocalDate date, long driverId);
       
}
