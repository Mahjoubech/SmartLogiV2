package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.ClientExpediteur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientExpediteurRepository extends JpaRepository<ClientExpediteur, String> {
    Page<ClientExpediteur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);

    @Query("SELECT c FROM ClientExpediteur c WHERE c.email = ?1")
    Optional<ClientExpediteur> findByEmail(String email);
    Page<ClientExpediteur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
            String nomKeyword,
            String prenomKeyword,
            String emailKeyword,
            String phoneKeyword,
            Pageable pageable
    );
}
