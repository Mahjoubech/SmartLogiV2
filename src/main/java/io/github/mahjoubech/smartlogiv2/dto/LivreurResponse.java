package io.github.mahjoubech.smartlogiv2.dto;

import lombok.Data;

@Data
public class LivreurResponse {
    private String id;
    private String nom;
    private String prenom;
    private String telephone;
    private String vehicule;
    private ZoneResponse zoneAssignee;
}
