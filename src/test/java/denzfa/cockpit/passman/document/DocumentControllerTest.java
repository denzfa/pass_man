package denzfa.cockpit.passman.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import denzfa.cockpit.passman.TestSecurityBeans;
import denzfa.cockpit.passman.document.dto.DocumentRequest;
import denzfa.cockpit.passman.document.dto.TranslationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityBeans.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ---- helpers -----------------------------------------------------------

    private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .jwt(builder -> builder.subject("admin@cockpit"));
    }

    private DocumentRequest sampleRequest(String name) {
        return new DocumentRequest(name, List.of(
                new TranslationDto("en", "Master Key", "The primary credential store"),
                new TranslationDto("fr", "Cle Maitresse", "Le coffre principal")
        ));
    }

    // ---- security ----------------------------------------------------------

    @Test
    void unauthenticatedRequestIsRejectedWith401() throws Exception {
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void nonAdminRequestIsRejectedWith403() throws Exception {
        mockMvc.perform(get("/api/documents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    // ---- CRUD --------------------------------------------------------------

    @Test
    void fullCrudLifecycleAsAdmin() throws Exception {
        // create
        MvcResult created = mockMvc.perform(post("/api/documents")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "fr")
                        .content(objectMapper.writeValueAsString(sampleRequest("vault-root"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("vault-root"))
                .andExpect(jsonPath("$.resolvedLocale").value("fr"))
                .andExpect(jsonPath("$.title").value("Cle Maitresse"))
                .andExpect(jsonPath("$.translations.length()").value(2))
                .andExpect(jsonPath("$.createdBy").value("admin@cockpit"))
                .andReturn();

        String id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText();

        // get with English resolution
        mockMvc.perform(get("/api/documents/{id}", id)
                        .with(adminJwt())
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Master Key"));

        // update
        DocumentRequest updated = new DocumentRequest("vault-root", List.of(
                new TranslationDto("en", "Master Vault", "Updated description")));
        mockMvc.perform(put("/api/documents/{id}", id)
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.translations.length()").value(1))
                .andExpect(jsonPath("$.title").value("Master Vault"));

        // delete
        mockMvc.perform(delete("/api/documents/{id}", id).with(adminJwt()))
                .andExpect(status().isNoContent());

        // gone
        mockMvc.perform(get("/api/documents/{id}", id).with(adminJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void duplicateNameReturns409() throws Exception {
        mockMvc.perform(post("/api/documents")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest("dup-name"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/documents")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest("dup-name"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void invalidPayloadReturns400() throws Exception {
        DocumentRequest invalid = new DocumentRequest("", List.of());
        mockMvc.perform(post("/api/documents")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.violations").isArray());
    }
}
