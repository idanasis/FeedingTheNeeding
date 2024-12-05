package Project.Final.FeedingTheNeeding.User.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Needy extends BaseUser {


    private int familySize;
    @Enumerated(EnumType.STRING)
    private NeedyStatus confirmStatus;


}