package Project.Final.FeedingTheNeeding.SystemTests;

import Project.Final.FeedingTheNeeding.Authentication.DTO.AuthenticationRequest;
import Project.Final.FeedingTheNeeding.Authentication.DTO.RegistrationStatus;
import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.FeedingTheNeedingApplication;
import Project.Final.FeedingTheNeeding.User.Model.Donor;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.driving.Model.Route;
import Project.Final.FeedingTheNeeding.driving.Model.Visit;
import Project.Final.FeedingTheNeeding.driving.Model.VisitStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import Project.Final.FeedingTheNeeding.social.model.NeederTracking;
import Project.Final.FeedingTheNeeding.social.model.WeekStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = FeedingTheNeedingApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
public class SystemTestsImpl {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void endToEndWorkflow() throws Exception {
        // === Step 1: Needy Registration ===
        String needyJson = """
            {
                "firstName": "NeedyUser",
                "lastName": "Test",
                "phoneNumber": "123456789",
                "address": "rager",
                "familySize": 3
            }
        """;
        MvcResult needyRegisterResult = mockMvc.perform(post("/needer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(needyJson))
                .andExpect(status().isOk())
                .andReturn();
        Needy needy = objectMapper.readValue(needyRegisterResult.getResponse().getContentAsString(), Needy.class);
        needy.setConfirmStatus(NeedyStatus.APPROVED);

        // === Step 2: Admin Accepts Needy ===
        mockMvc.perform(post("/needer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(needy)))
                .andExpect(status().isOk())
                .andReturn();

        // === Step 3: Donor1 Registers ===
        String donor1Json = """
            {
                "email": "donor1@example.com",
                "firstName": "DonorUser1",
                "lastName": "Test",
                "phoneNumber": "0531223421",
                "address": "rager",
                "password": "12345678",
                "confirmPassword": "12345678"
            }
        """;
        mockMvc.perform(post("/auth/register/donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(donor1Json))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult donorGetResult=mockMvc.perform(get("/user/donor/donorId/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(donor1Json)).andReturn();
        Donor donor1 = objectMapper.readValue(donorGetResult.getResponse().getContentAsString(), Donor.class);

        donor1.setStatus(RegistrationStatus.AVAILABLE);
        // === Step 4: Admin Accepts Donor1 ===
        mockMvc.perform(put("/user/donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donor1)))
                .andExpect(status().isNoContent());

        // === Step 5: Donor2 Registers ===
        String donor2Json = """
            {
                "email": "donor2@example.com",
                "phoneNumber": "0578787877",
                "firstName": "DonorUser2",
                "lastName": "Test",
                "address": "rager",
                 "password": "12345678",
                "confirmPassword": "12345678"
            }
        """;
       mockMvc.perform(post("/auth/register/donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(donor2Json))
                .andExpect(status().isOk())
                .andReturn();


        mockMvc.perform(get("/user/donor/donorId/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(donor1Json)).andReturn();
        Donor donor2 = objectMapper.readValue(donorGetResult.getResponse().getContentAsString(), Donor.class);

        donor2.setStatus(RegistrationStatus.AVAILABLE);
        // === Step 6: Admin Accepts Donor2 ===
        mockMvc.perform(put("/user/donor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donor2)))
                .andExpect(status().isNoContent());


        // === Step 7: Update Needy Tracking ===
        MvcResult result = mockMvc.perform(get("/needer/needy-tracking?date=" + LocalDate.now()))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        List<NeederTracking> neederTrackings = Arrays.asList(objectMapper.readValue(jsonResponse, NeederTracking[].class));

        NeederTracking firstNeederTracking = neederTrackings.get(0);
        firstNeederTracking.setWeekStatus(WeekStatus.Here);

        String updatedJson = objectMapper.writeValueAsString(firstNeederTracking);

        mockMvc.perform(put("/social/" + firstNeederTracking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk());

        // === Step 8: Donor1 Adds Cooking Constraint ===
        AuthenticationRequest authenticationRequest=new AuthenticationRequest();
        authenticationRequest.setPhoneNumber("0531223421");
        authenticationRequest.setPassword("12345678");
        MvcResult donor1LoginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String tokenDonor1=objectMapper.readTree(donor1LoginResult.getResponse().getContentAsString()).get("token").textValue();
        AuthenticationRequest authenticationRequest2=new AuthenticationRequest();
        authenticationRequest2.setPhoneNumber("0578787877");
        authenticationRequest2.setPassword("12345678");
        String cookingConstraintJson = """
            {
                "cookId": %d,
                "startTime": "10",
                "endTime": "14",
                "constraints": { "vegetarian": 1 },
                "location": "Tel Aviv",
                "date": "%s"
            }
        """.formatted(1, LocalDate.now());

        mockMvc.perform(post("/cooking/submit/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cookingConstraintJson).header("Authorization", "Bearer " + tokenDonor1))
                .andExpect(status().isOk());

        // === Step 9: Admin Accepts Donor1 Cooking Constraint ===
        int constraintId = 1;
        mockMvc.perform(post("/cooking/acceptConstraint/" + constraintId))
                .andExpect(status().isOk());

        // === Step 10: Donor2 Adds Driving Constraint ===
        String drivingConstraintJson = """
            {
                "driverId": %d,
                "date": "%s",
                "startHour": 8,
                "endHour": 18,
                "startLocation": "Tel Aviv",
                "requests": "Available for driving on weekends"
            }
        """.formatted(2, LocalDate.now());

        mockMvc.perform(post("/driving/constraints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(drivingConstraintJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(2))
                .andExpect(jsonPath("$.requests").value("Available for driving on weekends"));

        // === Step 11: Admin Creates Route ===


        MvcResult createRouteResult = mockMvc.perform(post("/driving/routes/create?date="+LocalDate.now())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Long routeId = objectMapper.readTree(createRouteResult.getResponse().getContentAsString()).get("routeId").asLong();

        // === Step 12: Admin Adds Donor2 as Driver, and Visits for Donor1 and Needy ===
        Visit donorVisit = new Visit();
        donorVisit.setFirstName("Donor1");
        donorVisit.setLastName("Name");
        donorVisit.setPhoneNumber("1234567890");
        donorVisit.setAddress("Donor1 Address");
        donorVisit.setEndHour("13:00");
        donorVisit.setStatus(VisitStatus.Pickup);
        donorVisit.setPriority(1);
        donorVisit.setNote("Visit for Donor1");

        Visit needyVisit = new Visit();
        needyVisit.setFirstName("Needy");
        needyVisit.setLastName("Name");
        needyVisit.setPhoneNumber("9876543210");
        needyVisit.setAddress("Needy Address");
        needyVisit.setEndHour("14:00");
        needyVisit.setStatus(VisitStatus.Deliver);
        needyVisit.setPriority(1);
        needyVisit.setNote("Visit for Needy");


        // Populate route
        Route route = new Route();
        route.setRouteId(routeId); // Example route ID
        route.setDriverId(donor2.getId()); // Example driver ID
        route.setSubmitted(true);
        route.setVisit(Arrays.asList(donorVisit, needyVisit));
        route.setDate(LocalDate.now());
        mockMvc.perform(patch("/driving/routes/updateRoute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(route)))
                .andExpect(status().isOk());

        // === Step 13: Donor1 Checks Cooking Inlay ===
        String currentDate = LocalDate.now().toString();

        mockMvc.perform(post("/cooking/constraints/latest")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + tokenDonor1)
                        .content("""
                            {
                                "date": "%s"
                            }
                        """.formatted(currentDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Accepted"))
                .andExpect(jsonPath("$[0].constraints").exists())
                .andExpect(jsonPath("$[0].constraints['vegetarian']").value(1));

        // === Step 14: Donor2 Checks Driving Inlay ===
        mockMvc.perform(get("/driving/routes/getRoutes?date="+LocalDate.now()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].driverId").value(donor2.getId()))
                .andExpect(jsonPath("$[0].submitted").value(true));
    }
}