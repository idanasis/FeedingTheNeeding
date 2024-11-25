package Project.Final.FeedingTheNeeding.User.DTO;

import Project.Final.FeedingTheNeeding.User.Model.Driver;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DriverDTO extends BaseUserDTO{
    private DriverConstraint[] driverConstraints;
    private boolean isAvailable;
    private String vehicleNumber;
    private String licenseNumber;
    private String currentLocation;

    public DriverDTO(Driver driver) {
        super(driver);
        this.driverConstraints = driver.getDriverConstraints();
        this.isAvailable = driver.isAvailable();
        this.vehicleNumber = driver.getVehicleNumber();
        this.licenseNumber = driver.getLicenseNumber();
        this.currentLocation = driver.getCurrentLocation();
    }
}