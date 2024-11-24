package Project.Final.FeedingTheNeeding.driving.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class DriverConstraintId implements Serializable {
    private long driverId;
    private LocalDate date;

    public DriverConstraintId() {}

    public DriverConstraintId(long driverId, LocalDate date) {
        this.driverId = driverId;
        this.date = date;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DriverConstraintId that = (DriverConstraintId) o;
        return driverId == that.driverId && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, date);
    }
}
