package io.github.mahjoubech.smartlogiv2.dto.response.basic;

import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import lombok.Data;

import java.time.ZonedDateTime;
@Data
public class ColisResponseBasic {
    private String id;
    private String description;
    private Double poids;
    private String statut;
    private String priorite;
    private String villeDestination;
    private ZonedDateTime dateCreation;
    private ClientDestinataireResponseBasic clientExpediteur;
    private ClientDestinataireResponseBasic destinataire;
    private ZoneResponse zone;
}
