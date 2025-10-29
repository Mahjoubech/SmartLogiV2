package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, String> {
}
