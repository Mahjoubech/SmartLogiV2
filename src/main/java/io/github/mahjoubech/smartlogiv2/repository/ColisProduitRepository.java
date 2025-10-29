package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.ColisProduit;
import io.github.mahjoubech.smartlogiv2.utils.ColisProduitId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColisProduitRepository extends JpaRepository<ColisProduit, ColisProduitId>{

}
