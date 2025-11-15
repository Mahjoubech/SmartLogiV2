package io.github.mahjoubech.smartlogiv2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.*;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.mapper.HistoriqueLivraisonMapper;
import io.github.mahjoubech.smartlogiv2.mapper.ZoneMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.*;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import io.github.mahjoubech.smartlogiv2.repository.*;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import io.github.mahjoubech.smartlogiv2.service.EmailService;
import io.github.mahjoubech.smartlogiv2.specs.ColisSpecification;
import io.github.mahjoubech.smartlogiv2.utils.ColisProduitId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
    private final ZoneMapper zoneMapper;
    private final HistoriqueLivraisonRepository historiqueRepository;
    private final LivreurRepository livreurRepository;
    private final ProduitRepository produitRepository;
    private  final ColisProduitRepository colisProduitRepository;
    private  final HistoriqueLivraisonMapper historiqueLivraisonMapper;
    private final ColisMapper colisMapper;
    private final EmailService emailService;

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
        PrioriteStatus prioriteEnum = PrioriteStatus.valueOf(request.getPriorite().toUpperCase());

        List<Colis> existingColisList = colisRepository.findByClientExpediteurEmailAndDestinataireEmailAndPoidsAndStatusAndVilleDestinationAndPrioriteStatus(
                request.getPoids(),
                ColisStatus.CREE,
                request.getVilleDestination(),
                prioriteEnum
        );

        if (!existingColisList.isEmpty()) {
            throw new ConflictStateException("Un colis avec les mêmes détails ( Poids,  Priorité) existe déjà avec l'ID: " + existingColisList.get(0).getId());
        }

         ClientExpediteur expediteur = expediteurRepository.findByEmail(request.getClientExpediteurEmail())
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur", "ID", request.getClientExpediteurEmail()));

        Destinataire destinataire = destinataireRepository.findByEmail(request.getDestinataireEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire", "ID", request.getDestinataireEmail()));

        Zone zone = zoneRepository.findByCodePostal(request.getCodePostal())
                .orElseGet(() -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try (InputStream is = getClass().getResourceAsStream("/data/zone.json")) {
                        List<ZoneRequest> zonesList = Arrays.asList(mapper.readValue(is, ZoneRequest[].class));
                        ZoneRequest zr = zonesList.stream()
                                .filter(z -> z.getCodePostal().equals(request.getCodePostal()))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("Zone non trouvée", "codePostal", request.getCodePostal()));
                        Zone newZone = zoneMapper.toEntity(zr);
                        return zoneRepository.save(newZone);
                    } catch (Exception e) {
                        throw new RuntimeException("Erreur lecture JSON zones", e);
                    }
                });
        Colis colis = colisMapper.toEntity(request);
        colis.setClientExpediteur(expediteur);
        colis.setDestinataire(destinataire);
        colis.setZone(zone);
        colis.setStatus(ColisStatus.CREE);

        colis.setPrioriteStatus(PrioriteStatus.valueOf(request.getPriorite().toUpperCase()));

        Colis savedColis = colisRepository.save(colis);

        Set<ColisProduit> produitsSet = new HashSet<>();
        if (request.getProduits() != null && !request.getProduits().isEmpty()) {
            for (ProduitRequest produitRequest : request.getProduits()) {

                Produit produitEntity = produitRepository.findByNomIgnoreCase(produitRequest.getNom())
                        .orElseGet(() -> {
                            Produit newProduit = new Produit();
                            newProduit.setNom(produitRequest.getNom());
                            newProduit.setCategorie(produitRequest.getCategorie());
                            newProduit.setPoids(produitRequest.getPoids());
                            newProduit.setPrix(produitRequest.getPrix());
                            return produitRepository.save(newProduit);
                        });

                ColisProduit colisProduit = new ColisProduit();
                colisProduit.setColis(savedColis);
                colisProduit.setProduit(produitEntity);
                colisProduit.setQuantite(produitRequest.getColisProduit().getQuantite());
                colisProduit.setDateAjout(ZonedDateTime.now());

                BigDecimal prixTotal = produitEntity.getPrix().multiply(BigDecimal.valueOf(produitRequest.getColisProduit().getQuantite()));
                colisProduit.setPrixUnitaire(prixTotal);

                ColisProduitId id = new ColisProduitId();
                id.setColisId(savedColis.getId());
                id.setProduitId(produitEntity.getId());
                colisProduit.setColisProduitId(id);
                produitsSet.add(colisProduit);
            }
        }

        HistoriqueLivraison historique = createInitialHistory(colis, ColisStatus.CREE, "Demande de livraison créée par l'expéditeur.");
        Set<HistoriqueLivraison> historiqueSet = new HashSet<>();
        historiqueSet.add(historique);
        savedColis.setHistorique(historiqueSet);
        savedColis.setProduits(produitsSet);

        Colis finalSavedColis = colisRepository.save(savedColis);
        historiqueRepository.save(historique);

        return colisMapper.toResponse(finalSavedColis);
    }

    @Override
    public ColisResponse getColisById(String colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));
        return colisMapper.toResponse(colis);
    }
    @Override
    public Page<ColisResponseBasic> getAllColis(Pageable pageable) {
        Page<Colis> colisPage = colisRepository.findAll(pageable);
        return colisPage.map(colisMapper::toResponseBasic);
    }
    @Override
    @Transactional
public ColisResponse updateColis(String colisId, ColisRequest request) {

        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));

        if (colis.getStatus() != ColisStatus.CREE) {
            throw new ValidationException("Impossible de modifier les détails du colis. Le statut actuel est: " + colis.getStatus());
        }


        ClientExpediteur expediteur = expediteurRepository.findByEmail(request.getClientExpediteurEmail())
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur", "Email", request.getClientExpediteurEmail()));
        Destinataire destinataire = destinataireRepository.findByEmail(request.getDestinataireEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire", "Email", request.getDestinataireEmail()));

        Zone zone = zoneRepository.findByCodePostal(request.getCodePostal())
                .orElseGet(() -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try (InputStream is = getClass().getResourceAsStream("/data/zone.json")) {
                        List<ZoneRequest> zonesList = Arrays.asList(mapper.readValue(is, ZoneRequest[].class));
                        ZoneRequest zr = zonesList.stream()
                                .filter(z -> z.getCodePostal().equals(request.getCodePostal()))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("Zone non trouvée", "codePostal", request.getCodePostal()));
                        Zone newZone = zoneMapper.toEntity(zr);
                        return zoneRepository.save(newZone);
                    } catch (Exception e) {
                        throw new RuntimeException("Erreur lecture JSON zones", e);
                    }
                });
        colis.setClientExpediteur(expediteur);
        colis.setDestinataire(destinataire);
        colis.setZone(zone);
        colis.setDescription(request.getDescription());
        colis.setPoids(request.getPoids());

        try {
            colis.setPrioriteStatus(PrioriteStatus.valueOf(request.getPriorite().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Priorité invalide fournie: " + request.getPriorite());
        }

        Set<ColisProduit> produitsUpdated = new HashSet<>();

        if (request.getProduits() != null && !request.getProduits().isEmpty()) {
            for (ProduitRequest produitRequest : request.getProduits()) {

                Produit produitEntity = produitRepository.findByNomIgnoreCase(produitRequest.getNom())
                        .map(existingProduit -> {
                            existingProduit.setCategorie(produitRequest.getCategorie());
                            existingProduit.setPoids(produitRequest.getPoids());
                            existingProduit.setPrix(produitRequest.getPrix());
                            return produitRepository.save(existingProduit);
                        })
                        .orElseGet(() -> {
                            Produit newProduit = new Produit();
                            newProduit.setNom(produitRequest.getNom());
                            newProduit.setCategorie(produitRequest.getCategorie());
                            newProduit.setPoids(produitRequest.getPoids());
                            newProduit.setPrix(produitRequest.getPrix());
                            return produitRepository.save(newProduit);
                        });

                ColisProduitId associationId = new ColisProduitId();
                associationId.setColisId(colis.getId());
                associationId.setProduitId(produitEntity.getId());

                ColisProduit colisProduit = colisProduitRepository.findById(associationId)
                        .map(existingAssociation -> {
                            existingAssociation.setQuantite(produitRequest.getColisProduit().getQuantite());
                            return existingAssociation;
                        })
                        .orElseGet(() -> {
                            ColisProduit newAssociation = new ColisProduit();
                            newAssociation.setColis(colis);
                            newAssociation.setProduit(produitEntity);
                            newAssociation.setColisProduitId(associationId);
                            newAssociation.setQuantite(produitRequest.getColisProduit().getQuantite());
                            newAssociation.setDateAjout(ZonedDateTime.now());
                            return newAssociation;
                        });

                java.math.BigDecimal prixTotal = produitEntity.getPrix().multiply(java.math.BigDecimal.valueOf(colisProduit.getQuantite()));
                colisProduit.setPrixUnitaire(prixTotal);

                produitsUpdated.add(colisProduit);
            }
        }

        if (colis.getProduits() != null) {
            colis.getProduits().clear();
        }

        colis.getProduits().addAll(produitsUpdated);

        return colisMapper.toResponse(colisRepository.save(colis));
    }

    @Override
    @Transactional
    public void deleteColis(String colisId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));

        if (colis.getStatus() != ColisStatus.CREE) {
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
            Colis updatedColis = colisRepository.save(colis);
            String subject = "Mise à jour du colis " + colisId + ": Statut " + newStatus.name();
            String bodyDes = String.format(
                    "Bonjour Mr (%s),\n\nLe statut de votre colis (%s) (%s) a été mis à jour à: %s.\nCommentaire: %s",
                    updatedColis.getDestinataire().getNom(),updatedColis.getClientExpediteur().getPrenom()  ,colisId, newStatus.name(), statusRequest.getCommentaire()
            );
            String bodyExp = String.format(
                    "Bonjour Mr (%s),\n\nLe statut de votre colis (%s) (%s) a été mis à jour à: %s.\nCommentaire: %s",
                    updatedColis.getClientExpediteur().getNom() ,updatedColis.getClientExpediteur().getPrenom()  ,colisId, newStatus.name(), statusRequest.getCommentaire()
            );
            emailService.sendNotification(updatedColis.getDestinataire().getEmail(), subject, bodyDes);
            emailService.sendNotification(updatedColis.getClientExpediteur().getEmail(), subject, bodyExp);
           return colisMapper.toResponse(updatedColis);


        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide fourni: " + statusRequest.getStatut() + ". Valeurs possibles: " + ColisStatus.getAllowedValues());
        }
    }

    @Override
    @Transactional
    public Page<HistoriqueLivraisonResponse> getColisHistory(String colisId , Pageable pageable) {
        colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis", "ID", colisId));
        Page<HistoriqueLivraison> historiquePage = historiqueRepository.findByColisId(colisId, pageable);
        return historiquePage.map(historiqueLivraisonMapper::toResponse);
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
    @Transactional
    public Map<String, Long> getColisSummary(String groupByField) {
        if (groupByField == null || groupByField.isEmpty()) {
            throw new ValidationException("Le champ de regroupement (statut, zoneId, etc.) est obligatoire.");
        }

        List<Map<String, Object>> results;

        if ("status".equalsIgnoreCase(groupByField)) {
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
    @Transactional
    public ColisResponse assignColisToLivreur(String colisId, String livreurId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis not found with ID: " + colisId));

        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur not found with ID: " + livreurId));
        if (!colis.getZone().getId().equals(livreur.getZoneAssigned().getId())) {
            throw new ValidationException(
                    "Le colis (Zone: " +colis.getZone().getNom() + ") ne correspond pas à la zone assignée au livreur (" + livreur.getZoneAssigned().getNom() + ")."
            );
        }
        colis.setLivreur(livreur);
        if (colis.getStatus() == ColisStatus.CREE) {
            colis.setStatus(ColisStatus.COLLECTE);
        }

        return colisMapper.toResponse(colisRepository.save(colis));
    }
    @Override
    @Transactional
    public List<Map<String, Object>> getDetailedColisSummary(String groupByField) {
        if ("livreur".equalsIgnoreCase(groupByField)) {
            return colisRepository.calculateSummaryByLivreur();
        }
        if ("zone".equalsIgnoreCase(groupByField)) {
            return colisRepository.calculateSummaryByZone();
        }
        throw new ValidationException("Le regroupement par champ '" + groupByField + "' n'est pas supporté (livreur ou zone sont acceptés).");
    }

    @Override
    @Transactional
    public List<ColisResponse> getDelayedOrHighPriorityColis(ZonedDateTime dateLimiteCheck) {

        PrioriteStatus highPriority = PrioriteStatus.URGENT;
        List<Colis> delayedColis = colisRepository.findByPrioriteOrDelayed(highPriority, dateLimiteCheck);
        return delayedColis.stream()
                .map(colisMapper::toResponse)
                .collect(Collectors.toList());
    }
}