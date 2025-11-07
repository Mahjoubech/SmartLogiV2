package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.dto.response.detail.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.HistoriqueLivraison;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HistoriqueLivraisonRepository extends JpaRepository<HistoriqueLivraison, String> {
    @Query("select h from HistoriqueLivraison h where h.colis.id = ?1 order by h.dateChangement desc")
    Page<HistoriqueLivraison> findByColisId(String colisId, Pageable pageable);
}
