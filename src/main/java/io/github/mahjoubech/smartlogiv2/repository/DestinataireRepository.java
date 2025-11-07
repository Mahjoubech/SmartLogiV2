package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.ClientExpediteur;
import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import org.antlr.v4.runtime.atn.DecisionState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DestinataireRepository extends JpaRepository<Destinataire, String> {
    @Query("SELECT c FROM Destinataire c WHERE c.email = ?1")
    Optional<Destinataire> findByEmail(String email);
    Page<Destinataire> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
            String nomKeyword,
            String prenomKeyword,
            String emailKeyword,
            String phoneKeyword,
            Pageable pageable
    );
}
