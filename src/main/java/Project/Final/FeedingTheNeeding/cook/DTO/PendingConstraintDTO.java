package Project.Final.FeedingTheNeeding.cook.DTO;

import java.time.LocalDate;
import java.util.Map;

public class PendingConstraintDTO {
    public long constraintId;

    public String name;

    public String startTime;
    public String endTime;

    public Map<String, Integer> constraints;

    public String address;

    public LocalDate date;

    public Status status;

    public String phoneNumber;

    //Spring needs a default constractor
    public PendingConstraintDTO() {}

    public PendingConstraintDTO(long constraintId, String name, String startTime, String endTime, Map<String, Integer> constraints,
                                String address, LocalDate date, Status status, String phoneNumber){
        this.constraintId = constraintId;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.constraints = constraints;
        this.address = address;
        this.date = date;
        this.status = status;
        this.phoneNumber = phoneNumber;
    }
}
