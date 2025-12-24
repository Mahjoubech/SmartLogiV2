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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;



@RestController
@RequestMapping("/api/v3/clients")
@RequiredArgsConstructor
@Tag(name = "Clients et Destinataires", description = "Gestion des utilisateurs (Expéditeurs, Destinataires) pour la logistique.")
public class ClientDestinataireController {
    private final ClientDestinataireService clientDestinataireService;
    @Operation(summary = "Enregistrer un nouveau destinataire",
            description = "Crée un nouveau destinataire. Utilisé par le gestionnaire.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Destinataire créé avec succès"),
                    @ApiResponse(responseCode = "409", description = "Conflit: Email déjà utilisé")
            })
    @PreAuthorize("hasRole('CLIENT') and hasAuthority('CREATE')")
    @PostMapping("/register/destinataire")
    public ResponseEntity<ClientDestinataireResponse> createDestinataire(@Valid @RequestBody ClientDestinataireRequest request) {
        ClientDestinataireResponse response = clientDestinataireService.createDestinataire(request);
        return ResponseEntity.status(HttpStatus.CREATED).body( response );
    }

    @Operation(summary = "Consulter les détails d'un client/destinataire",
            description = "Recherche un utilisateur par son ID unique et affiche son rôle (Expéditeur ou Destinataire).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Détails de l'utilisateur trouvés"),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
            })
    @PreAuthorize("hasAnyRole('CLIENT' , 'MANAGER') and hasAuthority('VIEW')")
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDestinataireResponse> getColisById(@PathVariable String clientId) {
        ClientDestinataireResponse response = clientDestinataireService.getClientById(clientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mettre à jour les informations d'un client/destinataire",
            description = "Permet de corriger l'adresse, le nom ou le téléphone d'un utilisateur existant (L'email ne peut pas être modifié).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mise à jour réussie"),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
            })
    @PreAuthorize("hasAnyRole('CLIENT' , 'MANAGER')and hasAuthority('UPDATE')")
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientDestinataireResponse> updateClient(
            @PathVariable String clientId,
            @Valid @RequestBody ClientDestinataireRequest request) {
        ClientDestinataireResponse response = clientDestinataireService.updateClient(clientId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Supprimer un client ou destinataire",
            description = "Supprime un utilisateur par son ID. Opération bloquée si l'utilisateur est lié à des Colis existants.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Suppression réussie"),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
                    @ApiResponse(responseCode = "409", description = "Conflit: Lié à des colis existants")
            })
    @PreAuthorize("hasAnyRole('MANAGER') and hasAuthority('DELETE')")
    @DeleteMapping("/{clientId}")
    public ResponseEntity<String> deleteClient(@PathVariable String clientId) {
        clientDestinataireService.deleteClient(clientId);
        String message = "Client deleted successfully with ID: " + clientId;
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Afficher la liste paginée de tous les clients et destinataires",
            description = "Utilisé par le gestionnaire pour visualiser toute la base clients avec pagination et tri.",
            responses = {@ApiResponse(responseCode = "200", description = "Liste paginée des utilisateurs retournée")})
    @PreAuthorize("hasRole('MANAGER') and hasAuthority('VIEW')")
    @GetMapping
    public ResponseEntity<Page<ClientDestinataireResponse>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page , size, sort);
        Page<ClientDestinataireResponse> clientPage = clientDestinataireService.getAllClients(pageable);
        return ResponseEntity.ok().body(clientPage);
    }

    @Operation(summary = "Rechercher des clients ou destinataires par mot-clé",
            description = "Permet de rechercher des utilisateurs par Nom, Email, ou Téléphone (recherche unifiée).",
            responses = {@ApiResponse(responseCode = "200", description = "Résultats de la recherche paginés")})
    @PreAuthorize("hasRole('MANAGER') and hasAuthority('VIEW')")
    @GetMapping("/search")
    public ResponseEntity<Page<ClientDestinataireResponse>> searchClients(
            @RequestParam String keyword,
            Pageable pageable)
    {
        Page<ClientDestinataireResponse> responsePage = clientDestinataireService.searchClients(keyword, pageable);
        return ResponseEntity.ok(responsePage);
    }
}