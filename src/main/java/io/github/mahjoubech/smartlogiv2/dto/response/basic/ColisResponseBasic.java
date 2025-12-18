package io.github.mahjoubech.smartlogiv2.dto.response.basic;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
@Data
public class ColisResponseBasic {
    private String id;
    private String description;
    private Double poids;
    private String statut;
    private String priorite;
    private String villeDestination;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss" , locale = "fr")
    private LocalDateTime dateCreation;
    private ClientDestinataireResponseBasic clientExpediteur;
    private ClientDestinataireResponseBasic destinataire;
    private ZoneResponse zone;
}
