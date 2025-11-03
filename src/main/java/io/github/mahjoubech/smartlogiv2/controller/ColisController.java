package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
("/api/v2/colis")
@RequiredArgsConstructor
public class ColisController {
    private final ColisService colisService;
    @PostMapping
    public ResponseEntity<ColisResponse> createColis(@Valid @RequestBody ColisRequest request) {
        ColisResponse response = colisService.createDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<Iterable<ColisResponse>> getAllColis() {
        Iterable<ColisResponse> colisList = colisService.getAllColis();
        return ResponseEntity.ok(colisList);
    }
    @GetMapping("/{colisId}")
    public ResponseEntity<ColisResponse> getColisById(@PathVariable String colisId) {
        ColisResponse response = colisService.getColisById(colisId);
        return ResponseEntity.ok(response);
    }

    // 3. UPDATE (CRUD)
    // PUT /api/v1/colis/{colisId} - User Story: Gestionnaire - Je veux corriger des informations
    @PutMapping("/{colisId}")
    public ResponseEntity<ColisResponse> updateColis(
            @PathVariable String colisId,
            @Valid @RequestBody ColisRequest request) {
        ColisResponse response = colisService.updateColis(colisId, request);
        return ResponseEntity.ok(response);
    }

    // 4. DELETE (CRUD)
    // DELETE /api/v1/colis/{colisId} - User Story: Gestionnaire - Je veux supprimer des informations erronées
    @DeleteMapping("/{colisId}")
    public ResponseEntity<Void> deleteColis(@PathVariable String colisId) {
        colisService.deleteColis(colisId);
        return ResponseEntity.noContent().build();
    }

    // --- FONCTIONNALITÉS LOGISTIQUES ---

    // 5. UPDATE STATUS
    // PUT /api/v1/colis/{colisId}/status - User Story: Livreur/Gestionnaire - Mettre à jour le statut
    @PutMapping("/{colisId}/status")
    public ResponseEntity<ColisResponse> updateColisStatus(
            @PathVariable String colisId,
            @Valid @RequestBody HistoriqueLivraisonRequest statusRequest) {
        ColisResponse response = colisService.updateColisStatus(colisId, statusRequest);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/expediteur/{expediteurId}")
    public ResponseEntity<Page<ColisResponse>> getColisByExpediteur(
            @PathVariable String expediteurId,
            Pageable pageable)
    {
        Page<ColisResponse> responsePage = colisService.findByExpediteur(expediteurId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{colisId}/history")
    public ResponseEntity<List<HistoriqueLivraisonResponse>> getColisHistory(@PathVariable String colisId) {
        List<HistoriqueLivraisonResponse> history = colisService.getColisHistory(colisId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getColisSummary(@RequestParam String groupByField) {
        Map<String, Long> summary = colisService.getColisSummary(groupByField);
        return ResponseEntity.ok(summary);
    }
}
