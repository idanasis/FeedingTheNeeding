package Project.Final.FeedingTheNeeding.cook.DTO;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class LatestConstraintsRequestDto {

    public String date;

    public LatestConstraintsRequestDto() {}

    public LatestConstraintsRequestDto(String date){
        this.date = date;
    }
}
