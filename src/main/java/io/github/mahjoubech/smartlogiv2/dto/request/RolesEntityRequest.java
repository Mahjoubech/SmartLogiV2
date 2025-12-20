package io.github.mahjoubech.smartlogiv2.dto.request;

import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import lombok.Data;

@Data
public class RolesEntityRequest {
    private Roles name;
}
