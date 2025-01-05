package Project.Final.FeedingTheNeeding.User.Controller;


import Project.Final.FeedingTheNeeding.TestConfig.TestSecurityConfig;
import Project.Final.FeedingTheNeeding.User.Model.Needy;
import Project.Final.FeedingTheNeeding.User.Model.NeedyStatus;
import Project.Final.FeedingTheNeeding.User.Service.NeedyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NeederController.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
public class NeedyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NeedyService needyService;

    @InjectMocks
    private NeederController neederController;

    private final long NEEDY1_ID = 1L, NEEDY2_ID = 2L;
    private final String NEEDY1_FIRST_NAME = "needy1", NEEDY2_FIRST_NAME = "needy2";
    private final String NEEDY1_LAST_NAME = "needy1", NEEDY2_LAST_NAME = "needy2";
    private final String NEEDY1_PHONE_NUMBER = "0510000000", NEEDY2_PHONE_NUMBER = "0520000000";
    private final NeedyStatus NEEDY1_STATUS = NeedyStatus.PENDING;
    private final NeedyStatus NEEDY2_STATUS = NeedyStatus.APPROVED;

    private Needy needy1;
    private Needy needy2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        needy1 = new Needy();
        needy1.setId(NEEDY1_ID);
        needy1.setFirstName(NEEDY1_FIRST_NAME);
        needy1.setLastName(NEEDY1_LAST_NAME);
        needy1.setPhoneNumber(NEEDY1_PHONE_NUMBER);
        needy1.setConfirmStatus(NEEDY1_STATUS);

        needy2 = new Needy();
        needy2.setId(NEEDY2_ID);
        needy2.setFirstName(NEEDY2_FIRST_NAME);
        needy2.setLastName(NEEDY2_LAST_NAME);
        needy2.setPhoneNumber(NEEDY2_PHONE_NUMBER);
        needy2.setConfirmStatus(NEEDY2_STATUS);
    }

    @Test
    void testCreateOrUpdateNeedy_Success() throws Exception {
        when(needyService.saveOrUpdateNeedy(any(Needy.class))).thenReturn(needy1);

        mockMvc.perform(post("/needer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(needy1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(NEEDY1_ID))
                .andExpect(jsonPath("$.firstName").value(NEEDY1_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(NEEDY1_LAST_NAME));


        verify(needyService, times(1)).saveOrUpdateNeedy(any(Needy.class));
    }

    @Test
    void testGetAllNeedies_Success() throws Exception {
        List<Needy> needyList = Arrays.asList(needy1, needy2);
        when(needyService.getAllNeedyUsers()).thenReturn(needyList);

        mockMvc.perform(get("/needer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].firstName").value(NEEDY1_FIRST_NAME))
                .andExpect(jsonPath("$[0].lastName").value(NEEDY1_LAST_NAME))
                .andExpect(jsonPath("$[1].firstName").value(NEEDY2_FIRST_NAME))
                .andExpect(jsonPath("$[1].lastName").value(NEEDY2_LAST_NAME));

        verify(needyService, times(1)).getAllNeedyUsers();
    }

    @Test
    void testGetNeedyById_Success() throws Exception {
        when(needyService.getNeedyById(NEEDY1_ID)).thenReturn(Optional.of(needy1));

        mockMvc.perform(get("/needer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(NEEDY1_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(NEEDY1_LAST_NAME));


        verify(needyService, times(1)).getNeedyById(1L);
    }

    @Test
    void testGetNeedyById_NotFound() throws Exception {
        when(needyService.getNeedyById(NEEDY1_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/needer/1"))
                .andExpect(status().isNotFound());

        verify(needyService, times(1)).getNeedyById(NEEDY1_ID);
    }

    @Test
    void testDeleteNeedyById_Success() throws Exception {
        doNothing().when(needyService).deleteNeedyById(NEEDY1_ID);

        mockMvc.perform(delete("/needer/1"))
                .andExpect(status().isNoContent());

        verify(needyService, times(1)).deleteNeedyById(NEEDY1_ID);
    }

    @Test
    void testDeleteNeedyById_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Needy user with ID 1 does not exist."))
                .when(needyService).deleteNeedyById(NEEDY1_ID);

        mockMvc.perform(delete("/needer/1"))
                .andExpect(status().isNotFound());

        verify(needyService, times(1)).deleteNeedyById(NEEDY1_ID);
    }

    @Test
    void testGetPendingNeedies_Success() throws Exception {
        List<Needy> pendingList = Arrays.asList(needy1);
        when(needyService.getPendingNeedy()).thenReturn(pendingList);

        mockMvc.perform(get("/needer/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].firstName").value(NEEDY1_FIRST_NAME))
                .andExpect(jsonPath("$[0].lastName").value(NEEDY1_LAST_NAME));


        verify(needyService, times(1)).getPendingNeedy();
    }

    @Test
    void testGetNeedyByPhoneNumber_Success() throws Exception {
        when(needyService.getNeedyByPhoneNumber(NEEDY1_PHONE_NUMBER)).thenReturn(Optional.of(needy1));

        mockMvc.perform(get("/needer/phone/0510000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(NEEDY1_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(NEEDY1_LAST_NAME));


        verify(needyService, times(1)).getNeedyByPhoneNumber(NEEDY1_PHONE_NUMBER);
    }

    @Test
    void testGetNeedyByPhoneNumber_NotFound() throws Exception {
        when(needyService.getNeedyByPhoneNumber(NEEDY1_PHONE_NUMBER)).thenReturn(Optional.empty());

        mockMvc.perform(get("/needer/phone/0510000000"))
                .andExpect(status().isNotFound());

        verify(needyService, times(1)).getNeedyByPhoneNumber(NEEDY1_PHONE_NUMBER);
    }
}
