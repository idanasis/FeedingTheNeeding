package Project.Final.FeedingTheNeeding.cook.Model;

import java.time.LocalDate;

import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class CookConstraints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long constraintId;

    private long cookId;

    private String startTime;
    private String endTime;

    private int PlatesNum; //how many plates will she make. Min 2 plates

    private String location;
    private LocalDate date;

    private Status status = Status.Pending;

    public CookConstraints(){}

    public CookConstraints(long constraintId, long cookId, String startTime, String endTime, int platesNum, String loc, LocalDate date){
        this.constraintId = constraintId;
        this.cookId = cookId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.PlatesNum = platesNum;
        this.location = loc;
        this.date = date;
    }

    public CookConstraints(long constraintId, long cookId, String startTime, String endTime, int platesNum, String loc, LocalDate date, Status status){
        this.constraintId = constraintId;
        this.cookId = cookId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.PlatesNum = platesNum;
        this.location = loc;
        this.date = date;
        this.status = status;
    }
}
