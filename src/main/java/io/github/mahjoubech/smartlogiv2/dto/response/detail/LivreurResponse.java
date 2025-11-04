package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class LivreurResponse {
    private String id;
    private String nom;
    private String prenom;
    private String telephone;
    private String vehicule;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ZoneResponse zoneAssignee;
}
