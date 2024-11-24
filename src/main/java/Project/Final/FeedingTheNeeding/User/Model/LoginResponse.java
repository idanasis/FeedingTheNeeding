package Project.Final.FeedingTheNeeding.User.Model;

import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private BaseUserDTO user;
}
