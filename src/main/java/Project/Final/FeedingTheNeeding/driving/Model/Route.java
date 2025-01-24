package Project.Final.FeedingTheNeeding.driving.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routeId")
    private long routeId;
    private long driverId;
    private LocalDate date;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Visit> visit;
    private boolean isSubmitted;

    public Route(long driverId, LocalDate date,List<Visit> visits) {
        this.driverId = driverId;
        this.date = date;
        this.visit = visits;
        this.isSubmitted = false;
    }
    public Route(long driverId, LocalDate date) {
        this.driverId = driverId;
        this.date = date;
        this.visit = new ArrayList<>();
        this.isSubmitted = false;
    }
    public Route(LocalDate date) {
        this.date = date;
        this.visit = new ArrayList<>();
        this.isSubmitted = false;
    }
    public long getRouteId() {
        return routeId;
    }
    public long getDriverId() {
        return driverId;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public List<Visit> getVisit() {
        return visit;
    }
    public void addVisit(Visit address) {
        this.visit.add(address);
    }
    public void removeVisit(Visit address) {
        this.visit.remove(address);
    }
    public void setVisit(List<Visit> addresses) {
        this.visit = addresses;
    }
    public boolean isSubmitted() {
        return isSubmitted;
    }
    public void setSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

}
