package Project.Final.FeedingTheNeeding.driving.Model;

import java.time.LocalDate;

import Project.Final.FeedingTheNeeding.User.Model.Donor;
import lombok.Data;

@Data
public class DriverConstraintsDTO {
    private long driverId;
    private LocalDate date;
    private Integer startHour;
    private Integer endHour;
    private String startLocation;
    private String requests;
    private String driverFirstName;
    private String driverLastName;
    private String driverPhone;

    public DriverConstraintsDTO(DriverConstraint driverConstraint, Donor donor) {
        this.driverId = driverConstraint.getDriverId();
        this.date = driverConstraint.getDate();
        this.startHour = driverConstraint.getStartHour();
        this.endHour = driverConstraint.getEndHour();
        this.startLocation = driverConstraint.getStartLocation();
        this.requests = driverConstraint.getRequests();
        this.driverFirstName = donor.getFirstName();
        this.driverLastName = donor.getLastName();
        this.driverPhone = donor.getPhoneNumber();
    }
    
}
