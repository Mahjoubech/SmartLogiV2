package io.github.mahjoubech.smartlogiv2.model.entity;

import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;

import java.util.Date;

public class Colis {
    private String id;
    private String description;
    private double weight;
    private ColisStatus status;
    private PrioriteStatus prioriteStatus;
    private  String villeDestination;
    private Date dateCreation;
    private ClientExpediteur clientExpediteur;
    private Destinataire destinataire;
    private Livreur livreur;
    private Zone zone;
}
