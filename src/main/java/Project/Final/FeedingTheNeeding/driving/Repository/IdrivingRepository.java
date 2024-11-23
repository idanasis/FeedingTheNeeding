package Project.Final.FeedingTheNeeding.driving.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.Route;

@Repository
public interface IdrivingRepository {
    
    public void save(DriverConstraint constraint);
    public void delete(DriverConstraint constraint);
    public void save(Route route);
    public void delete(Route route);
    public String getConstraintsByDate(LocalDate date);
    public Route getRoute(String routeId);
    public Route getRoute(LocalDate date, String driverId);
    public List<Route> getRoutesByDate(LocalDate date);
    public List<Route> getAllRoutes();

}
