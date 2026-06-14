package denzfa.cockpit.passman;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.Mockito.mock;

/**
 * Supplies a mock {@link JwtDecoder} so the application context starts without
 * contacting a live authorization server. Authentication in the tests is injected
 * directly via {@code SecurityMockMvcRequestPostProcessors.jwt()}.
 */
@TestConfiguration
public class TestSecurityBeans {

    @Bean
    JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }
}
