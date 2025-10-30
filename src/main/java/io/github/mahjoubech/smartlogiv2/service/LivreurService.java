package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.LivreurResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface LivreurService {

    LivreurResponse createLivreur(LivreurRequest request);
    LivreurResponse getLivreurById(String livreurId);
    LivreurResponse updateLivreur(String livreurId, LivreurRequest request);

    // Gestionnaire: Assigner un colis à un livreur
    ColisResponse assignColisToLivreur(String colisId, String livreurId);

    Page<ColisResponse> getAssignedColis(String livreurId, Pageable pageable);

    // Rechercher un livreur par mot-clé (nom, téléphone, etc.)
    Page<LivreurResponse> searchLivreurs(String keyword, Pageable pageable);
}