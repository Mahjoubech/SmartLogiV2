package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ColisService {
    ColisResponse createDelivery(ColisRequest request);
    Page<ColisResponseBasic> getAllColis(Pageable pageable);
    ColisResponse getColisById(String colisId);
    ColisResponse updateColis(String colisId, ColisRequest request);
    void deleteColis(String colisId);
    ColisResponse updateColisStatus(String colisId, HistoriqueLivraisonRequest statusRequest);
    Page<HistoriqueLivraisonResponse> getColisHistory(String colisId , Pageable pageable);    ColisResponse assignColisToLivreur(String colisId, String livreurId);
    Page<ColisResponse> findColisByCriteria(String statut, String zoneId, String ville, String priorite, Pageable pageable);
    Page<ColisResponse> findByExpediteur(String expediteurId, Pageable pageable);
    Page<ColisResponse> findByDestinataire(String destinataireId, Pageable pageable);

    Map<String, Long> getColisSummary(String groupByField);
    List<ColisResponse> getDelayedOrHighPriorityColis();

    Double calculateTotalWeightByZone(String zoneId);
}
