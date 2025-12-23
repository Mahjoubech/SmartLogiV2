package io.github.mahjoubech.smartlogiv2.dto.request;

import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolesEntityRequest {
    @NotBlank(message = "Le nom du role est obligatoire")
    private Roles name;
}
