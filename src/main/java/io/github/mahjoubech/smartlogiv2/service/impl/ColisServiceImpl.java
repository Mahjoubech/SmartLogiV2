package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.*;
import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.repository.*;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        historique.setStatus(statut);
        historique.setDateChangement(ZonedDateTime.now());
        historique.setCommentaire(commentaire);
        return historique;
    }

    @Override
    @Transactional
    public ColisResponse createDelivery(ColisRequest request) {
        ClientExpediteur expediteur = expediteurRepository.findById(request.getClientExpediteurId())
                .orElseThrow(() -> new EntityNotFoundException("ClientExpediteur not found with ID: " + request.getClientExpediteurId()));

        Destinataire destinataire = destinataireRepository.findById(request.getDestinataireId())
                .orElseThrow(() -> new EntityNotFoundException("Destinataire not found with ID: " + request.getDestinataireId()));

        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new EntityNotFoundException("Zone not found with ID: " + request.getZoneId()));
        Colis colis = colisMapper.toEntity(request);
        colis.setClientExpediteur(expediteur);
        colis.setDestinataire(destinataire);
        colis.setZone(zone);
        colis.setStatus(ColisStatus.CREE);
        HistoriqueLivraison historique = createInitialHistory(colis, ColisStatus.CREE, "Demande de livraison créée par l'expéditeur.");
        colis.setHistorique(Collections.singleton(historique));

        Colis savedColis = colisRepository.save(colis);
        historiqueRepository.save(historique);

        return colisMapper.toResponse(savedColis);
    }

    @Override
    public ColisResponse getColisById(String colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé: " + colisId));
        return colisMapper.toResponse(colis);
    }

    @Override
    public ColisResponse updateColis(String colisId, ColisRequest request) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé: " + colisId));
        colis.setDescription(request.getDescription());
        colis.setWeight(request.getPoids());
        if (!colis.getClientExpediteur().getId().equals(request.getClientExpediteurId())) {
            colis.setClientExpediteur(expediteurRepository.findById(request.getClientExpediteurId())
                    .orElseThrow(() -> new RuntimeException("ClientExpediteur not found")));
        }

        return colisMapper.toResponse(colisRepository.save(colis));
    }

    @Override
    public void deleteColis(String colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé: " + colisId));

        colisRepository.delete(colis);
    }

    @Override
    @Transactional
    public ColisResponse updateColisStatus(String colisId, HistoriqueLivraisonRequest statusRequest) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé: " + colisId));

        try {
            ColisStatus newStatus = ColisStatus.valueOf(statusRequest.getStatut().toUpperCase());

            if (colis.getStatus() == ColisStatus.LIVRE || colis.getStatus() == ColisStatus.ANNULE) {
                throw new RuntimeException("Impossible de modifier le statut d'un colis terminé.");
            }

            colis.setStatus(newStatus);

            HistoriqueLivraison newHistory = createInitialHistory(colis, newStatus, statusRequest.getCommentaire());
            historiqueRepository.save(newHistory);

            return colisMapper.toResponse(colisRepository.save(colis));

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide fourni: " + statusRequest.getStatut());
        }
    }

    @Override
    public List<HistoriqueLivraisonResponse> getColisHistory(String colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé: " + colisId));

        return Collections.emptyList();
    }

    @Override
    public Page<ColisResponse> findColisByCriteria(String statut, String zoneId, String ville, String priorite, Pageable pageable) {
        Page<Colis> colisPage = colisRepository.findByStatut(ColisStatus.valueOf(statut.toUpperCase()), pageable);

        return Page.empty();
    }

    @Override
    public Page<ColisResponse> findByExpediteur(String expediteurId, Pageable pageable) {
        return Page.empty();
    }


    @Override
    public Page<ColisResponse> findByDestinataire(String destinataireId, Pageable pageable) {
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