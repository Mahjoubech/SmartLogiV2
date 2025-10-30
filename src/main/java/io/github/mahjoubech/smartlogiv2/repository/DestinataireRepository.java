package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import org.antlr.v4.runtime.atn.DecisionState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinataireRepository extends JpaRepository<Destinataire, String> {
}
