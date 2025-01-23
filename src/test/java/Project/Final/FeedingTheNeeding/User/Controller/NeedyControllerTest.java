package Project.Final.FeedingTheNeeding.User.Controller;

import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Service.NeedyService;
import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NeederController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestSecurityConfig.class)
public class NeedyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NeedyService needyService;

    @Autowired
    private ObjectMapper objectMapper;

    // Endpoint Constants
    private static final String BASE_ENDPOINT = "/needer";
    private static final String CREATE_OR_UPDATE_ENDPOINT = "";
    private static final String GET_ALL_NEEDIES_ENDPOINT = "";
    private static final String GET_NEEDY_BY_ID_ENDPOINT = "/{id}";
    private static final String DELETE_NEEDY_BY_ID_ENDPOINT = "/{id}";
    private static final String GET_PENDING_NEEDIES_ENDPOINT = "/pending";
    private static final String GET_NEEDY_BY_PHONE_ENDPOINT = "/phone/{phoneNumber}";
    private static final String GET_NEEDY_TRACKING_ENDPOINT = "/needy-tracking";

    // Test Data Constants
    private static final long NEEDY_ID_EXISTING = 1L;
    private static final long NEEDY_ID_NON_EXISTING = 2L;
    private static final String NEEDY_FIRST_NAME = "John";
    private static final String NEEDY_LAST_NAME = "Doe";
    private static final String NEEDY_PHONE_NUMBER_EXISTING = "0510000000";
    private static final String NEEDY_PHONE_NUMBER_NON_EXISTING = "0520000000";
    private static final NeedyStatus NEEDY_STATUS_PENDING = NeedyStatus.PENDING;
    private static final NeedyStatus NEEDY_STATUS_APPROVED = NeedyStatus.APPROVED;
    private static final LocalDate TRACKING_DATE = LocalDate.of(2025, 1, 1);
    private static final String TRACKING_DATE_STRING = "2025-01-01";

    // Sample Needy Objects
    private Needy existingNeedy;
    private Needy nonExistingNeedy;
    private NeederTracking existingTracking;

    @BeforeEach
    void setUp() {
        existingNeedy = new Needy();
        existingNeedy.setId(NEEDY_ID_EXISTING);
        existingNeedy.setFirstName(NEEDY_FIRST_NAME);
        existingNeedy.setLastName(NEEDY_LAST_NAME);
        existingNeedy.setPhoneNumber(NEEDY_PHONE_NUMBER_EXISTING);
        existingNeedy.setConfirmStatus(NEEDY_STATUS_APPROVED);

        nonExistingNeedy = new Needy();
        nonExistingNeedy.setId(NEEDY_ID_NON_EXISTING);
        nonExistingNeedy.setFirstName("Jane");
        nonExistingNeedy.setLastName("Smith");
        nonExistingNeedy.setPhoneNumber(NEEDY_PHONE_NUMBER_NON_EXISTING);
        nonExistingNeedy.setConfirmStatus(NEEDY_STATUS_PENDING);

        existingTracking = new NeederTracking();
        existingTracking.setNeedy(existingNeedy);
        existingTracking.setDate(TRACKING_DATE);
    }

    @Nested
    @DisplayName("Create or Update Needy Tests")
    class CreateOrUpdateNeedyTests {

        @Test
        @DisplayName("POST /needer - Success")
        void testCreateOrUpdateNeedy_Success() throws Exception {
            Needy inputNeedy = new Needy();
            inputNeedy.setFirstName("Alice");
            inputNeedy.setLastName("Wonderland");
            inputNeedy.setPhoneNumber("0530000000");
            inputNeedy.setConfirmStatus(NeedyStatus.APPROVED);

            Needy savedNeedy = new Needy();
            savedNeedy.setId(3L);
            savedNeedy.setFirstName("Alice");
            savedNeedy.setLastName("Wonderland");
            savedNeedy.setPhoneNumber("0530000000");
            savedNeedy.setConfirmStatus(NeedyStatus.APPROVED);

            when(needyService.saveOrUpdateNeedy(any(Needy.class))).thenReturn(savedNeedy);

            mockMvc.perform(post(BASE_ENDPOINT + CREATE_OR_UPDATE_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputNeedy)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(savedNeedy.getId()))
                    .andExpect(jsonPath("$.firstName").value(savedNeedy.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(savedNeedy.getLastName()))
                    .andExpect(jsonPath("$.phoneNumber").value(savedNeedy.getPhoneNumber()))
                    .andExpect(jsonPath("$.confirmStatus").value(savedNeedy.getConfirmStatus().toString()));

            verify(needyService, times(1)).saveOrUpdateNeedy(any(Needy.class));
        }

        @Test
        @DisplayName("POST /needer - Failure")
        void testCreateOrUpdateNeedy_Failure() throws Exception {
            Needy inputNeedy = new Needy();
            inputNeedy.setFirstName("Bob");
            inputNeedy.setLastName("Builder");
            inputNeedy.setPhoneNumber("0540000000");
            inputNeedy.setConfirmStatus(NeedyStatus.PENDING);

            when(needyService.saveOrUpdateNeedy(any(Needy.class))).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(post(BASE_ENDPOINT + CREATE_OR_UPDATE_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputNeedy)))
                    .andExpect(status().isInternalServerError());

            verify(needyService, times(1)).saveOrUpdateNeedy(any(Needy.class));
        }
    }

    @Nested
    @DisplayName("Get All Needies Tests")
    class GetAllNeediesTests {

        @Test
        @DisplayName("GET /needer - Success")
        void testGetAllNeedies_Success() throws Exception {
            Needy needy1 = existingNeedy;
            Needy needy2 = nonExistingNeedy;
            List<Needy> needyList = Arrays.asList(needy1, needy2);

            when(needyService.getAllNeedyUsers()).thenReturn(needyList);

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_NEEDIES_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(needyList.size()))
                    .andExpect(jsonPath("$[0].id").value(needy1.getId()))
                    .andExpect(jsonPath("$[0].firstName").value(needy1.getFirstName()))
                    .andExpect(jsonPath("$[1].id").value(needy2.getId()))
                    .andExpect(jsonPath("$[1].firstName").value(needy2.getFirstName()));
        }

        @Test
        @DisplayName("GET /needer - Empty List")
        void testGetAllNeedies_Empty() throws Exception {
            when(needyService.getAllNeedyUsers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_NEEDIES_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(needyService, times(1)).getAllNeedyUsers();
        }

        @Test
        @DisplayName("GET /needer - Failure")
        void testGetAllNeedies_Failure() throws Exception {
            when(needyService.getAllNeedyUsers()).thenThrow(new RuntimeException("Failed to retrieve needy users"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_ALL_NEEDIES_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(needyService, times(1)).getAllNeedyUsers();
        }
    }

    @Nested
    @DisplayName("Get Needy By ID Tests")
    class GetNeedyByIdTests {

        @Test
        @DisplayName("GET /needer/{id} - Success")
        void testGetNeedyById_Success() throws Exception {
            when(needyService.getNeedyById(NEEDY_ID_EXISTING)).thenReturn(Optional.of(existingNeedy));

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_BY_ID_ENDPOINT, NEEDY_ID_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(existingNeedy.getId()))
                    .andExpect(jsonPath("$.firstName").value(existingNeedy.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(existingNeedy.getLastName()))
                    .andExpect(jsonPath("$.phoneNumber").value(existingNeedy.getPhoneNumber()))
                    .andExpect(jsonPath("$.confirmStatus").value(existingNeedy.getConfirmStatus().toString()));
        }

        @Test
        @DisplayName("GET /needer/{id} - Not Found")
        void testGetNeedyById_NotFound() throws Exception {
            when(needyService.getNeedyById(NEEDY_ID_NON_EXISTING)).thenReturn(Optional.empty());

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_BY_ID_ENDPOINT, NEEDY_ID_NON_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(needyService, times(1)).getNeedyById(NEEDY_ID_NON_EXISTING);
        }

        @Test
        @DisplayName("GET /needer/{id} - Failure")
        void testGetNeedyById_Failure() throws Exception {
            when(needyService.getNeedyById(NEEDY_ID_EXISTING)).thenThrow(new RuntimeException("Service error"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_BY_ID_ENDPOINT, NEEDY_ID_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(needyService, times(1)).getNeedyById(NEEDY_ID_EXISTING);
        }
    }

    @Nested
    @DisplayName("Delete Needy By ID Tests")
    class DeleteNeedyByIdTests {

        @Test
        @DisplayName("DELETE /needer/{id} - Success")
        void testDeleteNeedyById_Success() throws Exception {
            doNothing().when(needyService).deleteNeedyById(NEEDY_ID_EXISTING);

            mockMvc.perform(delete(BASE_ENDPOINT + DELETE_NEEDY_BY_ID_ENDPOINT, NEEDY_ID_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(needyService, times(1)).deleteNeedyById(NEEDY_ID_EXISTING);
        }

        @Test
        @DisplayName("DELETE /needer/{id} - Not Found")
        void testDeleteNeedyById_NotFound() throws Exception {
            doThrow(new IllegalArgumentException("Needy user with ID " + NEEDY_ID_NON_EXISTING + " does not exist."))
                    .when(needyService).deleteNeedyById(NEEDY_ID_NON_EXISTING);

            mockMvc.perform(delete(BASE_ENDPOINT + DELETE_NEEDY_BY_ID_ENDPOINT, NEEDY_ID_NON_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(needyService, times(1)).deleteNeedyById(NEEDY_ID_NON_EXISTING);
        }

        @Test
        @DisplayName("DELETE /needer/{id} - Failure")
        void testDeleteNeedyById_Failure() throws Exception {
            doThrow(new RuntimeException("Service error")).when(needyService).deleteNeedyById(NEEDY_ID_EXISTING);

            mockMvc.perform(delete(BASE_ENDPOINT + DELETE_NEEDY_BY_ID_ENDPOINT, NEEDY_ID_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(needyService, times(1)).deleteNeedyById(NEEDY_ID_EXISTING);
        }
    }

    @Nested
    @DisplayName("Get Pending Needies Tests")
    class GetPendingNeediesTests {

        @Test
        @DisplayName("GET /needer/pending - Success")
        void testGetPendingNeedies_Success() throws Exception {
            Needy pendingNeedy = new Needy();
            pendingNeedy.setId(3L);
            pendingNeedy.setFirstName("Eve");
            pendingNeedy.setLastName("Adams");
            pendingNeedy.setPhoneNumber("0540000000");
            pendingNeedy.setConfirmStatus(NeedyStatus.PENDING);

            List<Needy> pendingList = Collections.singletonList(pendingNeedy);

            when(needyService.getPendingNeedy()).thenReturn(pendingList);

            mockMvc.perform(get(BASE_ENDPOINT + GET_PENDING_NEEDIES_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(pendingList.size()))
                    .andExpect(jsonPath("$[0].id").value(pendingNeedy.getId()))
                    .andExpect(jsonPath("$[0].firstName").value(pendingNeedy.getFirstName()))
                    .andExpect(jsonPath("$[0].lastName").value(pendingNeedy.getLastName()))
                    .andExpect(jsonPath("$[0].phoneNumber").value(pendingNeedy.getPhoneNumber()))
                    .andExpect(jsonPath("$[0].confirmStatus").value(pendingNeedy.getConfirmStatus().toString()));
        }

        @Test
        @DisplayName("GET /needer/pending - Empty List")
        void testGetPendingNeedies_Empty() throws Exception {
            when(needyService.getPendingNeedy()).thenReturn(Collections.emptyList());

            mockMvc.perform(get(BASE_ENDPOINT + GET_PENDING_NEEDIES_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(needyService, times(1)).getPendingNeedy();
        }

        @Test
        @DisplayName("GET /needer/pending - Failure")
        void testGetPendingNeedies_Failure() throws Exception {
            when(needyService.getPendingNeedy()).thenThrow(new RuntimeException("Failed to retrieve pending needies"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_PENDING_NEEDIES_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(needyService, times(1)).getPendingNeedy();
        }
    }

    @Nested
    @DisplayName("Get Needy By Phone Number Tests")
    class GetNeedyByPhoneNumberTests {

        @Test
        @DisplayName("GET /needer/phone/{phoneNumber} - Success")
        void testGetNeedyByPhoneNumber_Success() throws Exception {
            when(needyService.getNeedyByPhoneNumber(NEEDY_PHONE_NUMBER_EXISTING))
                    .thenReturn(Optional.of(existingNeedy));

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_BY_PHONE_ENDPOINT, NEEDY_PHONE_NUMBER_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(existingNeedy.getId()))
                    .andExpect(jsonPath("$.firstName").value(existingNeedy.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(existingNeedy.getLastName()))
                    .andExpect(jsonPath("$.phoneNumber").value(existingNeedy.getPhoneNumber()))
                    .andExpect(jsonPath("$.confirmStatus").value(existingNeedy.getConfirmStatus().toString()));
        }

        @Test
        @DisplayName("GET /needer/phone/{phoneNumber} - Not Found")
        void testGetNeedyByPhoneNumber_NotFound() throws Exception {
            when(needyService.getNeedyByPhoneNumber(NEEDY_PHONE_NUMBER_NON_EXISTING))
                    .thenReturn(Optional.empty());

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_BY_PHONE_ENDPOINT, NEEDY_PHONE_NUMBER_NON_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(needyService, times(1)).getNeedyByPhoneNumber(NEEDY_PHONE_NUMBER_NON_EXISTING);
        }

        @Test
        @DisplayName("GET /needer/phone/{phoneNumber} - Failure")
        void testGetNeedyByPhoneNumber_Failure() throws Exception {
            when(needyService.getNeedyByPhoneNumber(NEEDY_PHONE_NUMBER_EXISTING))
                    .thenThrow(new RuntimeException("Service error"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_BY_PHONE_ENDPOINT, NEEDY_PHONE_NUMBER_EXISTING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(needyService, times(1)).getNeedyByPhoneNumber(NEEDY_PHONE_NUMBER_EXISTING);
        }
    }

    @Nested
    @DisplayName("Get Needy Users Tracking By Date Tests")
    class GetNeedyUsersTrackingByDateTests {

        @Test
        @DisplayName("GET /needer/needy-tracking?date=2025-01-01 - Success")
        void testGetNeedyUsersTrackingByDate_Success() throws Exception {
            NeederTracking tracking = new NeederTracking();
            tracking.setNeedy(existingNeedy);
            tracking.setDate(TRACKING_DATE);

            List<NeederTracking> trackingList = Collections.singletonList(tracking);

            when(needyService.getNeedyUsersTrackingByData(TRACKING_DATE)).thenReturn(trackingList);

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_TRACKING_ENDPOINT)
                            .param("date", TRACKING_DATE_STRING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(trackingList.size()))
                    .andExpect(jsonPath("$[0].needy.id").value(existingNeedy.getId()))
                    .andExpect(jsonPath("$[0].needy.firstName").value(existingNeedy.getFirstName()))
                    .andExpect(jsonPath("$[0].needy.lastName").value(existingNeedy.getLastName()))
                    .andExpect(jsonPath("$[0].needy.phoneNumber").value(existingNeedy.getPhoneNumber()))
                    .andExpect(jsonPath("$[0].needy.confirmStatus").value(existingNeedy.getConfirmStatus().toString()))
                    .andExpect(jsonPath("$[0].date").value(TRACKING_DATE_STRING));
        }

        @Test
        @DisplayName("GET /needer/needy-tracking?date=invalid-date - Failure")
        void testGetNeedyUsersTrackingByDate_InvalidDate() throws Exception {
            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_TRACKING_ENDPOINT)
                            .param("date", "invalid-date")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(needyService, times(0)).getNeedyUsersTrackingByData(any(LocalDate.class));
        }

        @Test
        @DisplayName("GET /needer/needy-tracking?date=2025-01-01 - Service Exception")
        void testGetNeedyUsersTrackingByDate_ServiceException() throws Exception {
            when(needyService.getNeedyUsersTrackingByData(TRACKING_DATE))
                    .thenThrow(new RuntimeException("Service error"));

            mockMvc.perform(get(BASE_ENDPOINT + GET_NEEDY_TRACKING_ENDPOINT)
                            .param("date", TRACKING_DATE_STRING)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Service error"));

            verify(needyService, times(1)).getNeedyUsersTrackingByData(TRACKING_DATE);
        }
    }
}
