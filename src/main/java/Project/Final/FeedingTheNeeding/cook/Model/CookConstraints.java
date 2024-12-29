package Project.Final.FeedingTheNeeding.cook.Model;

import java.time.LocalDate;

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
    private long cookId;

    private String startTime;
    private String endTime;

    private int PlatesNum; //how many plates will she make. Min 2 plates

    private String location;
    private LocalDate date;  

    public CookConstraints(){}

    public CookConstraints(long cookId, String startTime, String endTime, int platesNum, String loc, LocalDate date){
        this.cookId = cookId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.PlatesNum = platesNum;
        this.location = loc;
        this.date = date;
    }
}
