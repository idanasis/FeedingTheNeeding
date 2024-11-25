package Project.Final.FeedingTheNeeding.User.Model;

import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "drivers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Driver extends BaseUser {
    private DriverConstraint[] driverConstraints;
    private boolean isAvailable;
    private String vehicleNumber;
    private String licenseNumber;
    private String currentLocation;
}