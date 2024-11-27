package Project.Final.FeedingTheNeeding.driving.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Data;

@Data
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routeId")
    private long routeId;
    private String driverId;
    private LocalDate date;
    private int startHour;

    @ElementCollection
    @CollectionTable(name="route_visits", joinColumns=@JoinColumn(name="routeId"))
    @Column(name="visited")
    private List<String> addresses;
    private boolean isSubmitted;

    public Route(String driverId, LocalDate date,int startHour, List<String> addresses) {
        this.driverId = driverId;
        this.date = date;
        this.startHour = startHour;
        this.addresses = addresses;
    }
    public Route(String driverId, LocalDate date,int startHour) {
        this.driverId = driverId;
        this.date = date;
        this.startHour = startHour;
        this.addresses = new ArrayList<>();
    }
    public Route(LocalDate date) {
        this.date = date;
        this.addresses = new ArrayList<>();
    }
    public long getRouteId() {
        return routeId;
    }
    public String getDriverId() {
        return driverId;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getStartHour() {
        return startHour;
    }
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }
    public List<String> getAddresses() {
        return addresses;
    }
    public void addAddresses(String address) {
        this.addresses.add(address);
    }
    public void removeAddresses(String address) {
        this.addresses.remove(address);
    }
    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }
    public boolean isSubmitted() {
        return isSubmitted;
    }
    public void setSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }
    
}
