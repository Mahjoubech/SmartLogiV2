package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.mahjoubech.smartlogiv2.dto.response.*;
import lombok.Data;

import java.time.LocalDateTime;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , locale = "fr")
    private LocalDateTime dateCreation;
    private ClientDestinataireResponse clientExpediteur;
    private ClientDestinataireResponse destinataire;
    private ZoneResponse zone;

    private LivreurResponse livreur;
    private List<HistoriqueLivraisonResponse> historique;
    private List<ColisProduitResponse> produits;

}
