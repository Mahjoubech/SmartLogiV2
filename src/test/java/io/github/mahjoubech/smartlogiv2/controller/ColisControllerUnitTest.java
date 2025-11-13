package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ColisProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ClientDestinataireResponseBasic;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.*;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ColisController.class)
public class ColisControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColisService colisService;

    @Autowired
    private ObjectMapper objectMapper;

    private ColisRequest createSampleColisRequest() {
        ColisRequest request = new ColisRequest();
        request.setPoids(5.5);
        request.setDescription("Test package");
        request.setPriorite("NORMAL");
        request.setVilleDestination("Paris");
        request.setClientExpediteurEmail("sender@example.com");
        request.setDestinataireEmail("receiver@example.com");
        request.setCodePostal("75001");

        ProduitRequest produit = new ProduitRequest();
        produit.setNom("Laptop");
        produit.setCategorie("Electronics");
        produit.setPoids(2.5);
        produit.setPrix(BigDecimal.valueOf(1000));
        produit.setColisProduit(new ColisProduitRequest());

        request.setProduits(List.of(produit));
        return request;
    }

    private ColisResponse createSampleColisResponse(String id) {
        ColisResponse response = new ColisResponse();
        response.setId(id);
        response.setDescription("Test package");
        response.setPoids(5.5);
        response.setStatut("CREE");
        response.setPriorite("NORMAL");
        response.setVilleDestination("Paris");
        response.setDateCreation(ZonedDateTime.now());

        ClientDestinataireResponse expediteur = new ClientDestinataireResponse();
        expediteur.setEmail("sender@example.com");
        response.setClientExpediteur(expediteur);

        ClientDestinataireResponse destinataire = new ClientDestinataireResponse();
        destinataire.setEmail("receiver@example.com");
        response.setDestinataire(destinataire);

        ZoneResponse zone = new ZoneResponse();
        zone.setId(UUID.randomUUID().toString());
        zone.setNom("Zone Paris");
        response.setZone(zone);

        response.setHistorique(new ArrayList<>());
        response.setProduits(new ArrayList<>());

        return response;
    }
    private ColisResponseBasic createSampleColisResponseBasic(String id) {
        ColisResponseBasic response = new ColisResponseBasic();
        response.setId(id);
        response.setDescription("Test package");
        response.setPoids(5.5);
        response.setStatut("CREE");
        response.setPriorite("NORMAL");
        response.setVilleDestination("Paris");
        response.setDateCreation(ZonedDateTime.now());

        ClientDestinataireResponseBasic expediteur = new ClientDestinataireResponseBasic();
        response.setClientExpediteur(expediteur);

        ClientDestinataireResponseBasic destinataire = new ClientDestinataireResponseBasic();
        response.setDestinataire(destinataire);

        ZoneResponse zone = new ZoneResponse();
        zone.setId(UUID.randomUUID().toString());
        zone.setNom("Zone Paris");
        response.setZone(zone);

        return response;
    }

    @Test
    public void createColis_should_create_and_return_created_status() throws Exception {
        ColisRequest request = createSampleColisRequest();
        String colisId = UUID.randomUUID().toString();
        ColisResponse response = createSampleColisResponse(colisId);

        when(colisService.createDelivery( ArgumentMatchers.any(ColisRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v2/colis")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.description", is("Test package")))
                .andExpect(jsonPath("$.statut", is("CREE")));
    }

    @Test
    public void createColis_should_return_bad_request_when_validation_fails() throws Exception {
        ColisRequest request = new ColisRequest();
        // Missing required fields

        mockMvc.perform(post("/api/v2/colis")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllColis_should_return_paginated_list() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ColisResponseBasic> colisList = List.of(
                createSampleColisResponseBasic(UUID.randomUUID().toString()),
                createSampleColisResponseBasic(UUID.randomUUID().toString())
        );
        Page<ColisResponseBasic> page = new PageImpl<>(colisList, pageable, 2);

        when(colisService.getAllColis( ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v2/colis")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "dateCreation")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    public void getColisById_should_return_colis_details() throws Exception {
        String colisId = UUID.randomUUID().toString();
        ColisResponse response = createSampleColisResponse(colisId);

        when(colisService.getColisById(colisId)).thenReturn(response);

        mockMvc.perform(get("/api/v2/colis/{colisId}", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.description", is("Test package")))
                .andExpect(jsonPath("$.poids", is(5.5)));
    }

    @Test
    public void updateColis_should_update_and_return_ok() throws Exception {
        String colisId = UUID.randomUUID().toString();
        ColisRequest request = createSampleColisRequest();
        request.setDescription("Updated package");
        ColisResponse response = createSampleColisResponse(colisId);
        response.setDescription("Updated package");

        when(colisService.updateColis(eq(colisId),  ArgumentMatchers.any(ColisRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v2/colis/{colisId}", colisId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.description", is("Updated package")));
    }

    @Test
    public void deleteColis_should_delete_and_return_success_message() throws Exception {
        String colisId = UUID.randomUUID().toString();

        doNothing().when(colisService).deleteColis(colisId);

        mockMvc.perform(delete("/api/v2/colis/{colisId}", colisId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Colis deleted successfully")))
                .andExpect(content().string(containsString(colisId)));
    }

    @Test
    public void updateColisStatus_should_update_status_and_return_ok() throws Exception {
        String colisId = UUID.randomUUID().toString();
        HistoriqueLivraisonRequest statusRequest = new HistoriqueLivraisonRequest();
        statusRequest.setStatut("EN_TRANSIT");
        statusRequest.setCommentaire("Test Status Update");
        ColisResponse response = createSampleColisResponse(colisId);
        response.setStatut("EN_TRANSIT");

        when(colisService.updateColisStatus(eq(colisId),  ArgumentMatchers.any(HistoriqueLivraisonRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v2/colis/{colisId}/status", colisId)
                        .contentType(MediaType.APPLICATION_JSON) // 👈 Utilisez MediaType
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut", is("EN_TRANSIT")));
    }

    @Test
    public void assignColisToLivreur_should_assign_and_return_ok() throws Exception {
        String colisId = UUID.randomUUID().toString();
        String livreurId = UUID.randomUUID().toString();

        ColisResponse response = createSampleColisResponse(colisId);
        LivreurResponse livreur = new LivreurResponse();
        livreur.setId(livreurId);
        response.setLivreur(livreur);
        response.setStatut("COLLECTE");

        when(colisService.assignColisToLivreur(colisId, livreurId)).thenReturn(response);

        mockMvc.perform(put("/api/v2/colis/gestionner/livreur/{colisId}/assign", colisId)
                        .param("livreurId", livreurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.livreur.id", is(livreurId)))
                .andExpect(jsonPath("$.statut", is("COLLECTE")));
    }

    @Test
    public void findColisByCriteria_should_return_filtered_results() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        ColisResponse sampleResponse = createSampleColisResponse(UUID.randomUUID().toString());

        List<ColisResponse> colisList = List.of(sampleResponse);
        Page<ColisResponse> page = new PageImpl<>(colisList, pageable, colisList.size());
        when(colisService.findColisByCriteria(
                eq("CREE"),
                any(),
                eq("Paris"),
                eq("NORMAL"),
                ArgumentMatchers.any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/v2/colis/search")
                        .param("statut", "CREE")
                        .param("ville", "Paris")
                        .param("priorite", "NORMAL")
                        // ... Ma'zidch zoneId hna ...
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].statut", is("CREE")));
    }

    @Test
    public void getColisByExpediteur_should_return_expediteur_colis() throws Exception {
        String expediteurId = UUID.randomUUID().toString();
        Pageable pageable = PageRequest.of(0, 10);
        List<ColisResponse> colisList = List.of(
                createSampleColisResponse(UUID.randomUUID().toString()),
                createSampleColisResponse(UUID.randomUUID().toString())
        );
        Page<ColisResponse> page = new PageImpl<>(colisList, pageable, 2);

        when(colisService.findByExpediteur(eq(expediteurId),  ArgumentMatchers.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v2/colis/expediteur/{expediteurId}", expediteurId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    @Test
    public void getColisHistory_should_return_history() throws Exception {
        String colisId = UUID.randomUUID().toString();
        Pageable pageable = PageRequest.of(0, 10);

        HistoriqueLivraisonResponse historyItem = new HistoriqueLivraisonResponse();
        // Set fields for historyItem

        List<HistoriqueLivraisonResponse> historyList = List.of(historyItem);
        Page<HistoriqueLivraisonResponse> page = new PageImpl<>(historyList, pageable, 1);

        when(colisService.getColisHistory(eq(colisId),  ArgumentMatchers.any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v2/colis/{colisId}/history", colisId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void getColisSummary_should_return_summary_by_status() throws Exception {
        Map<String, Long> summary = new HashMap<>();
        summary.put("CREE", 10L);
        summary.put("EN_TRANSIT", 5L);
        summary.put("LIVRE", 15L);

        when(colisService.getColisSummary("statut")).thenReturn(summary);

        mockMvc.perform(get("/api/v2/colis/summary")
                        .param("groupByField", "statut"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CREE", is(10)))
                .andExpect(jsonPath("$.EN_TRANSIT", is(5)))
                .andExpect(jsonPath("$.LIVRE", is(15)));
    }

    @Test
    public void getColisSummary_should_return_summary_by_zone() throws Exception {
        Map<String, Long> summary = new HashMap<>();
        summary.put("Zone Nord", 8L);
        summary.put("Zone Sud", 12L);

        when(colisService.getColisSummary("zone")).thenReturn(summary);

        mockMvc.perform(get("/api/v2/colis/summary")
                        .param("groupByField", "zone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Zone Nord']", is(8)))
                .andExpect(jsonPath("$.['Zone Sud']", is(12)));
    }

    @Test
    public void createColis_should_validate_priorite_pattern() throws Exception {
        ColisRequest request = createSampleColisRequest();
        request.setPriorite("INVALID");

        mockMvc.perform(post("/api/v2/colis")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createColis_should_validate_email_format() throws Exception {
        ColisRequest request = createSampleColisRequest();
        request.setClientExpediteurEmail("invalid-email");

        mockMvc.perform(post("/api/v2/colis")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createColis_should_validate_poids_minimum() throws Exception {
        ColisRequest request = createSampleColisRequest();
        request.setPoids(-1.0);

        mockMvc.perform(post("/api/v2/colis")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void getAllColis_should_return_list_with_desc_sort() throws Exception {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("dateCreation").descending());
        when(colisService.getAllColis(eq(pageable))).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v2/colis")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "dateCreation")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk());
    }
    @Test
    public void getAllColis_should_trigger_ASC_sort() throws Exception {
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by("id").ascending()); // Sort ASC

        when(colisService.getAllColis(eq(expectedPageable))).thenReturn(Page.empty());

        // T'nfid dyal MockMvc m3a sortDir=asc
        mockMvc.perform(get("/api/v2/colis")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk());

        verify(colisService, times(1)).getAllColis(eq(expectedPageable));
    }
}
