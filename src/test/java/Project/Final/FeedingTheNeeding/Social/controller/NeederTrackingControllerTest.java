package Project.Final.FeedingTheNeeding.Social.controller;

import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
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

    private static final Long NEEDY_ID = 1L;
    private static final int FAMILY_SIZE = 4;
    private static final String DIETARY_PREFERENCES = "Vegetarian";
    private static final String ADDITIONAL_NOTES = "Requires delivery before noon";
    private static final String UPDATED_DIETARY_PREFERENCES = "Vegetarian, No Sugar";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NeederTrackingService neederTrackingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllNeeders() throws Exception {
        // Arrange
        Needy needy = new Needy();
        needy.setId(NEEDY_ID);
        needy.setConfirmStatus(NeedyStatus.PENDING);
        needy.setFamilySize(FAMILY_SIZE);

        NeederTracking mockNeederTracking = new NeederTracking();
        mockNeederTracking.setId(NEEDY_ID);
        mockNeederTracking.setNeedy(needy);
        mockNeederTracking.setDietaryPreferences(DIETARY_PREFERENCES);
        mockNeederTracking.setAdditionalNotes(ADDITIONAL_NOTES);

        Mockito.when(neederTrackingService.getAllNeedersTrackings())
                .thenReturn(Arrays.asList(mockNeederTracking));

        // Act & Assert
        mockMvc.perform(get("/social")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(NEEDY_ID))
                .andExpect(jsonPath("$[0].dietaryPreferences").value(DIETARY_PREFERENCES))
                .andExpect(jsonPath("$[0].additionalNotes").value(ADDITIONAL_NOTES))
                .andExpect(jsonPath("$[0].needy.id").value(NEEDY_ID))
                .andExpect(jsonPath("$[0].needy.familySize").value(FAMILY_SIZE));
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
        Needy needy = new Needy();
        needy.setId(NEEDY_ID);
        needy.setConfirmStatus(NeedyStatus.PENDING);
        needy.setFamilySize(FAMILY_SIZE);

        NeederTracking newNeederTracking = new NeederTracking();
        newNeederTracking.setNeedy(needy);
        newNeederTracking.setId(NEEDY_ID);
        newNeederTracking.setDietaryPreferences(UPDATED_DIETARY_PREFERENCES);
        newNeederTracking.setAdditionalNotes(ADDITIONAL_NOTES);

        Mockito.when(neederTrackingService.addNeederTracking(Mockito.any(NeederTracking.class)))
                .thenReturn(newNeederTracking);

        // Act & Assert
        mockMvc.perform(post("/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newNeederTracking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(NEEDY_ID))
                .andExpect(jsonPath("$.dietaryPreferences").value(UPDATED_DIETARY_PREFERENCES))
                .andExpect(jsonPath("$.additionalNotes").value(ADDITIONAL_NOTES))
                .andExpect(jsonPath("$.needy.id").value(NEEDY_ID))
                .andExpect(jsonPath("$.needy.familySize").value(FAMILY_SIZE));
    }
}
