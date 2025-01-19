package Project.Final.FeedingTheNeeding.cook.DTO;

import java.time.LocalDate;
import java.util.Map;

public class PendingConstraintDTO {
    public long constraintId;

    public String name = "Default name"; //TODO: change the mapper function, then remove this

    public String startTime;
    public String endTime;

    public Map<String, Integer> constraints;

    public String address;

    public LocalDate date;

    public Status status;

    public String phoneNumber = "Default_Phone_Number"; //TODO: change the mapper function, then remove this

    //Spring needs a default constractor
    public PendingConstraintDTO() {}

    public PendingConstraintDTO(long constraintId, String startTime, String endTime, Map<String, Integer> constraints,
                                String address, LocalDate date, Status status){
        this.constraintId = constraintId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.constraints = constraints;
        this.address = address;
        this.date = date;
        this.status = status;
    }
}
