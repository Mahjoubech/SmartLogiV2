package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionRequest {
    @NotBlank(message = "Le nom de la permission est obligatoire")
    private String name;
}
