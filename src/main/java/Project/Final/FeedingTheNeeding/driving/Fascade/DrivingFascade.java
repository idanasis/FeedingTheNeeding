package Project.Final.FeedingTheNeeding.driving.Fascade;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

import Project.Final.FeedingTheNeeding.driving.Repository.DriverConstraintsRepository;
import Project.Final.FeedingTheNeeding.driving.Repository.RouteRepository;
import Project.Final.FeedingTheNeeding.driving.exception.DriverConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.driving.exception.RouteNotFoundException;
import Project.Final.FeedingTheNeeding.driving.exception.VisitNotExistException;
import jakarta.transaction.Transactional;
import Project.Final.FeedingTheNeeding.driving.Model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class DrivingFascade {
    
    private final DriverConstraintsRepository driverConstraintsRepository;

    private final RouteRepository routeRepository;
    private static final Logger logger = LogManager.getLogger(DrivingFascade.class);

    public DrivingFascade(DriverConstraintsRepository driverConstraintsRepository, RouteRepository routeRepository) {
        logger.info("DrivingFascade create");
        this.driverConstraintsRepository = driverConstraintsRepository;
        this.routeRepository = routeRepository;
        logger.info("DrivingFascade created");
    }

    public DriverConstraint submitConstraint(DriverConstraint constraint){
        logger.info("submitConstraint of driver {} to date {}", constraint.getDriverId(), constraint.getDate());
        return driverConstraintsRepository.save(constraint);
    }
    public void removeConstraint(DriverConstraintId constraint){
        logger.info("removeConstraint of driver {} to date {}", constraint.getDriverId(), constraint.getDate());
        Long id = constraint.getDriverId();
        LocalDate date = constraint.getDate();
        DriverConstraint driverConstraint = driverConstraintsRepository.findByDriverIdAndDate(id, date).orElseThrow(() -> new DriverConstraintsNotExistException(date));
        driverConstraintsRepository.delete(driverConstraint);
        logger.info("removeConstraint of driver {} to date {} done", constraint.getDriverId(), constraint.getDate());
    }
    public List<DriverConstraint> getDateConstraints(LocalDate date){
        logger.info("getDateConstraints of date {}", date);
        return driverConstraintsRepository.findConstraintsByDate(date);
    }
    public List<DriverConstraint> getDriverConstraints(long driverId){
        logger.info("getDriverConstraints of driver id {}", driverId);
        return driverConstraintsRepository.findConstraintsByDriverId(driverId);
    }
    public void submitRouteForDriver(long routeId) {
        logger.info("submitRouteForDriver with route id={}", routeId);
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
        route.setSubmitted(true);
        routeRepository.save(route);
        logger.info("submitRouteForDriver with route id={} done", routeId);
    }
    public Route createRoute(LocalDate date){
        logger.info("createRoute");
        Route route = new Route(date);
        routeRepository.save(route);
        logger.info("createRoute done");
        return route;
    }
    public Route createRoute(long driverId, LocalDate date){
        logger.info("createRoute with driver id={} and date={}", driverId, date);
        Route route = new Route(driverId, date);
        routeRepository.save(route);
        logger.info("createRoute with driver id={} and date={} done", driverId, date);
        return route;
    }
    @Transactional
    public void setDriverIdToRoute(long routeId, long driverId){
        logger.info("setDriverIdToRoute with route id={} and driver id {}", routeId, driverId);
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
        driverConstraintsRepository.findByDriverIdAndDate(driverId, route.getDate()).orElseThrow(() -> new DriverConstraintsNotExistException(route.getDate()));
        route.setDriverId(driverId);
        routeRepository.save(route);
        logger.info("setDriverIdToRoute with route id={} and driver id {} done", routeId, driverId);
    }

    public void removeRoute(long routeId){
        logger.info("removeRoute with route id={}", routeId);
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
        routeRepository.delete(route);
        logger.info("removeRoute with route id={} done", routeId);
    }
    @Transactional
    public void submitAllRoutes(LocalDate date){
        logger.info("submitAllRoutes with date {}", date);
        List<Route> routes = routeRepository.findRoutesByDate(date);
        for(Route route : routes){
            route.setSubmitted(true);
        }
        routeRepository.saveAll(routes);
        logger.info("submitAllRoutes with date {} done", date);
    }

    public void addAddressToRoute(long routeId,long address,VisitStatus status){ 
        logger.info("addAddressToRoute with route id={} and address {} and status", routeId, address,status);
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
        if(status==VisitStatus.Deliver){
            //TODO: get address from cooking service
        }
        else if(status==VisitStatus.Pickup){
            //TODO: get address from social service
        }
        routeRepository.save(route);
        logger.info("addAddressToRoute with route id={} and address {} and status={} done", routeId, address,status);
    }
    public void removeAddressFromRoute(long routeId,long visitId){
        logger.info("removeAddressFromRoute with route id={} and visitId= {}", routeId, visitId);
       Route route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
       Visit visitToRemove = route.getVisit().stream().filter(visit -> visit.getVisitId() == visitId).findFirst().orElseThrow(()->new VisitNotExistException(routeId,visitId));
       route.removeVisit(visitToRemove);
       routeRepository.save(route);
       logger.info("removeAddressFromRoute with route id={} and visitId={} done", routeId, visitId);
    }
    public Route getRoute(long routeId){
        logger.info("getRoute with route id={}", routeId);
        return routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
    }
    public Route getRoute(LocalDate date, long driverId){
        logger.info("getRoute with date {} and driver id {}", date, driverId);
        return routeRepository.findRouteByDateAndDriverId(date, driverId).orElse(null);
    }
    public List<Route> viewHistory(){
       logger.info("viewHistory");
       return routeRepository.findAll();
    }
}
