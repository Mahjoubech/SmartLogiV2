package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, String> {
    @Query("select p from Produit p where p.nom = ?1")
    Optional<Produit> findByNomIgnoreCase(String nom);
}
