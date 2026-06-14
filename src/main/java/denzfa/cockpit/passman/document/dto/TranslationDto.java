package denzfa.cockpit.passman.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Locale-specific text carried in requests and responses.
 */
public record TranslationDto(

        @NotBlank
        @Size(max = 35)
        String locale,

        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 255)
        String description
) {
}
