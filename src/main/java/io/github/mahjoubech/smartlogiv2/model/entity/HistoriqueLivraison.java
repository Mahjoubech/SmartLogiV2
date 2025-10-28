package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;

import java.time.ZonedDateTime;

public class HistoriqueLivraison {
    private  String id;
    private  Colis colis;
    private ColisStatus status;
    private String commentaire;
    private ZonedDateTime dateChangement;
}
