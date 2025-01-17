package Project.Final.FeedingTheNeeding.cook.DTO;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class LatestConstraintsRequestDto {
    public long cookId;

    public LocalDate date;

    public LatestConstraintsRequestDto() {}

    public LatestConstraintsRequestDto(long cookId, LocalDate date){
        this.cookId = cookId;
        this.date = date;
    }
}
