package Project.Final.FeedingTheNeeding.cook.Model;

import java.time.LocalDate;
import java.util.Dictionary;
import java.util.Map;

import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity
public class CookConstraints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long constraintId;

    private long cookId;

    private String startTime;
    private String endTime;

    @Convert(converter = Map2JsonConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, Integer> constraints;

    private String location;
    private LocalDate date;

    private Status status = Status.Pending;

    public CookConstraints(){}

    public CookConstraints(long constraintId, long cookId, String startTime, String endTime, Map<String, Integer> constraints, String loc, LocalDate date){
        this.constraintId = constraintId;
        this.cookId = cookId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.constraints = constraints;
        this.location = loc;
        this.date = date;
    }

    public CookConstraints(long constraintId, long cookId, String startTime, String endTime, Map<String, Integer> constraints, String loc, LocalDate date, Status status){
        this.constraintId = constraintId;
        this.cookId = cookId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.constraints = constraints;
        this.location = loc;
        this.date = date;
        this.status = status;
    }
}
