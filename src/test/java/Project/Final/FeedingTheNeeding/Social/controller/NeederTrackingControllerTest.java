package Project.Final.FeedingTheNeeding.Social.controller;
import Project.Final.FeedingTheNeeding.social.controller.NeederTrackingController;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NeederTrackingController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NeederTrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NeederTrackingService neederTrackingService; // Use @MockBean instead of @Mock

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllNeeders() throws Exception {
        // Arrange
        NeederTracking mockNeederTracking = new NeederTracking();
        mockNeederTracking.setId(1L);
        mockNeederTracking.setStatusForWeek("pending");
        mockNeederTracking.setFamilySize(4);

        Mockito.when(neederTrackingService.getAllNeedersTrackings())
                .thenReturn(Arrays.asList(mockNeederTracking));

        // Act & Assert
        mockMvc.perform(get("/social")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].statusForWeek").value("pending"))
                .andExpect(jsonPath("$[0].familySize").value(4));
    }

    @Test
    public void testGetAllNeeders_EmptyList() throws Exception {
        // Arrange
        Mockito.when(neederTrackingService.getAllNeedersTrackings())
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/social")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testAddNeeder() throws Exception {
        // Arrange
        NeederTracking newNeederTracking = new NeederTracking();
        newNeederTracking.setId(1L);
        newNeederTracking.setStatusForWeek("completed");
        newNeederTracking.setFamilySize(3);

        Mockito.when(neederTrackingService.addNeederTracking(Mockito.any(NeederTracking.class)))
                .thenReturn(newNeederTracking);

        // Act & Assert
        mockMvc.perform(post("/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newNeederTracking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.statusForWeek").value("completed"))
                .andExpect(jsonPath("$.familySize").value(3));
    }
}
