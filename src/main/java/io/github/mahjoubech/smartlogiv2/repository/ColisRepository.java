package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColisRepository extends JpaRepository<Colis,String> {

    Page<Colis> findByStatut(ColisStatus statut, Pageable pageable);
    Page<Colis> findByLivreurId(String livreurId, Pageable pageable);

}

