package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import io.github.mahjoubech.smartlogiv2.dto.response.basic.RolesResponesBasic;
import lombok.Data;

import java.util.Set;

@Data
public class GestionResponse {
    private String id;
    private String nom;
    private String prenom;
    private String telephone;
    private RolesResponesBasic role;
}
