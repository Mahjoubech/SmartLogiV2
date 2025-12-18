package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
@Data
public class HistoriqueLivraisonResponse {
    private String id;
    private String statut;
    private String commentaire;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , locale = "fr")
    private LocalDateTime dateChangement;
}
