package Project.Final.FeedingTheNeeding.cook.Controller;

import Project.Final.FeedingTheNeeding.cook.DTO.PendingConstraintDTO;
import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintMapperTest {

    private ConstraintMapper mapper;
    private CookConstraints constraint;
    private static final long CONSTRAINT_ID = 1L;
    private static final String NAME = "John Doe";
    private static final String PHONE = "123-456-7890";
    private static final String START_TIME = "09:00";
    private static final String END_TIME = "17:00";
    private static final String ADDRESS = "123 Main St";
    private static final String STREET = "ד'";
    private static final LocalDate DATE = LocalDate.now();
    private static final Map<String, Integer> CONSTRAINTS = new HashMap<>();

    @BeforeEach
    void setUp() {
        mapper = new ConstraintMapper();
        CONSTRAINTS.put("maxPlates", 5);

        constraint = new CookConstraints(
                CONSTRAINT_ID,
                1L,
                START_TIME,
                END_TIME,
                CONSTRAINTS,
                ADDRESS,
                DATE,
                Status.Pending,
                STREET
        );
    }

    @Test
    void testToDTO() {
        PendingConstraintDTO dto = mapper.toDTO(constraint, NAME, PHONE);

        assertEquals(CONSTRAINT_ID, dto.constraintId);
        assertEquals(NAME, dto.name);
        assertEquals(PHONE, dto.phoneNumber);
        assertEquals(START_TIME, dto.startTime);
        assertEquals(END_TIME, dto.endTime);
        assertEquals(CONSTRAINTS, dto.constraints);
        assertEquals(ADDRESS, dto.address);
        assertEquals(DATE, dto.date);
        assertEquals(Status.Pending, dto.status);
    }

    @Test
    void testToDTO_WithNullValues() {
        constraint = new CookConstraints(
                CONSTRAINT_ID,
                1L,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        PendingConstraintDTO dto = mapper.toDTO(constraint, null, null);

        assertEquals(CONSTRAINT_ID, dto.constraintId);
        assertNull(dto.name);
        assertNull(dto.phoneNumber);
        assertNull(dto.startTime);
        assertNull(dto.endTime);
        assertNull(dto.constraints);
        assertNull(dto.address);
        assertNull(dto.date);
        assertNull(dto.status);
    }

    @Test
    void testToDTO_WithEmptyConstraints() {
        constraint.setConstraints(new HashMap<>());

        PendingConstraintDTO dto = mapper.toDTO(constraint, NAME, PHONE);

        assertTrue(dto.constraints.isEmpty());
    }
}