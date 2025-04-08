package Project.Final.FeedingTheNeeding.User.Model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Needy extends BaseUser {

    private int familySize;
    @Enumerated(EnumType.STRING)
    private NeedyStatus confirmStatus;

@OneToMany(mappedBy = "needy", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnore
private List<NeederTracking> neederTrackings;
}