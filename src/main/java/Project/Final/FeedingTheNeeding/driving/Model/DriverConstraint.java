package Project.Final.FeedingTheNeeding.driving.Model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@IdClass(DriverConstraintId.class) 
public class DriverConstraint {
    @Id
    private long driverId;
    @Id
    private LocalDate date;
    private int startHour;
    private int endHour;
    private String startLocation;
    private String requests;
    
    public DriverConstraint(long driverId, LocalDate date, int startHour, int endHour, String startLocation, String requests){
        this.driverId = driverId;
        this.date = date;
        this.startHour = startHour;
        this.endHour = endHour;
        this.startLocation = startLocation;
        this.requests = requests;
    }
    public long getDriverId(){
        return driverId;
    }
    public LocalDate getDate(){
        return date;
    }
    public int getStartHour(){
        return startHour;
    }
    public int getEndHour(){
        return endHour;
    }
    public String getStartLocation(){
        return startLocation;
    }
    public String getRequests(){
        return requests;
    }
    public void setDriverId(long driverId){
        this.driverId = driverId;
    }
    public void setDate(LocalDate date){
        this.date = date;
    }
    public void setStartHour(int startHour){
        this.startHour = startHour;
    }
    public void setEndHour(int endHour){
        this.endHour = endHour;
    }
    public void setStartLocation(String startLocation){
        this.startLocation = startLocation;
    }
    public void setRequests(String requests){
        this.requests = requests;
    }

    
}
