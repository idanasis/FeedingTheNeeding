package Project.Final.FeedingTheNeeding.cook.DTO;

import java.time.LocalDate;

public class PendingConstraintDTO {
    public long constraintId;

    public String name = "Default name"; //TODO: change the mapper function, then remove this

    public String startTime;
    public String endTime;

    public int mealCount;

    public String addr;

    public LocalDate date;

    public Status status;

    //Spring needs a default constractor
    public PendingConstraintDTO() {}

    public PendingConstraintDTO(long constraintId, String startTime, String endTime, int mealCount,
                                String addr, LocalDate date, Status status){
        this.constraintId = constraintId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mealCount = mealCount;
        this.addr = addr;
        this.date = date;
        this.status = status;
    }
}
