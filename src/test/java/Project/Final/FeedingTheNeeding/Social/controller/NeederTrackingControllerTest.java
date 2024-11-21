package Project.Final.FeedingTheNeeding.Social.controller;


import Project.Final.FeedingTheNeeding.social.controller.NeederTrackingController;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.service.NeederTrackingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NeederTrackingController.class)
public class NeederTrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private NeederTrackingService neederTrackingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllNeeders() throws Exception {
        NeederTracking needer = new NeederTracking();
        needer.setId(1L);
        needer.setName("John Doe");

        Mockito.when(neederTrackingService.getAllNeeders()).thenReturn(Arrays.asList(needer));

        mockMvc.perform(get("/social"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    public void testAddNeeder() throws Exception {
        NeederTracking needer = new NeederTracking();
        needer.setName("Jane Doe");

        Mockito.when(neederTrackingService.addNeeder(Mockito.any(NeederTracking.class))).thenReturn(needer);

        mockMvc.perform(post("/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(needer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }
}
