package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.service.LogisticsDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v4/gestion")
@RequiredArgsConstructor
public class LogisticsDataController {
    private final LogisticsDataService logisticsDataService;

    @PostMapping("/zone")
    public ResponseEntity<ZoneResponse> createZone(@Valid @RequestBody ZoneRequest request){
        ZoneResponse zoneResponse= logisticsDataService.createZone(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(zoneResponse);
    }

    @PutMapping("/zone/{zoneId}")
    public ResponseEntity<ZoneResponse> updateZone(@PathVariable String zoneId,
                                                   @Valid @RequestBody ZoneRequest request){
        ZoneResponse zoneResponse= logisticsDataService.updateZone(zoneId,request);
        return ResponseEntity.ok(zoneResponse);
    }

    @DeleteMapping("/zone/{zoneId}")
    public ResponseEntity<String> deleteZone(@PathVariable String zoneId){
        logisticsDataService.deleteZone(zoneId);
        return ResponseEntity.ok("Zone deleted successfully");
    }
    @GetMapping("/zone/{zoneId}")
    public  ResponseEntity<ZoneResponse> getZone(@PathVariable String zoneId){
        ZoneResponse zoneResponse= logisticsDataService.getZoneById(zoneId);
        return ResponseEntity.ok(zoneResponse);
    }

    @GetMapping("/zone")
    public ResponseEntity<Page<ZoneResponse>> getAllZones(Pageable pageable){
        Page<ZoneResponse> zoneResponses= logisticsDataService.getAllZones(pageable);
        return ResponseEntity.ok(zoneResponses);

    }
    @PostMapping("/produit")
    public ResponseEntity<ProduitResponse> createProduit(@Valid @RequestBody ProduitRequest request) {
        ProduitResponse response = logisticsDataService.createProduit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/produit/{produitId}")
    public ResponseEntity<ProduitResponse> getProduitById(@PathVariable String produitId) {
        ProduitResponse response = logisticsDataService.getProduitById(produitId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/produit")
    public ResponseEntity<Page<ProduitResponse>> findAllProduits(Pageable pageable) {
        Page<ProduitResponse> responsePage = logisticsDataService.findAllProduits(pageable);
        return ResponseEntity.ok(responsePage);
    }
    @PutMapping("/produit/{produitId}")
    public ResponseEntity<ProduitResponse> updateProduit(
            @PathVariable String produitId,
            @Valid @RequestBody ProduitRequest request) {
        ProduitResponse response = logisticsDataService.updateProduit(produitId, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/produit{produitId}")
    public ResponseEntity<Void> deleteProduit(@PathVariable String produitId) {
        logisticsDataService.deleteProduit(produitId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/produit/cleanup/duplicates")
    public ResponseEntity<String> deleteDuplicateProducts() {
        logisticsDataService.deleteDuplicateProducts();
        return ResponseEntity.ok("Nettoyage des produits dupliqués effectué avec succès.");
    }
}
