package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.LivreurResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface LivreurService {

    LivreurResponse createLivreur(LivreurRequest request);
    Page<LivreurResponse> getAllLivreurs(Pageable pageable);
    LivreurResponse getLivreurById(String livreurId);
    LivreurResponse updateLivreur(String livreurId, LivreurRequest request);
    void deleteLivreur(String livreurId);
    Page<ColisResponse> getAssignedColis( String livreurId, Pageable pageable);
    Page<LivreurColisResponse> getLivreurColisCounts(Pageable pageable);
    Page<LivreurResponse> searchLivreurs(String keyword, Pageable pageable);
}