package denzfa.cockpit.passman.document;

import denzfa.cockpit.passman.document.dto.DocumentRequest;
import denzfa.cockpit.passman.document.dto.TranslationDto;
import denzfa.cockpit.passman.error.DuplicateNameException;
import denzfa.cockpit.passman.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Document> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Document findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
    }

    public Document create(DocumentRequest request) {
        if (repository.existsByName(request.name())) {
            throw new DuplicateNameException("Document name already exists: " + request.name());
        }
        Document document = new Document(request.name());
        applyTranslations(document, request.translations());
        return repository.save(document);
    }

    public Document update(UUID id, DocumentRequest request) {
        Document document = findById(id);
        if (!document.getName().equals(request.name()) && repository.existsByName(request.name())) {
            throw new DuplicateNameException("Document name already exists: " + request.name());
        }
        document.setName(request.name());
        document.clearTranslations();
        applyTranslations(document, request.translations());
        return repository.save(document);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Document not found: " + id);
        }
        repository.deleteById(id);
    }

    private void applyTranslations(Document document, List<TranslationDto> translations) {
        for (TranslationDto t : translations) {
            document.addTranslation(new DocumentTranslation(t.locale(), t.title(), t.description()));
        }
    }
}
