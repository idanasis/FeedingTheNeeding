package Project.Final.FeedingTheNeeding.Driving.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import Project.Final.FeedingTheNeeding.driving.Controller.DrivingController;
import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraintId;
import Project.Final.FeedingTheNeeding.driving.Model.Route;
import Project.Final.FeedingTheNeeding.driving.Model.Visit;
import Project.Final.FeedingTheNeeding.driving.Model.VisitStatus;

import java.time.LocalDate;
import java.util.Collections;

@WebMvcTest(DrivingController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
public class DrivingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DrivingFascade drivingService;
    private Route route;
    final Long driverId=1L,routeId=1L,visitId = 1L;
    String status = VisitStatus.Deliver.name();
    LocalDate date = LocalDate.now();

    @BeforeEach
    void setUp() {
        route = new Route(date);
    }

    @Test
    void testSubmitConstraint() throws Exception {
        DriverConstraint constraint = new DriverConstraint();
        when(drivingService.submitConstraint(any(DriverConstraint.class))).thenReturn(constraint);

        mockMvc.perform(post("/driving/constraints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(constraint)))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).submitConstraint(any(DriverConstraint.class));
    }
    @Test
    void testSubmitConstraintFail() throws Exception {
        DriverConstraint constraint = new DriverConstraint();
        when(drivingService.submitConstraint(any(DriverConstraint.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/driving/constraints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(constraint)))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).submitConstraint(any(DriverConstraint.class));
    }
    @Test
    void testRemoveConstraint() throws Exception {
        DriverConstraintId constraintId = new DriverConstraintId(driverId, date);

        mockMvc.perform(delete("/driving/constraints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(constraintId)))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).removeConstraint(constraintId);
    }
    @Test
    void testRemoveConstraintFail() throws Exception {
        DriverConstraintId constraintId = new DriverConstraintId(driverId, date);
        doThrow(new IllegalArgumentException()).when(drivingService).removeConstraint(constraintId);
        mockMvc.perform(delete("/driving/constraints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(constraintId)))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).removeConstraint(constraintId);
    }
    @Test
    void testGetDateConstraints() throws Exception {
        when(drivingService.getDateConstraints(any(LocalDate.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/driving/constraints/{date}", date.toString()))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).getDateConstraints(date);
    }
    @Test
    void testGetDateConstraintsFail() throws Exception {
        when(drivingService.getDateConstraints(any(LocalDate.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/driving/constraints/{date}", date.toString()))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).getDateConstraints(date);
    }
    @Test
    void testGetDriverConstraints() throws Exception {
        when(drivingService.getDriverConstraints(driverId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/driving/constraints/driver/{driverId}", driverId))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).getDriverConstraints(driverId);
    }
    @Test
    void testGetDriverConstraintsFail() throws Exception {
        when(drivingService.getDriverConstraints(driverId)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/driving/constraints/driver/{driverId}", driverId))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).getDriverConstraints(driverId);
    }
    @Test
    void testSubmitRouteForDriver() throws Exception {
        mockMvc.perform(post("/driving/routes/submit/{routeId}", routeId))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).submitRouteForDriver(routeId);
    }
    @Test
    void testSubmitRouteForDriverFail() throws Exception {
        doThrow(new IllegalArgumentException()).when(drivingService).submitRouteForDriver(routeId);

        mockMvc.perform(post("/driving/routes/submit/{routeId}", routeId))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).submitRouteForDriver(routeId);
    }
    @Test
    void testCreateRoute() throws Exception {
        when(drivingService.createRoute(date)).thenReturn(route);

        mockMvc.perform(post("/driving/routes/create")
                .param("date", date.toString()))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).createRoute(date);
    }
    @Test
    void testCreateRouteFail() throws Exception {
        when(drivingService.createRoute(date)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/driving/routes/create")
                .param("date", date.toString()))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).createRoute(date);
    }
    @Test
    void testCreateRouteWithDriver() throws Exception {
        when(drivingService.createRoute(driverId, date)).thenReturn(route);

        mockMvc.perform(post("/driving/routes/create/driver")
                .param("driverId", driverId.toString())
                .param("date", date.toString()))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).createRoute(driverId, date);
    }
 
    @Test
    void testCreateRouteWithDriverFail() throws Exception {
        when(drivingService.createRoute(driverId, date)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/driving/routes/create/driver")
                .param("driverId", driverId.toString())
                .param("date", date.toString()))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).createRoute(driverId, date);
    }
    @Test
    void testSetDriverIdToRoute() throws Exception {
        mockMvc.perform(put("/driving/routes/{routeId}/driver/{driverId}", routeId, driverId))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).setDriverIdToRoute(routeId, driverId);
    }
    @Test
    void testSetDriverIdToRouteFail() throws Exception {
        doThrow(new IllegalArgumentException()).when(drivingService).setDriverIdToRoute(routeId, driverId);

        mockMvc.perform(put("/driving/routes/{routeId}/driver/{driverId}", routeId, driverId))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).setDriverIdToRoute(routeId, driverId);
    }
    @Test
    void testRemoveRoute() throws Exception {
        mockMvc.perform(delete("/driving/routes/{routeId}", routeId))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).removeRoute(routeId);
    }
    @Test
    void testRemoveRouteFail() throws Exception {
        doThrow(new IllegalArgumentException()).when(drivingService).removeRoute(routeId);
        mockMvc.perform(delete("/driving/routes/{routeId}", routeId))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).removeRoute(routeId);
    }
    @Test
    void testSubmitAllRoutes() throws Exception {
        mockMvc.perform(post("/driving/routes/submitAll/{date}", date.toString()))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).submitAllRoutes(date);
    }
    @Test
    void testSubmitAllRoutesFail() throws Exception {
        doThrow(new IllegalArgumentException()).when(drivingService).submitAllRoutes(date);

        mockMvc.perform(post("/driving/routes/submitAll/{date}", date.toString()))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).submitAllRoutes(date);
    }

    @Test
    void testGetRoute() throws Exception {
        when(drivingService.getRoute(routeId)).thenReturn(route);
        mockMvc.perform(get("/driving/routes/{routeId}", routeId))
                .andExpect(status().isOk());

        verify(drivingService, times(1)).getRoute(routeId);
    }
    @Test
    void testGetRouteFail() throws Exception {
        when(drivingService.getRoute(routeId)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(get("/driving/routes/{routeId}", routeId))
                .andExpect(status().isBadRequest());

        verify(drivingService, times(1)).getRoute(routeId);
    }
    @Test
    void testGetRouteByDateAndDriver() throws Exception {  
        when(drivingService.getRoute(date, driverId)).thenReturn(route);
        mockMvc.perform(get("/driving/routes")
                .param("date", date.toString())
                .param("driverId", Long.toString(driverId)))
                .andExpect(status().isOk());
        verify(drivingService, times(1)).getRoute(date, driverId);
    }
    @Test
    void testGetRouteByDateAndDriverFail() throws Exception {
        when(drivingService.getRoute(date, driverId)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(get("/driving/routes")
                .param("date", date.toString())
                .param("driverId", Long.toString(driverId)))
                .andExpect(status().isBadRequest());
        verify(drivingService, times(1)).getRoute(date, driverId);
    }
    @Test
    void testViewHistory() throws Exception {
        when(drivingService.viewHistory()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/driving/history"))
                .andExpect(status().isOk());
        verify(drivingService, times(1)).viewHistory();
    }
    @Test
    void testViewHistoryFail() throws Exception {
        when(drivingService.viewHistory()).thenThrow(new IllegalArgumentException());
        mockMvc.perform(get("/driving/history"))
                .andExpect(status().isBadRequest());
        verify(drivingService, times(1)).viewHistory();
    }
    @Test
    void testGetRoutes() throws Exception {
        LocalDate date = LocalDate.of(2023, 12, 28);
        when(drivingService.getRoutes(date)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/driving/routes/getRoutes")
                .param("date", date.toString())) // Add the date parameter
                .andExpect(status().isOk());

        verify(drivingService, times(1)).getRoutes(date);
    }

    @Test
    void testGetRoutesFail() throws Exception {
        String invalidDate = "invalid-date";
        when(drivingService.getRoutes(any())).thenThrow(new IllegalArgumentException("Invalid date format"));

        mockMvc.perform(get("/driving/routes/getRoutes")
                .param("date", invalidDate)) // Invalid date parameter
                .andExpect(status().isBadRequest());

        verify(drivingService, never()).getRoutes(any()); // No service call since parsing failed
    }
    @Test
    void testGetDriverFutureConstraintsHaventConfirmed() throws Exception {
        long driverId = 123L;
        when(drivingService.getDriverFutureConstraintsHaventConfirmed(driverId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/driving/constraints/driver/futureNotApproved")
                .param("driverId", Long.toString(driverId))) // Add driverId parameter
                .andExpect(status().isOk());

        verify(drivingService, times(1)).getDriverFutureConstraintsHaventConfirmed(driverId);
    }

@Test
void testGetDriverFutureConstraintsHaventConfirmedFail() throws Exception {
    String invalidDriverId = "not-a-number";
    when(drivingService.getDriverFutureConstraintsHaventConfirmed(anyLong()))
            .thenThrow(new IllegalArgumentException("Invalid driverId"));

    mockMvc.perform(get("/driving/constraints/driver/futureNotApproved")
            .param("driverId", invalidDriverId)) // Invalid driverId parameter
            .andExpect(status().isBadRequest());

    verify(drivingService, never()).getDriverFutureConstraintsHaventConfirmed(anyLong()); // No service call since parsing failed
}
@Test
void testUpdateRoute() throws Exception {
    Route route = new Route(); // Use a mock or create a dummy Route object
    when(drivingService.updateRoute(route)).thenReturn(route);

    mockMvc.perform(patch("/driving/routes/updateRoute")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(route))) // Send the Route object as JSON
            .andExpect(status().isOk());

    verify(drivingService, times(1)).updateRoute(route);
}

@Test
void testUpdateRouteFail() throws Exception {
    Route route = new Route(); // Use a mock or create a dummy Route object
    when(drivingService.updateRoute(route)).thenThrow(new IllegalArgumentException("Invalid route data"));

    mockMvc.perform(patch("/driving/routes/updateRoute")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(route))) // Send the Route object as JSON
            .andExpect(status().isBadRequest());

    verify(drivingService, times(1)).updateRoute(route);
}


}

