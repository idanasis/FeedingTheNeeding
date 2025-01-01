package Project.Final.FeedingTheNeeding.cook.DTO;

public enum Status {
    Pending("Pending"),
    Accepted("Accepted"),
    Declined("Declined");

    private final String status;

    Status(String status){
        this.status = status;
    }

    public String getStatus(){return this.status;}
}
