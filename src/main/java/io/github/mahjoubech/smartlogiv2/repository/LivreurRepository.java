package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivreurRepository extends JpaRepository<Livreur, String> {
    Page<Livreur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String key, String k, Pageable p);
    @Query("""
    SELECT new io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse(
        CONCAT(l.nom, ' ', l.prenom),
        COUNT(c)
    )
    FROM Colis c
    JOIN c.livreur l
    GROUP BY CONCAT(l.nom, ' ', l.prenom)
""")
    Page<LivreurColisResponse> getColisEvryLivreur(Pageable pageable);


}
