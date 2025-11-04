package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import lombok.Data;

@Data
public class ClientDestinataireResponse {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
}
