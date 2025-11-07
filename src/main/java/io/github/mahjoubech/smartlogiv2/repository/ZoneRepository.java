package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, String> {
    @Query("SELECT z FROM Zone z WHERE z.codePostal = ?1")
    Optional<Zone> findByCodePostal(String codePostal);
}
