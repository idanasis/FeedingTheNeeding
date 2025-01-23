package Project.Final.FeedingTheNeeding.Security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Staff Permissions Endpoints Security Tests")
public class StaffPermissionsSecurityTest extends BaseSecurityTest {

    @Autowired
    protected MockMvc mockMvc;
}
