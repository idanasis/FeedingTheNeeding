package Project.Final.FeedingTheNeeding.driving.exception;

public class VisitNotExistException extends IllegalArgumentException{
    public VisitNotExistException(long visitIdrouteId,long visitId) {
        super("Visit with id " + visitId + " does not exist in route with id " + visitIdrouteId);
    }
}
