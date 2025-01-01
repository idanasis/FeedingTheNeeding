package Project.Final.FeedingTheNeeding.cook.Exceptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CookConstraintsNotExistException extends IllegalArgumentException{
    public CookConstraintsNotExistException(LocalDate date){
        super("Cook havent submit any constraints for this date: "+date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    public CookConstraintsNotExistException(String err){
        super(err);
    }
}
