package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.service.LivreurService;
import io.swagger.v3.oas.annotations.Parameter;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/api/v1/gestionner/livreur")
@RequiredArgsConstructor
@Tag(name = "Gestion des Livreurs", description = "Opérations CRUD, recherche et affectation des colis aux livreurs.")
public class LivreurController {

    private final LivreurService livreurService;
    @Operation(summary = "Enregistrer un nouveau livreur",
            description = "Ajoute un nouveau livreur au système et lui assigne une zone par défaut (à des fins de planification).",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Livreur créé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Erreur de validation des champs")
            })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping
    public ResponseEntity<LivreurResponse> createLivreur(@Valid @RequestBody LivreurRequest livreurRequest){
        LivreurResponse livreurResponse= livreurService.createLivreur(livreurRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(livreurResponse);
    }
    @Operation(summary = "Afficher la liste paginée de tous les livreurs",
            description = "Retourne la liste complète des livreurs avec des options de tri et de pagination.",
            responses = {@ApiResponse(responseCode = "200", description = "Liste paginée des livreurs retournée")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping
    public ResponseEntity<Page<LivreurResponse>> getAllLivreurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LivreurResponse> livreurResponses = livreurService.getAllLivreurs(pageable);
        return ResponseEntity.ok().body(livreurResponses);
    }

    @Operation(summary = "Consulter les détails d'un livreur par ID",
            description = "Recherche un livreur par son ID unique.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Détails du livreur trouvés"),
                    @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
            })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/{livreur_id}")
    public ResponseEntity<LivreurResponse> getLivreurById(
            @Parameter(description = "ID unique du livreur") @PathVariable("livreur_id") String livreurId){
        LivreurResponse livreurResponse= livreurService.getLivreurById(livreurId);
        return ResponseEntity.ok().body(livreurResponse);
    }

    @Operation(summary = "Mettre à jour les informations d'un livreur",
            description = "Permet de corriger les informations du livreur (Nom, Prénom, Véhicule, Zone Assignée).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mise à jour réussie"),
                    @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
            })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("/{livreur_id}")
    public ResponseEntity<LivreurResponse> updateLivreur(@PathVariable("livreur_id") String livreurId,
                                                         @Valid @RequestBody LivreurRequest livreurRequest){
        LivreurResponse livreurResponse= livreurService.updateLivreur(livreurId, livreurRequest);
        return ResponseEntity.ok().body(livreurResponse);
    }

    @Operation(summary = "Supprimer un livreur",
            description = "Supprime un livreur par ID. Opération bloquée si le livreur est lié à des colis (Contrainte FK).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Suppression réussie"),
                    @ApiResponse(responseCode = "404", description = "Livreur non trouvé"),
                    @ApiResponse(responseCode = "409", description = "Conflit: Livreur lié à des colis")
            })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @DeleteMapping("/{livreur_id}")
    public ResponseEntity<String> deleteLivreur(@PathVariable("livreur_id") String livre){
        livreurService.deleteLivreur(livre);
        String message = "Livreur deleted successfully with ID: " + livre;
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Rechercher des livreurs par mot-clé",
            description = "Recherche des livreurs par Nom, Prénom, ou Téléphone (recherche unifiée).",
            responses = {@ApiResponse(responseCode = "200", description = "Résultats de la recherche paginés")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<Page<LivreurResponse>> searchLivreurs(
            @Parameter(description = "Mot-clé pour la recherche par Nom, Prénom, ou Téléphone.") @RequestParam(required = false) String keyword,
            Pageable pageable) {

        Page<LivreurResponse> result = livreurService.searchLivreurs(keyword == null ? "" : keyword, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Consulter le nombre de colis assignés par livreur",
            description = "Opération Gestionnaire: Retourne la liste des livreurs avec le compte des colis qui leur sont affectés (utile pour l'équilibrage des tournées).",
            responses = {@ApiResponse(responseCode = "200", description = "Liste des livreurs avec leurs comptes de colis")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/counts")
    public ResponseEntity<Page<LivreurColisResponse>> getLivreurColisCounts(Pageable pageable) {
        Page<LivreurColisResponse> result = livreurService.getLivreurColisCounts(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Afficher les colis assignés à un livreur spécifique",
            description = "Utilisé par l'application livreur pour visualiser sa tournée (Colis à collecter ou livrer).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste paginée des colis assignés"),
                    @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
            })
    @PreAuthorize("hasAnyRole('MANAGER','LIVREUR')")
    @GetMapping("/{livreurId}/colis")
    public ResponseEntity<Page<ColisResponse>> getAssignedColis(
            @PathVariable String livreurId,
            Pageable pageable) {
        Page<ColisResponse> colisPage = livreurService.getAssignedColis(livreurId, pageable);
        return ResponseEntity.ok(colisPage);
    }
}