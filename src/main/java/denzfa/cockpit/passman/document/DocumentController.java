package denzfa.cockpit.passman.document;

import denzfa.cockpit.passman.document.dto.DocumentRequest;
import denzfa.cockpit.passman.document.dto.DocumentResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentResponse> list(
            @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage) {
        return service.findAll().stream()
                .map(d -> DocumentResponse.from(d, acceptLanguage))
                .toList();
    }

    @GetMapping("/{id}")
    public DocumentResponse get(
            @PathVariable UUID id,
            @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage) {
        return DocumentResponse.from(service.findById(id), acceptLanguage);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DocumentResponse> create(
            @Valid @RequestBody DocumentRequest request,
            @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage,
            UriComponentsBuilder uriBuilder) {
        Document created = service.create(request);
        URI location = uriBuilder.path("/api/documents/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(DocumentResponse.from(created, acceptLanguage));
    }

    @PutMapping("/{id}")
    public DocumentResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody DocumentRequest request,
            @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage) {
        return DocumentResponse.from(service.update(id, request), acceptLanguage);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
