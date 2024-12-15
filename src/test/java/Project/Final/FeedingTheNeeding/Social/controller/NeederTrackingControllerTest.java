package Project.Final.FeedingTheNeeding.Social.controller;

import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.social.controller.NeederTrackingController;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.model.WeekStatus;
import Project.Final.FeedingTheNeeding.social.projection.NeederTrackingProjection;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NeederTrackingController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
public class NeederTrackingControllerTest {

    private static final Long NEEDY_ID = 1L;
    private static final int FAMILY_SIZE = 4;
    private static final String DIETARY_PREFERENCES = "Vegetarian";
    private static final String ADDITIONAL_NOTES = "Requires delivery before noon";
    private static final String UPDATED_DIETARY_PREFERENCES = "Vegetarian, No Sugar";
    private static final Long NEEDER_TRACKING_ID = 10L;
    private static final LocalDate DATE = LocalDate.of(2021, 10, 10);
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

    @Test
    public void testGetNeederTrackingById() throws Exception {
        // Arrange
        Needy needy = new Needy();
        needy.setId(NEEDY_ID);
        needy.setConfirmStatus(NeedyStatus.PENDING);
        needy.setFamilySize(FAMILY_SIZE);

        NeederTracking mockNeederTracking = new NeederTracking();
        mockNeederTracking.setId(NEEDER_TRACKING_ID);
        mockNeederTracking.setNeedy(needy);
        mockNeederTracking.setDietaryPreferences(DIETARY_PREFERENCES);
        mockNeederTracking.setAdditionalNotes(ADDITIONAL_NOTES);

        Mockito.when(neederTrackingService.getNeederTrackById(NEEDER_TRACKING_ID))
                .thenReturn(mockNeederTracking);

        // Act & Assert
        mockMvc.perform(get("/social/{id}", NEEDER_TRACKING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(NEEDER_TRACKING_ID))
                .andExpect(jsonPath("$.dietaryPreferences").value(DIETARY_PREFERENCES))
                .andExpect(jsonPath("$.additionalNotes").value(ADDITIONAL_NOTES))
                .andExpect(jsonPath("$.needy.id").value(NEEDY_ID))
                .andExpect(jsonPath("$.needy.familySize").value(FAMILY_SIZE));
    }

    @Test
    public void testUpdateNeederTracking() throws Exception {
        // Arrange
        Needy needy = new Needy();
        needy.setId(NEEDY_ID);
        needy.setConfirmStatus(NeedyStatus.PENDING);
        needy.setFamilySize(FAMILY_SIZE);

        NeederTracking existingNeederTracking = new NeederTracking();
        existingNeederTracking.setId(NEEDER_TRACKING_ID);
        existingNeederTracking.setNeedy(needy);
        existingNeederTracking.setDietaryPreferences(DIETARY_PREFERENCES);
        existingNeederTracking.setAdditionalNotes(ADDITIONAL_NOTES);

        NeederTracking updatedNeederTracking = new NeederTracking();
        updatedNeederTracking.setId(NEEDER_TRACKING_ID);
        updatedNeederTracking.setNeedy(needy);
        updatedNeederTracking.setDietaryPreferences(UPDATED_DIETARY_PREFERENCES);
        updatedNeederTracking.setAdditionalNotes(ADDITIONAL_NOTES);

        Mockito.when(neederTrackingService.updateNeederTrack(NEEDER_TRACKING_ID, updatedNeederTracking))
                .thenReturn(updatedNeederTracking);

        // Act & Assert
        mockMvc.perform(put("/social/{id}", NEEDER_TRACKING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedNeederTracking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(NEEDER_TRACKING_ID))
                .andExpect(jsonPath("$.dietaryPreferences").value(UPDATED_DIETARY_PREFERENCES))
                .andExpect(jsonPath("$.additionalNotes").value(ADDITIONAL_NOTES))
                .andExpect(jsonPath("$.needy.id").value(NEEDY_ID))
                .andExpect(jsonPath("$.needy.familySize").value(FAMILY_SIZE));
    }

    @Test
    public void testDeleteNeederTracking() throws Exception {
        // Arrange
        Mockito.doNothing().when(neederTrackingService).deleteNeederTrack(NEEDER_TRACKING_ID);

        // Act & Assert
        mockMvc.perform(delete("/social/{id}", NEEDER_TRACKING_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetNeedersHere() throws Exception {
        // Arrange
        Needy needy = new Needy();
        needy.setId(NEEDY_ID);
        needy.setConfirmStatus(NeedyStatus.PENDING);
        needy.setFamilySize(FAMILY_SIZE);

        // Create a mock NeederTracking and set all necessary properties
        NeederTracking mockNeederTracking = new NeederTracking();
        mockNeederTracking.setId(NEEDER_TRACKING_ID);
        mockNeederTracking.setNeedy(needy);
        mockNeederTracking.setDietaryPreferences(DIETARY_PREFERENCES);
        mockNeederTracking.setAdditionalNotes(ADDITIONAL_NOTES);
        mockNeederTracking.setWeekStatus(WeekStatus.Here); // Ensure weekStatus is set
        mockNeederTracking.setDate(DATE);

        // Create a real implementation of NeederTrackingProjection
        NeederTrackingProjection projection = new NeederTrackingProjection() {
            @Override
            public Long getId() {
                return mockNeederTracking.getId();
            }

            @Override
            public Long getNeedy_Id() {
                return mockNeederTracking.getNeedy().getId();
            }

            @Override
            public String getWeekStatus() {
                return mockNeederTracking.getWeekStatus().toString();  // Avoid null issue
            }

            @Override
            public String getDietaryPreferences() {
                return mockNeederTracking.getDietaryPreferences();
            }

            @Override
            public String getAdditionalNotes() {
                return mockNeederTracking.getAdditionalNotes();
            }

            @Override
            public LocalDate getDate() {
                return mockNeederTracking.getDate();
            }
        };

        // Mock the service method to return a list with this projection
        Mockito.when(neederTrackingService.getNeedersHereByDate(DATE))
                .thenReturn(Collections.singletonList(projection));

        // Act & Assert
        mockMvc.perform(get("/social/getNeedersHere").param("date", DATE.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(NEEDER_TRACKING_ID))
                .andExpect(jsonPath("$[0].dietaryPreferences").value(DIETARY_PREFERENCES))
                .andExpect(jsonPath("$[0].additionalNotes").value(ADDITIONAL_NOTES))
                .andExpect(jsonPath("$[0].needy_Id").value(NEEDY_ID)).andExpect(jsonPath("$[0].weekStatus").value("Here"));
                // Ensure this matches the field name in projection
    }


    @Test
    public void testGetNeedyFromNeederTrackingId() throws Exception {
        // Arrange
        Needy needy = new Needy();
        needy.setId(NEEDY_ID);
        needy.setConfirmStatus(NeedyStatus.PENDING);
        needy.setFamilySize(FAMILY_SIZE);

        NeederTracking mockNeederTracking = new NeederTracking();
        mockNeederTracking.setId(NEEDER_TRACKING_ID);
        mockNeederTracking.setNeedy(needy);
        mockNeederTracking.setDietaryPreferences(DIETARY_PREFERENCES);
        mockNeederTracking.setAdditionalNotes(ADDITIONAL_NOTES);

        Mockito.when(neederTrackingService.getNeederTrackById(NEEDER_TRACKING_ID))
                .thenReturn(mockNeederTracking);

        // Act & Assert
        mockMvc.perform(get("/social/getNeedy/{id}", NEEDER_TRACKING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(NEEDY_ID))  // Expecting the Needy id
                .andExpect(jsonPath("$.firstName").value(needy.getFirstName()))  // Expecting the first name
                .andExpect(jsonPath("$.lastName").value(needy.getLastName()))  // Expecting the last name
                .andExpect(jsonPath("$.phoneNumber").value(needy.getPhoneNumber()))  // Expecting the phone number
                .andExpect(jsonPath("$.address").value(needy.getAddress()))  // Expecting the address
                .andExpect(jsonPath("$.city").value(needy.getCity()))  // Expecting the city
                .andExpect(jsonPath("$.additionalNotes").value(ADDITIONAL_NOTES));  // Expecting the additional notes
    }

    @Test
    public void testGetAllNeederTrackingsByDate() throws Exception {
        // Arrange
        NeederTracking mockNeederTracking = new NeederTracking();
        mockNeederTracking.setId(1L);
        mockNeederTracking.setDate(DATE);

        Mockito.when(neederTrackingService.getAllNeedersTrackingsByDate(DATE))
                .thenReturn(Collections.singletonList(mockNeederTracking));

        // Act & Assert
        mockMvc.perform(get("/social/getNeedersByDate")
                        .param("date", DATE.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].date").value(DATE.toString()));
    }

}
