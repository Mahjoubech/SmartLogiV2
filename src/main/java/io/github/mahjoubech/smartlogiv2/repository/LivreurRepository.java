package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivreurRepository extends JpaRepository<Livreur, String> {
    Page<Livreur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String key, String k, Pageable p);
}
