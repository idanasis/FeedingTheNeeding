package Project.Final.FeedingTheNeeding.driving.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Project.Final.FeedingTheNeeding.User.Model.UserDTO;
import Project.Final.FeedingTheNeeding.User.Model.UserDTO;

public class Route {
    private Integer routeId;
    private String driverId;
    private LocalDate date;
    private int startHour;
    private List<UserDTO> addresses;
    private boolean isSubmitted;

    public Route(String driverId, LocalDate date,int startHour, List<UserDTO> addresses) {
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
    public Integer getRouteId() {
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
    public List<UserDTO> getAddresses() {
        return addresses;
    }
    public void addAddresses(UserDTO address) {
        this.addresses.add(address);
    }
    public void removeAddresses(UserDTO address) {
        this.addresses.remove(address);
    }
    public void setAddresses(List<UserDTO> addresses) {
        this.addresses = addresses;
    }
    public boolean isSubmitted() {
        return isSubmitted;
    }
    public void setSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }
    
}
