package denzfa.cockpit.passman.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Disable writing nulls as JSON fields (optional, but commonly desired)
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        // Enable pretty printing for development (optional)
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
}
