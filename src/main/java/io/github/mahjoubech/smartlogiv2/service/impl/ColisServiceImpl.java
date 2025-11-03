package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.*;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import io.github.mahjoubech.smartlogiv2.repository.*;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import io.github.mahjoubech.smartlogiv2.specs.ColisSpecification;
import io.github.mahjoubech.smartlogiv2.utils.ColisProduitId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ProduitRepository produitRepository;

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
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur", "ID", request.getClientExpediteurId()));

        Destinataire destinataire = destinataireRepository.findById(request.getDestinataireId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire", "ID", request.getDestinataireId()));
        Livreur livreur = livreurRepository.findById(request.getLivreurId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire", "ID", request.getDestinataireId()));
        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "ID", request.getZoneId()));

        Colis colis = colisMapper.toEntity(request);
        colis.setClientExpediteur(expediteur);
        colis.setDestinataire(destinataire);
        colis.setZone(zone);
        colis.setLivreur(livreur);
        colis.setStatus(ColisStatus.CREE);

        Set<ColisProduit> produitsSet = new HashSet<>();

        for (ColisProduitRequest p : request.getProduits()) {
            Produit produit = produitRepository.findById(p.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec id: " + p.getProduitId()));
            ColisProduit colisProduit = new ColisProduit();
            ColisProduitId id = new ColisProduitId();
            id.setColisId(colis.getId());
            id.setProduitId(produit.getId());
            colisProduit.setColisProduitId(id);
            colisProduit.setProduit(produit);
            BigDecimal prixUnitaire = produit.getPrix().multiply(BigDecimal.valueOf(p.getQuantite()));
            colisProduit.setPrixUnitaire(prixUnitaire);
            colisProduit.setQuantite(p.getQuantite());
            colisProduit.setColis(colis);
            produitsSet.add(colisProduit);
        }
        colis.setProduits(produitsSet);
        colis.setPrioriteStatus(request.getPriorite().equalsIgnoreCase("URGENT") ? PrioriteStatus.URGENT : PrioriteStatus.NORMAL);
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
    public List<ColisResponse> getAllColis() {
        List<Colis> colisList = colisRepository.findAll();
        List<ColisResponse> responseList = new ArrayList<>();
        for (Colis colis : colisList) {
            responseList.add(colisMapper.toResponse(colis));
        }
        return responseList;
    }

    @Override
    @Transactional
    public ColisResponse updateColis(String colisId, ColisRequest request) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));
        if (!colis.getClientExpediteur().getId().equals(request.getClientExpediteurId())) {
            ClientExpediteur newExpediteur = expediteurRepository.findById(request.getClientExpediteurId())
                    .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur", "ID", request.getClientExpediteurId()));
            colis.setClientExpediteur(newExpediteur);
        }

        if (!colis.getDestinataire().getId().equals(request.getDestinataireId())) {
            Destinataire newDestinataire = destinataireRepository.findById(request.getDestinataireId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destinataire", "ID", request.getDestinataireId()));
            colis.setDestinataire(newDestinataire);
        }

        if (!colis.getZone().getId().equals(request.getZoneId())) {
            Zone newZone = zoneRepository.findById(request.getZoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zone", "ID", request.getZoneId()));
            colis.setZone(newZone);
        }
        if (request.getLivreurId() != null) {
            if (colis.getLivreur() == null || !colis.getLivreur().getId().equals(request.getLivreurId())) {
                Livreur newLivreur = livreurRepository.findById(request.getLivreurId())
                        .orElseThrow(() -> new ResourceNotFoundException("Livreur", "ID", request.getLivreurId()));
                colis.setLivreur(newLivreur);
            }
        } else {
            colis.setLivreur(null);
        }

        colis.setDescription(request.getDescription());
        colis.setPoids(request.getPoids());
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

            if (colis.getStatus() == ColisStatus.LIVRE || colis.getStatus() == ColisStatus.ANNULE) {
                throw new ValidationException("Impossible de modifier le statut d'un colis déjà terminé (LIVRE ou ANNULE).");
            }
            if (newStatus == ColisStatus.COLLECTE || newStatus == ColisStatus.EN_TRANSIT || newStatus == ColisStatus.LIVRE) {
                if (colis.getLivreur() == null) {
                    throw new ValidationException("Impossible de mettre le statut " + newStatus + " car aucun Livreur n'est assigné à ce colis.");
                }
            }

            colis.setStatus(newStatus);

            HistoriqueLivraison newHistory = createInitialHistory(colis, newStatus, statusRequest.getCommentaire());
            historiqueRepository.save(newHistory);

            return colisMapper.toResponse(colisRepository.save(colis));

        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide fourni: " + statusRequest.getStatut() + ". Valeurs possibles: " + ColisStatus.getAllowedValues());
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

        Specification<Colis> spec = Specification.not(null);

        if (statut != null && !statut.isEmpty()) {
            try {
                ColisStatus enumStatut = ColisStatus.valueOf(statut.toUpperCase());
                spec = spec.and(ColisSpecification.hasStatut(enumStatut));            } catch (IllegalArgumentException e) {
                throw new ValidationException("Le statut fourni dans la recherche est invalide: " + statut);
            }
        }
        if (zoneId != null && !zoneId.isEmpty()) {
            spec = spec.and(ColisSpecification.hasZoneId(zoneId));
        }

        if (ville != null && !ville.isEmpty()) {
            spec = spec.and(ColisSpecification.hasVilleDestination(ville));
        }

        if (priorite != null && !priorite.isEmpty()) {
            try {
                PrioriteStatus enumPriorite = PrioriteStatus.valueOf(priorite.toUpperCase());
                spec = spec.and(ColisSpecification.hasPriorite(enumPriorite));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("La priorité fournie dans la recherche est invalide: " + priorite);
            }
        }

        Page<Colis> colisPage = colisRepository.findAll(spec, pageable);

        return colisPage.map(colisMapper::toResponse);
    }

    @Override
    public Page<ColisResponse> findByExpediteur(String expediteurId, Pageable pageable) {
        expediteurRepository.findById(expediteurId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur", "ID", expediteurId));

        Page<Colis> colisPage = colisRepository.findByClientExpediteurId(expediteurId, pageable);

        return colisPage.map(colisMapper::toResponse);
    }

    @Override
    public Page<ColisResponse> findByDestinataire(String destinataireId, Pageable pageable) {
        destinataireRepository.findById(destinataireId)
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire", "ID", destinataireId));

        Page<Colis> colisPage = colisRepository.findByDestinataireId(destinataireId, pageable);

        return colisPage.map(colisMapper::toResponse);
    }

    @Override
    public Map<String, Long> getColisSummary(String groupByField) {
        if (groupByField == null || groupByField.isEmpty()) {
            throw new ValidationException("Le champ de regroupement (statut, zoneId, etc.) est obligatoire.");
        }

        List<Map<String, Object>> results;

        if ("statut".equalsIgnoreCase(groupByField)) {
            results = colisRepository.countColisByStatut();
        } else {
            throw new ValidationException("Le regroupement par champ '" + groupByField + "' n'est pas supporté.");
        }

        return results.stream()
                .collect(Collectors.toMap(
                        map -> map.get(groupByField).toString(),
                        map -> (Long) map.get("count")
                ));
    }

    @Override
    public List<ColisResponse> getDelayedOrHighPriorityColis() {
        ZonedDateTime dateLimite = ZonedDateTime.now().minusHours(48);

        PrioriteStatus highPriority = PrioriteStatus.URGENT;

        List<Colis> priorityColis = colisRepository.findByPrioriteOrDelayed(highPriority, dateLimite);

        return priorityColis.stream()
                .map(colisMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateTotalWeightByZone(String zoneId) {
        zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "ID", zoneId));

        Double totalWeight = colisRepository.sumPoidsByZoneId(zoneId);

        return totalWeight != null ? totalWeight : 0.0;
    }
}