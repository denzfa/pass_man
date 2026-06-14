package denzfa.cockpit.passman;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityBeans.class)
class PasswordsManagerApplicationTests {

    @Test
    void contextLoads() {
    }
}
