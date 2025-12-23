package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.RolesResponesBasic;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import lombok.Data;

import java.util.Set;

@Data
public class LivreurResponse {
    private String id;
    private String nom;
    private String prenom;
    private String telephone;
    private String vehicule;
    private RolesEntity role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ZoneResponse zoneAssignee;
}
