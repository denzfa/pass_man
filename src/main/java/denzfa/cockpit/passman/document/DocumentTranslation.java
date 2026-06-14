package denzfa.cockpit.passman.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

/**
 * Locale-specific text for a {@link Document}. One row per (document, locale).
 */
@Entity
@Table(
        name = "document_translation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_document_translation_locale",
                columnNames = {"document_id", "locale"}
        )
)
public class DocumentTranslation {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    /** BCP-47 language tag, e.g. "en", "fr", "de". */
    @Column(name = "locale", length = 35, nullable = false)
    private String locale;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    protected DocumentTranslation() {
        // for JPA
    }

    public DocumentTranslation(String locale, String title, String description) {
        this.locale = locale;
        this.title = title;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
