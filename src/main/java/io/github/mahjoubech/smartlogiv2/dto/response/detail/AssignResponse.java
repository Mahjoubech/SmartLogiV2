package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import lombok.Data;

import java.util.Set;

@Data
public class AssignResponse {
    private RolesResponse role;
    private Set<PermissionResponseDetail> permissions;
}
