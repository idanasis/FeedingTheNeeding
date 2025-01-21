package Project.Final.FeedingTheNeeding.driving.Fascade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Project.Final.FeedingTheNeeding.driving.Repository.DriverConstraintsRepository;
import Project.Final.FeedingTheNeeding.driving.Repository.RouteRepository;
import Project.Final.FeedingTheNeeding.driving.exception.DriverConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.driving.exception.RouteNotFoundException;
import Project.Final.FeedingTheNeeding.driving.exception.VisitNotExistException;
import jakarta.transaction.Transactional;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Service.UserService;
import Project.Final.FeedingTheNeeding.driving.Model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class DrivingFascade {
    
    private final DriverConstraintsRepository driverConstraintsRepository;

    private final RouteRepository routeRepository;
    private final UserService userService;
    private static final Logger logger = LogManager.getLogger(DrivingFascade.class);

    public DrivingFascade(DriverConstraintsRepository driverConstraintsRepository, RouteRepository routeRepository,UserService userService) {
        logger.info("DrivingFascade create");
        this.driverConstraintsRepository = driverConstraintsRepository;
        this.routeRepository = routeRepository;
        this.userService = userService;
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
    public List<DriverConstraintsDTO> getDateConstraints(LocalDate date){
        logger.info("getDateConstraints of date {}", date);
        List<DriverConstraint> driverConstraints= driverConstraintsRepository.findConstraintsByDate(date);
        List<DriverConstraintsDTO> driverConstraintsDTOs = new ArrayList<>();
        for (DriverConstraint driverConstraint : driverConstraints) {
            Donor donor = userService.getDonorById(driverConstraint.getDriverId());
            DriverConstraintsDTO driverConstraintsDTO = new DriverConstraintsDTO(driverConstraint, donor);
            driverConstraintsDTOs.add(driverConstraintsDTO);
        }
        return driverConstraintsDTOs;
    }
    public List<DriverConstraint> getDriverConstraints(long driverId){
        logger.info("getDriverConstraints of driver id {}", driverId);
        return driverConstraintsRepository.findConstraintsByDriverId(driverId);
    }
    @Transactional
    public void submitRouteForDriver(long routeId) {
        logger.info("submitRouteForDriver with route id={}", routeId);
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
        route.setSubmitted(true);
        routeRepository.save(route);
        userService.setDonationToDonor(route.getDriverId(), route.getDate());
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
    public Route updateRoute(Route route){
        logger.info("updateRoute with route id={}", route.getRouteId());
        for(Visit visit : route.getVisit()){
            visit.setRoute(route);
        }
        return routeRepository.save(route);
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
            userService.setDonationToDonor(route.getDriverId(), route.getDate());
        }
        routeRepository.saveAll(routes);
        logger.info("submitAllRoutes with date {} done", date);
    }

    public void addAddressToRoute(long routeId,Visit visit){ 
        logger.info("addAddressToRoute with route id={} and visitId= {}", routeId, visit);
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
        route.addVisit(visit);
        routeRepository.save(route);
        logger.info("addAddressToRoute with route id={} and visit {} done", routeId, visit);
    }
    public void removeAddressFromRoute(long routeId,Visit visit){
        logger.info("removeAddressFromRoute with route id={} and visitId= {}", routeId, visit);
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
        if(!route.getVisit().contains(visit))
            throw new VisitNotExistException(routeId, visit.getVisitId());
        route.removeVisit(visit);
        routeRepository.save(route);
        logger.info("removeAddressFromRoute with route id={} and visitId={} done", routeId, visit);
    }
    public Route getRoute(long routeId){
        logger.info("getRoute with route id={}", routeId);
        return routeRepository.findById(routeId).orElseThrow(() -> new RouteNotFoundException(routeId));
    }
    public Route getRoute(LocalDate date, long driverId){
        logger.info("getRoute with date {} and driver id {}", date, driverId);
        return routeRepository.findRouteByDateAndDriverId(date, driverId).orElse(null);
    }
    public List<Route> getRoutes(LocalDate date){
        logger.info("getRoutes with date {}", date);
        return routeRepository.findRoutesByDate(date);
    }
    public List<Route> viewHistory(){
       logger.info("viewHistory");
       return routeRepository.findAll();
    }
    public List<Route> getRoutesByDriverId(long driverId){
        logger.info("getRoutesByDriverId with driver id {}", driverId);
        return routeRepository.findRoutesByDriverId(driverId);
    }
    public List<DriverConstraint> getDriverFutureConstraintsHaventConfirmed(long driverId){
        logger.info("getDriverConstraintsHaventConfirmed with driver id {}", driverId);

        // Create a mutable list using collect(Collectors.toList()) instead of toList()
        List<DriverConstraint> constraints = driverConstraintsRepository.findConstraintsByDriverId(driverId);
        return constraints;
//                .stream()
//                .filter(constraint -> constraint.getDate().isBefore(LocalDate.now()))
//                .collect(Collectors.toList());  // Changed from toList()
//
//        logger.info("got constraints");
//        List<Route> routes = routeRepository.findRoutesByDriverId(driverId);
//        logger.info("got routes");
//
//        constraints.removeIf(constraint -> routes.stream().anyMatch(route -> route.getDate().equals(constraint.getDate())));
//        logger.info("getDriverConstraintsHaventConfirmed with driver id {} done", driverId);
//        return constraints;
    }
}
