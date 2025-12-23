package io.github.mahjoubech.smartlogiv2.dto.response.basic;

import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import lombok.Data;

@Data
public class RolesResponesBasic {
    private RolesEntity name;
}
