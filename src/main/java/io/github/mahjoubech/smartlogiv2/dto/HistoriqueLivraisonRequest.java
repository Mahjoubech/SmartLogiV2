package io.github.mahjoubech.smartlogiv2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HistoriqueLivraisonRequest {
    @NotBlank(message = "Le statut est obligatoire.")
    private String statut; // Bhal 'COLLECTE'

    private String commentaire;
}
