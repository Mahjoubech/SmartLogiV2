package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import io.github.mahjoubech.smartlogiv2.service.LogisticsDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v4/gestion")
@RequiredArgsConstructor
@Tag(name = "Gestion des Données Logistiques (Admin)", description = "Opérations CRUD sur les Zones, Catalogue des Produits et Rapports d'optimisation.")
public class LogisticsDataController {
    private final LogisticsDataService logisticsDataService;
    private final ColisService colisService;

    @Operation(summary = "Créer une nouvelle zone de distribution",
            description = "Ajoute une zone de distribution pour la planification des tournées.",
            responses = {@ApiResponse(responseCode = "201", description = "Zone créée")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/zone")
    public ResponseEntity<ZoneResponse> createZone(@Valid @RequestBody ZoneRequest request){
        ZoneResponse zoneResponse= logisticsDataService.createZone(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(zoneResponse);
    }

    @Operation(summary = "Mettre à jour les détails d'une zone",
            description = "Permet de modifier le nom ou le code postal d'une zone existante.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mise à jour réussie"),
                    @ApiResponse(responseCode = "404", description = "Zone non trouvée")
            })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("/zone/{zoneId}")
    public ResponseEntity<ZoneResponse> updateZone(
            @Parameter(description = "ID de la zone à mettre à jour") @PathVariable String zoneId,
            @Valid @RequestBody ZoneRequest request){
        ZoneResponse zoneResponse= logisticsDataService.updateZone(zoneId,request);
        return ResponseEntity.ok(zoneResponse);
    }

    @Operation(summary = "Supprimer une zone de distribution",
            description = "Supprime une zone par son ID. Bloqué si la zone est encore assignée à des colis ou livreurs.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zone supprimée"),
                    @ApiResponse(responseCode = "404", description = "Zone non trouvée"),
                    @ApiResponse(responseCode = "409", description = "Conflit: Zone encore liée")
            })
    @PreAuthorize("hasAnyRole('MANAGER')")
    @DeleteMapping("/zone/{zoneId}")
    public ResponseEntity<String> deleteZone(@PathVariable String zoneId){
        logisticsDataService.deleteZone(zoneId);
        return ResponseEntity.ok("Zone deleted successfully");
    }

    @Operation(summary = "Consulter les détails d'une zone par ID")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/zone/{zoneId}")
    public  ResponseEntity<ZoneResponse> getZone(@PathVariable String zoneId){
        ZoneResponse zoneResponse= logisticsDataService.getZoneById(zoneId);
        return ResponseEntity.ok(zoneResponse);
    }

    @Operation(summary = "Afficher toutes les zones",
            description = "Retourne la liste paginée de toutes les zones de distribution.")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/zone")
    public ResponseEntity<Page<ZoneResponse>> getAllZones(Pageable pageable){
        Page<ZoneResponse> zoneResponses= logisticsDataService.getAllZones(pageable);
        return ResponseEntity.ok(zoneResponses);

    }
    @Operation(summary = "Créer un produit pour le catalogue",
            description = "Ajoute un nouveau produit au catalogue général de SmartLogi.",
            responses = {@ApiResponse(responseCode = "201", description = "Produit créé")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/produit")
    public ResponseEntity<ProduitResponse> createProduit(@Valid @RequestBody ProduitRequest request) {
        ProduitResponse response = logisticsDataService.createProduit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Consulter un produit par ID")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/produit/{produitId}")
    public ResponseEntity<ProduitResponse> getProduitById(@PathVariable String produitId) {
        ProduitResponse response = logisticsDataService.getProduitById(produitId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Afficher le catalogue complet des produits")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/produit")
    public ResponseEntity<Page<ProduitResponse>> findAllProduits(Pageable pageable) {
        Page<ProduitResponse> responsePage = logisticsDataService.findAllProduits(pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "Mettre à jour un produit existant",
            description = "Corrige le prix, le poids ou la catégorie d'un produit.")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("/produit/{produitId}")
    public ResponseEntity<ProduitResponse> updateProduit(
            @PathVariable String produitId,
            @Valid @RequestBody ProduitRequest request) {
        ProduitResponse response = logisticsDataService.updateProduit(produitId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Supprimer un produit du catalogue",
            description = "Supprime un produit. Bloqué si le produit est lié à un colis existant.",
            responses = {@ApiResponse(responseCode = "204", description = "Produit supprimé")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @DeleteMapping("/produit{produitId}")
    public ResponseEntity<Void> deleteProduit(@PathVariable String produitId) {
        logisticsDataService.deleteProduit(produitId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Nettoyage des produits dupliqués",
            description = "Tâche d'administration: Supprime les produits qui partagent le même nom mais ont des ID différents (corrige les problèmes de base de données).")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/produit/cleanup/duplicates")
    public ResponseEntity<String> deleteDuplicateProducts() {
        logisticsDataService.deleteDuplicateProducts();
        return ResponseEntity.ok("Nettoyage des produits dupliqués effectué avec succès.");
    }

    @Operation(summary = "Rapport de synthèse des colis par statut ou zone",
            description = "Opération Gestionnaire: Calcule le nombre total de colis regroupés par le champ spécifié (statut ou zoneId).")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/colis/summary")
    public ResponseEntity<Map<String, Long>> getColisSummary(
            @Parameter(description = "Champ de regroupement: 'statut' ou 'zone'") @RequestParam String groupByField) {
        Map<String, Long> summary = colisService.getColisSummary(groupByField);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Rapport détaillé du poids et du nombre de colis par livreur ou zone",
            description = "Opération Gestionnaire: Calcule le poids total (SUM) et le nombre total (COUNT) des colis regroupés par Livreur ou par Zone.",
            responses = {@ApiResponse(responseCode = "200", description = "Liste des totaux par groupe")})
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/detailed-summary")
    public ResponseEntity<List<Map<String, Object>>> getDetailedColisSummary(
            @Parameter(description = "Champ de regroupement: 'livreur' ou 'zone'") @RequestParam String groupByField) {
        List<Map<String, Object>> summary = colisService.getDetailedColisSummary(groupByField);
        return ResponseEntity.ok(summary);
    }
}