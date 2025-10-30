package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ZoneRequest {
    @NotBlank(message = "Le nom de la zone est obligatoire.")
    @Size(max = 255)
    private String nom;

    @NotBlank(message = "Le code postal est obligatoire.")
    @Size(max = 10)
    private String codePostal;
}
