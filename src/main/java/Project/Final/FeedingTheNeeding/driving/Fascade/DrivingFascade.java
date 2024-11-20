package Project.Final.FeedingTheNeeding.driving.Fascade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.Route;
import Project.Final.FeedingTheNeeding.driving.Repository.IdrivingRepository;
import Project.Final.FeedingTheNeeding.user.Model.NeederContactDTO;

@Service
public class DrivingFascade {
    @Autowired
    IdrivingRepository drivingRepository;


    public void submitConstraint(DriverConstraint constraint){
        
    }
    public void removeConstraint(DriverConstraint constraint){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void submitRouteForDriver(String routeId){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public List<String> getDateConstraints(LocalDate date){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void removeRoute(String routeId){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void sumbitAllRoutes(LocalDate date){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void addAddressToRoute(NeederContactDTO address){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void removeAddressFromRoute(NeederContactDTO address){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public Route getRoute(String routeId){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public Route getRoute(LocalDate date, String driverId){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void viewHistory(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
