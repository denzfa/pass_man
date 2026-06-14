package denzfa.cockpit.passman.document.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Payload for creating or replacing a document.
 */
public record DocumentRequest(

        @NotBlank
        @Size(max = 30)
        String name,

        @NotEmpty
        @Valid
        List<TranslationDto> translations
) {
}
