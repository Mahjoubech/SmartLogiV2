package io.github.mahjoubech.smartlogiv2.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ColisResponse {
    private String id;
    private String description;
    private Double poids;
    private String statut;
    private String priorite;
    private String villeDestination;
    private ZonedDateTime dateCreation;
    private ClientDestinataireResponse clientExpediteur;
    private ClientDestinataireResponse destinataire;
    private ZoneResponse zone;
    private LivreurResponse livreur;

    private List<HistoriqueLivraisonResponse> historique;
    private List<ColisProduitResponse> produits;
}
