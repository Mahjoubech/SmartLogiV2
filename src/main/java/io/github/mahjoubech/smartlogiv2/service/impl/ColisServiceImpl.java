package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.*;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.repository.*;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
public class ColisServiceImpl implements ColisService {

    private final ColisRepository colisRepository;
    private final ClientExpediteurRepository expediteurRepository;
    private final DestinataireRepository destinataireRepository;
    private final ZoneRepository zoneRepository;
    private final HistoriqueLivraisonRepository historiqueRepository;
    private final LivreurRepository livreurRepository;

    private final ColisMapper colisMapper;

    private HistoriqueLivraison createInitialHistory(Colis colis, ColisStatus statut, String commentaire) {
        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setColis(colis);
        historique.setStatus(statut); // ✅ Correction dyal setStatut l'setStatus f'l'Entity
        historique.setDateChangement(ZonedDateTime.now());
        historique.setCommentaire(commentaire);
        return historique;
    }

    @Override
    @Transactional
    public ColisResponse createDelivery(ColisRequest request) {
        ClientExpediteur expediteur = expediteurRepository.findById(request.getClientExpediteurId())
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur", "ID", request.getClientExpediteurId()));

        Destinataire destinataire = destinataireRepository.findById(request.getDestinataireId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire", "ID", request.getDestinataireId()));

        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "ID", request.getZoneId()));

        Colis colis = colisMapper.toEntity(request);
        colis.setClientExpediteur(expediteur);
        colis.setDestinataire(destinataire);
        colis.setZone(zone);
        colis.setStatus(ColisStatus.CREE); // ✅ Correction dyal setStatut l'setStatus

        HistoriqueLivraison historique = createInitialHistory(colis, ColisStatus.CREE, "Demande de livraison créée par l'expéditeur.");
        colis.setHistorique(Collections.singleton(historique));

        Colis savedColis = colisRepository.save(colis);
        historiqueRepository.save(historique);

        return colisMapper.toResponse(savedColis);
    }

    @Override
    public ColisResponse getColisById(String colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));
        return colisMapper.toResponse(colis);
    }

    @Override
    @Transactional
    public ColisResponse updateColis(String colisId, ColisRequest request) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));

        colis.setDescription(request.getDescription());
        colis.setWeight(request.getPoids());

        if (!colis.getClientExpediteur().getId().equals(request.getClientExpediteurId())) {
            ClientExpediteur newExpediteur = expediteurRepository.findById(request.getClientExpediteurId())
                    .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur", "ID", request.getClientExpediteurId()));
            colis.setClientExpediteur(newExpediteur);
        }

        return colisMapper.toResponse(colisRepository.save(colis));
    }

    @Override
    @Transactional
    public void deleteColis(String colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));

        if (colis.getStatus() != ColisStatus.CREE) { // getStatut rahou khaddam 3la l'Entity
            throw new ValidationException("Impossible de supprimer un colis qui n'est pas au statut CREE.");
        }

        colisRepository.delete(colis);
    }

    @Override
    @Transactional
    public ColisResponse updateColisStatus(String colisId, HistoriqueLivraisonRequest statusRequest) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));

        try {
            ColisStatus newStatus = ColisStatus.valueOf(statusRequest.getStatut().toUpperCase());

            // GESTION dyal l'Mantiq (ValidationException)
            if (colis.getStatus() == ColisStatus.LIVRE || colis.getStatus() == ColisStatus.ANNULE) {
                throw new ValidationException("Impossible de modifier le statut d'un colis terminé.");
            }

            colis.setStatus(newStatus); // ✅ Correction dyal setStatut l'setStatus

            HistoriqueLivraison newHistory = createInitialHistory(colis, newStatus, statusRequest.getCommentaire());
            historiqueRepository.save(newHistory);
            return colisMapper.toResponse(colisRepository.save(colis));

        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide fourni: " + statusRequest.getStatut());
        }
    }

    @Override
    public List<HistoriqueLivraisonResponse> getColisHistory(String colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));

        return Collections.emptyList();
    }

    @Override
    public Page<ColisResponse> findColisByCriteria(String statut, String zoneId, String ville, String priorite, Pageable pageable) {
        try {
            ColisStatus enumStatut = ColisStatus.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Le statut fourni dans la recherche est invalide: " + statut);
        }

        return Page.empty();
    }

    @Override
    public Page<ColisResponse> findByExpediteur(String expediteurId, Pageable pageable) {
        expediteurRepository.findById(expediteurId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur", "ID", expediteurId));

        return Page.empty();
    }

    @Override
    public Page<ColisResponse> findByDestinataire(String destinataireId, Pageable pageable) {
        destinataireRepository.findById(destinataireId)
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire", "ID", destinataireId));
        return Page.empty();
    }

    @Override
    public Map<String, Long> getColisSummary(String groupByField) {
        return Collections.emptyMap();
    }

    @Override
    public List<ColisResponse> getDelayedOrHighPriorityColis() {
        return Collections.emptyList();
    }

    @Override
    public Double calculateTotalWeightByZone(String zoneId) {
        return 0.0;
    }
}