package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import io.github.mahjoubech.smartlogiv2.dto.response.basic.RolesResponesBasic;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import lombok.Data;

import java.util.Set;

@Data
public class GestionResponse {
    private String id;
    private String nom;
    private String prenom;
    private String telephone;
    private RolesEntity role;
}
