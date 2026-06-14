package denzfa.cockpit.passman.document;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A managed document. Localised text (title, description) is held in
 * {@link DocumentTranslation} rows, one per locale.
 */
@Entity
@Table(
        name = "document",
        uniqueConstraints = @UniqueConstraint(name = "uk_document_name", columnNames = "name")
)
@EntityListeners(AuditingEntityListener.class)
public class Document {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    /** Business identifier, unique but not the primary key. Max 30 chars. */
    @Column(name = "name", length = 30, nullable = false, unique = true)
    private String name;

    @OneToMany(
            mappedBy = "document",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = jakarta.persistence.FetchType.EAGER
    )
    @OrderBy("locale ASC")
    private List<DocumentTranslation> translations = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private Instant modifiedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    protected Document() {
        // for JPA
    }

    public Document(String name) {
        this.name = name;
    }

    /** Adds (or keeps both sides of) a translation association. */
    public void addTranslation(DocumentTranslation translation) {
        translation.setDocument(this);
        this.translations.add(translation);
    }

    /** Removes every existing translation and detaches it from this document. */
    public void clearTranslations() {
        for (DocumentTranslation t : new ArrayList<>(this.translations)) {
            t.setDocument(null);
        }
        this.translations.clear();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DocumentTranslation> getTranslations() {
        return translations;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }
}
