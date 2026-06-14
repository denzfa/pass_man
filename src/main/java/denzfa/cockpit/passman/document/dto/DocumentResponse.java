package denzfa.cockpit.passman.document.dto;

import denzfa.cockpit.passman.document.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Representation returned to clients. {@code title} / {@code description} are the
 * values resolved for the requested locale (see {@code Accept-Language}); the full
 * set of {@code translations} is also included.
 */
public record DocumentResponse(
        UUID id,
        String name,
        String resolvedLocale,
        String title,
        String description,
        List<TranslationDto> translations,
        Instant createdAt,
        Instant modifiedAt,
        String createdBy,
        String modifiedBy
) {

    public static DocumentResponse from(Document document, String preferredLocale) {
        List<TranslationDto> translations = document.getTranslations().stream()
                .map(t -> new TranslationDto(t.getLocale(), t.getTitle(), t.getDescription()))
                .toList();

        TranslationDto resolved = resolve(translations, preferredLocale);

        return new DocumentResponse(
                document.getId(),
                document.getName(),
                resolved != null ? resolved.locale() : null,
                resolved != null ? resolved.title() : null,
                resolved != null ? resolved.description() : null,
                translations,
                document.getCreatedAt(),
                document.getModifiedAt(),
                document.getCreatedBy(),
                document.getModifiedBy()
        );
    }

    /**
     * Picks the best translation: exact locale match, then language-only match,
     * then the first available translation as a fallback.
     */
    private static TranslationDto resolve(List<TranslationDto> translations, String preferredLocale) {
        if (translations.isEmpty()) {
            return null;
        }
        if (preferredLocale != null && !preferredLocale.isBlank()) {
            String wanted = preferredLocale.trim();
            for (TranslationDto t : translations) {
                if (t.locale().equalsIgnoreCase(wanted)) {
                    return t;
                }
            }
            String wantedLang = wanted.split("[-_]")[0];
            for (TranslationDto t : translations) {
                if (t.locale().split("[-_]")[0].equalsIgnoreCase(wantedLang)) {
                    return t;
                }
            }
        }
        return translations.get(0);
    }
}
