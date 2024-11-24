package Project.Final.FeedingTheNeeding.driving.exception;

public class RouteNotFoundException extends IllegalArgumentException {
    public RouteNotFoundException(long routeId) {
        super("route with id " + routeId + " not found");
    }
    
}
