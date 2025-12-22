package io.github.mahjoubech.smartlogiv2.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRequest {
    @NotBlank(message = "Le roleId est obligatoire")
    private String roleId;
    @NotBlank(message = "Le permissionId est obligatoire")
    private String permissionId;
}
