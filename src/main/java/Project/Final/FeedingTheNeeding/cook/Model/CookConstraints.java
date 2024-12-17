package Project.Final.FeedingTheNeeding.cook.Model;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private LocalTime TakingTime; //the limit of when the drivers can take the food
    private int PlatesNum; //how many plates will she make. Min 2 plates

    private String location;
    private LocalDate date;  
    //TODO: later, add somehow different food types and food constraints like vegetarian or something

    public CookConstraints(){}

    public CookConstraints(long cookId, LocalTime takingTime, int platesNum, String loc, LocalDate date){
        this.cookId = cookId;
        this.TakingTime = takingTime;
        this.PlatesNum = platesNum;
        this.location = loc;
        this.date = date;
    }
}
