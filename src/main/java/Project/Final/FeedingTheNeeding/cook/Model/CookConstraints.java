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

    private long cookJWT = 0; //??? - probably gonna need to change this later

    private String startTime;
    private String endTime;

    private int PlatesNum; //how many plates will she make. Min 2 plates

    private String location;
    private LocalDate date;

    private Status status = Status.Pending;

    public CookConstraints(){}

    //TODO: add constractor that includes also the jwt somehow

    public CookConstraints(long constraintId, String startTime, String endTime, int platesNum, String loc, LocalDate date){
        this.constraintId = constraintId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.PlatesNum = platesNum;
        this.location = loc;
        this.date = date;
    }

    public CookConstraints(long constraintId, String startTime, String endTime, int platesNum, String loc, LocalDate date, Status status){
        this.constraintId = constraintId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.PlatesNum = platesNum;
        this.location = loc;
        this.date = date;
        this.status = status;
    }
}
