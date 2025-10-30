package io.github.mahjoubech.smartlogiv2.dto.response;

import lombok.Data;

import java.time.ZonedDateTime;
@Data
public class HistoriqueLivraisonResponse {
    private String id;
    private String statut;
    private String commentaire;
    private ZonedDateTime dateChangement;
}
