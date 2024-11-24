package Project.Final.FeedingTheNeeding.driving.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DriverConstraintsNotExistException extends IllegalArgumentException {
    public DriverConstraintsNotExistException(LocalDate date){
        super("Driver didnt submit any constraints for this date: "+date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    
}
