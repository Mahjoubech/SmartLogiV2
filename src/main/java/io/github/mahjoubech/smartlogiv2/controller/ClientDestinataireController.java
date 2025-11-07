package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.service.ClientDestinataireService;
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
@RequestMapping("/api/v3/clients")
@RequiredArgsConstructor
public class ClientDestinataireController {
    private final ClientDestinataireService clientDestinataireService;

    @PostMapping("/register/expediteur")
    public ResponseEntity<ClientDestinataireResponse> createExpediteur(@Valid @RequestBody ClientDestinataireRequest request) {
        ClientDestinataireResponse response = clientDestinataireService.createExpediteur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body( response );
    }
    @PostMapping("/register/destinataire")
    public ResponseEntity<ClientDestinataireResponse> createDestinataire(@Valid @RequestBody ClientDestinataireRequest request) {
        ClientDestinataireResponse response = clientDestinataireService.createDestinataire(request);
        return ResponseEntity.status(HttpStatus.CREATED).body( response );
    }
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDestinataireResponse> getColisById(@PathVariable String clientId) {
        ClientDestinataireResponse response = clientDestinataireService.getClientById(clientId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientDestinataireResponse> updateClient(
            @PathVariable String clientId,
            @Valid @RequestBody ClientDestinataireRequest request) {
        ClientDestinataireResponse response = clientDestinataireService.updateClient(clientId, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{clientId}")
    public ResponseEntity<String> deleteClient(@PathVariable String clientId) {
        clientDestinataireService.deleteClient(clientId);
        String message = "Client deleted successfully with ID: " + clientId;
        return ResponseEntity.ok(message);
    }
    @GetMapping
    public ResponseEntity<Page<ClientDestinataireResponse>> getAllClients(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(defaultValue = "dateCreation") String sortBy,
                                                                          @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page , size, sort);
        Page<ClientDestinataireResponse> clientPage = clientDestinataireService.getAllClients(pageable);
        return ResponseEntity.ok().body(clientPage);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<ClientDestinataireResponse>> searchClients(
            @RequestParam String keyword,
            Pageable pageable)
    {
        Page<ClientDestinataireResponse> responsePage = clientDestinataireService.searchClients(keyword, pageable);
        return ResponseEntity.ok(responsePage);
    }
}
