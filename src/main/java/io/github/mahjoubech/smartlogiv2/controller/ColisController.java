package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/api/v2/colis")
@RequiredArgsConstructor
@Tag(name = "Gestion des Colis", description = "Opérations de création, suivi et logistique avancée des colis.")
public class ColisController {
    private final ColisService colisService;

    @Operation(summary = "Créer une nouvelle demande de livraison",
            description = "Permet d'enregistrer un colis. La méthode gère l'upsert des produits et la validation des clients/zones.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Colis créé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides ou statut de priorité incorrect"),
                    @ApiResponse(responseCode = "404", description = "Client/Destinataire/Zone non trouvé")
            })
    @PreAuthorize("hasAnyRole('CLIENT')")
    @PostMapping
    public ResponseEntity<ColisResponse> createColis(@Valid @RequestBody ColisRequest request) {
        ColisResponse response = colisService.createDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Afficher la liste paginée de tous les colis (Vue Basique)",
            description = "Retourne une liste allégée des colis, principalement pour les gestionnaires.",
            responses = {@ApiResponse(responseCode = "200", description = "Liste paginée des colis")})
    @GetMapping
    public ResponseEntity<Page<ColisResponseBasic>> getAllColis(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "dateCreation") String sortBy,
                                                                @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page , size, sort);
        Page<ColisResponseBasic> colisPage = colisService.getAllColis(pageable);
        return ResponseEntity.ok().body(colisPage);
    }

    @Operation(summary = "Consulter les détails complets d'un colis",
            description = "Retourne l'ensemble des informations du colis, y compris les détails des clients et les produits.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Colis trouvé"),
                    @ApiResponse(responseCode = "404", description = "Colis non trouvé")
            })
    @PreAuthorize("hasAnyRole('CLIENT')")

    @GetMapping("/{colisId}")
    public ResponseEntity<ColisResponse> getColisById(@PathVariable String colisId) {
        ColisResponse response = colisService.getColisById(colisId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Corriger les détails d'un colis",
            description = "Permet au gestionnaire de mettre à jour les informations de base (Poids, Description, Clients) d'un colis non encore en transit (Statut CREE).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mise à jour réussie"),
                    @ApiResponse(responseCode = "400", description = "Colis déjà en cours de traitement"),
                    @ApiResponse(responseCode = "404", description = "Colis non trouvé")
            })
    @PreAuthorize("hasAnyRole('CLIENT')")
    @PutMapping("/{colisId}")
    public ResponseEntity<ColisResponse> updateColis(
            @PathVariable String colisId,
            @Valid @RequestBody ColisRequest request) {
        ColisResponse response = colisService.updateColis(colisId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Supprimer définitivement un colis",
            description = "Supprime un colis. L'opération n'est autorisée que si le colis est au statut CREE.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Suppression réussie"),
                    @ApiResponse(responseCode = "400", description = "Statut ne permettant pas la suppression"),
                    @ApiResponse(responseCode = "404", description = "Colis non trouvé")
            })
    @PreAuthorize("hasAnyRole('CLIENT')")
    @DeleteMapping("/{colisId}")
    public ResponseEntity<String> deleteColis(@PathVariable String colisId) {
        colisService.deleteColis(colisId);
        String message = "Colis deleted successfully with ID: " + colisId;
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Mettre à jour le statut du colis",
            description = "Met à jour le statut (COLLECTE, EN_TRANSIT, LIVRE). Déclenche les notifications Email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statut mis à jour (et notifications envoyées)"),
                    @ApiResponse(responseCode = "400", description = "Statut invalide ou colis déjà terminé")
            })
    @PreAuthorize("hasAnyRole('MANAGER','LIVREUR')")
    @PutMapping("/{colisId}/status")
    public ResponseEntity<ColisResponse> updateColisStatus(
            @PathVariable String colisId,
            @Valid @RequestBody HistoriqueLivraisonRequest statusRequest) {
        ColisResponse response = colisService.updateColisStatus(colisId, statusRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Assigner un colis à un livreur",
            description = "Opération Gestionnaire: Permet d'affecter un colis à un Livreur. Déclenche le statut COLLECTE si le colis est au statut CREE.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Affectation réussie"),
                    @ApiResponse(responseCode = "404", description = "Colis ou Livreur non trouvé"),
                    @ApiResponse(responseCode = "400", description = "La zone du colis ne correspond pas à la zone du livreur.")
            })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("gestionner/livreur/{colisId}/assign")
    public ResponseEntity<ColisResponse> assignColisToLivreur(
            @PathVariable String colisId,
            @RequestParam String livreurId) {

        ColisResponse updatedColis = colisService.assignColisToLivreur(colisId, livreurId);
        return ResponseEntity.ok(updatedColis);
    }

    @Operation(summary = "Filtrage et Recherche avancée",
            description = "Recherche et pagine les colis par Statut, Zone, Ville ou Priorité.",
            responses = {@ApiResponse(responseCode = "200", description = "Résultats du filtre paginés")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<Page<ColisResponse>> findColisByCriteria(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String zoneId,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String priorite,
            Pageable pageable)
    {
        Page<ColisResponse> responsePage = colisService.findColisByCriteria(statut, zoneId, ville, priorite, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Consulter les colis d'un expéditeur",
            description = "Utilisé par le client pour suivre l'état de ses envois.",
            responses = {@ApiResponse(responseCode = "200", description = "Liste des colis de l'expéditeur")})
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/expediteur/{expediteurId}")
    public ResponseEntity<Page<ColisResponse>> getColisByExpediteur(
            @PathVariable String expediteurId,
            Pageable pageable)
    {
        Page<ColisResponse> responsePage = colisService.findByExpediteur(expediteurId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Consulter l'historique complet d'un colis",
            description = "Utilisé pour la traçabilité: retourne toutes les étapes (statuts et commentaires) d'un colis donné.",
            responses = {@ApiResponse(responseCode = "200", description = "Historique retourné")})
    @PreAuthorize("hasAnyRole('MANAGER','CLIENT')")
    @GetMapping("/{colisId}/history")
    public ResponseEntity<Page<HistoriqueLivraisonResponse>> getColisHistory(@PathVariable String colisId , Pageable pageable) {
        Page<HistoriqueLivraisonResponse> history = colisService.getColisHistory(colisId , pageable);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Rapport de synthèse des colis par regroupement",
            description = "Opération Gestionnaire: Calcule le nombre total de colis regroupés par statut ou par zone.",
            responses = {@ApiResponse(responseCode = "200", description = "Synthèse des colis par groupe")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getColisSummary(@RequestParam String groupByField) {
        Map<String, Long> summary = colisService.getColisSummary(groupByField);
        return ResponseEntity.ok(summary);
    }
}