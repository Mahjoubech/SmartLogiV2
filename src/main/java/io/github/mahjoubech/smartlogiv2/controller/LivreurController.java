package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.service.LivreurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
        ("/api/v1/livreur")
@RequiredArgsConstructor
public class LivreurController {

    private final LivreurService livreurService;

    @PostMapping
    public ResponseEntity<LivreurResponse> createLivreur(@Valid @RequestBody LivreurRequest livreurRequest){
        LivreurResponse livreurResponse= livreurService.createLivreur(livreurRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(livreurResponse);
    }

    @GetMapping
    public ResponseEntity<Page<LivreurResponse>> getAllLivreurs(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "id") String sortBy,
                                                                @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LivreurResponse> livreurResponses = livreurService.getAllLivreurs(pageable);
        return ResponseEntity.ok().body(livreurResponses);
    }

    @GetMapping("/{livreur_id}" )
    public ResponseEntity<LivreurResponse> getLivreurById(@PathVariable("livreur_id") String livreurId){
        LivreurResponse livreurResponse= livreurService.getLivreurById(livreurId);
        return ResponseEntity.ok().body(livreurResponse);
    }

    @PutMapping("/{livreur_id}")
    public ResponseEntity<LivreurResponse> updateLivreur(@PathVariable String livreurId,
                                                         @Valid @RequestBody LivreurRequest livreurRequest){
        LivreurResponse livreurResponse= livreurService.updateLivreur(livreurId, livreurRequest);
        return ResponseEntity.ok().body(livreurResponse);
    }

    @DeleteMapping("/{livreur_id}")
    public ResponseEntity<String> deleteLivreur(@PathVariable("livreur_id") String livre){
        livreurService.deleteLivreur(livre);
        String message = "Livreur deleted successfully with ID: " + livre;
        return ResponseEntity.ok(message);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<LivreurResponse>> searchLivreurs(  @RequestParam(required = false) String keyword,
                                                                  Pageable pageable) {

        Page<LivreurResponse> result = livreurService.searchLivreurs(keyword == null ? "" : keyword, pageable);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/counts")
    public ResponseEntity<Page<LivreurColisResponse>> getLivreurColisCounts(Pageable pageable) {
        Page<LivreurColisResponse> result = livreurService.getLivreurColisCounts(pageable);
        return ResponseEntity.ok(result);
    }
}
