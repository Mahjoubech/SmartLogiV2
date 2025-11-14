package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import io.github.mahjoubech.smartlogiv2.service.LogisticsDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LogisticsDataController.class)
public class LogisticsDataControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogisticsDataService logisticsDataService;

    @MockitoBean
    private ColisService colisService;

    @Autowired
    private ObjectMapper objectMapper;

    private ZoneRequest createSampleZoneRequest() {
        ZoneRequest request = new ZoneRequest();
        request.setNom("Zone Paris");
        request.setCodePostal("75001");
        return request;
    }

    private ZoneResponse createSampleZoneResponse(String id) {
        ZoneResponse response = new ZoneResponse();
        response.setId(id);
        response.setNom("Zone Paris");
        response.setCodePostal("75001");
        return response;
    }

    private ProduitRequest createSampleProduitRequest() {
        ProduitRequest request = new ProduitRequest();
        request.setNom("Ordinateur Portable");
        request.setCategorie("Électronique");
        request.setPoids(2.0);
        request.setPrix(BigDecimal.valueOf(1200));
        return request;
    }

    private ProduitResponse createSampleProduitResponse(String id) {
        ProduitResponse response = new ProduitResponse();
        response.setId(id);
        response.setNom("Ordinateur Portable");
        response.setCategorie("Électronique");
        response.setPoids(2.0);
        response.setPrix(BigDecimal.valueOf(1200));
        return response;
    }

    @Test
    public void createZone_should_return_created() throws Exception {
        ZoneRequest request = createSampleZoneRequest();
        String zoneId = UUID.randomUUID().toString();
        ZoneResponse response = createSampleZoneResponse(zoneId);

        when(logisticsDataService.createZone(any(ZoneRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v4/gestion/zone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(zoneId)))
                .andExpect(jsonPath("$.nom", is("Zone Paris")));
    }

    @Test
    public void updateZone_should_return_updated() throws Exception {
        ZoneRequest request = createSampleZoneRequest();
        String zoneId = UUID.randomUUID().toString();
        ZoneResponse response = createSampleZoneResponse(zoneId);

        when(logisticsDataService.updateZone(eq(zoneId), any(ZoneRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v4/gestion/zone/{zoneId}", zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(zoneId)))
                .andExpect(jsonPath("$.nom", is("Zone Paris")));
    }

    @Test
    public void deleteZone_should_return_success_message() throws Exception {
        String zoneId = UUID.randomUUID().toString();
        doNothing().when(logisticsDataService).deleteZone(zoneId);

        mockMvc.perform(delete("/api/v4/gestion/zone/{zoneId}", zoneId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Zone deleted successfully")));
    }

    @Test
    public void getZone_should_return_zone_details() throws Exception {
        String zoneId = UUID.randomUUID().toString();
        ZoneResponse response = createSampleZoneResponse(zoneId);

        when(logisticsDataService.getZoneById(zoneId)).thenReturn(response);

        mockMvc.perform(get("/api/v4/gestion/zone/{zoneId}", zoneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(zoneId)))
                .andExpect(jsonPath("$.nom", is("Zone Paris")))
                .andExpect(jsonPath("$.codePostal", is("75001")));
    }

    @Test
    public void getAllZones_should_return_paginated_list() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ZoneResponse> zones = List.of(
                createSampleZoneResponse(UUID.randomUUID().toString()),
                createSampleZoneResponse(UUID.randomUUID().toString())
        );
        Page<ZoneResponse> page = new PageImpl<>(zones, pageable, zones.size());

        when(logisticsDataService.getAllZones(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v4/gestion/zone")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void createProduit_should_return_created() throws Exception {
        ProduitRequest request = createSampleProduitRequest();
        String produitId = UUID.randomUUID().toString();
        ProduitResponse response = createSampleProduitResponse(produitId);

        when(logisticsDataService.createProduit(any(ProduitRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v4/gestion/produit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(produitId)))
                .andExpect(jsonPath("$.nom", is("Ordinateur Portable")));
    }

    @Test
    public void updateProduit_should_return_updated() throws Exception {
        ProduitRequest request = createSampleProduitRequest();
        String produitId = UUID.randomUUID().toString();
        ProduitResponse response = createSampleProduitResponse(produitId);

        when(logisticsDataService.updateProduit(eq(produitId), any(ProduitRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v4/gestion/produit/{produitId}", produitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(produitId)))
                .andExpect(jsonPath("$.nom", is("Ordinateur Portable")));
    }

    @Test
    public void deleteProduit_should_return_no_content() throws Exception {
        String produitId = UUID.randomUUID().toString();
        doNothing().when(logisticsDataService).deleteProduit(produitId);

        mockMvc.perform(delete("/api/v4/gestion/produit{produitId}", produitId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getProduitById_should_return_details() throws Exception {
        String produitId = UUID.randomUUID().toString();
        ProduitResponse response = createSampleProduitResponse(produitId);

        when(logisticsDataService.getProduitById(produitId)).thenReturn(response);

        mockMvc.perform(get("/api/v4/gestion/produit/{produitId}", produitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(produitId)))
                .andExpect(jsonPath("$.nom", is("Ordinateur Portable")));
    }

    @Test
    public void findAllProduits_should_return_paginated_catalogue() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProduitResponse> produits = List.of(
                createSampleProduitResponse(UUID.randomUUID().toString()),
                createSampleProduitResponse(UUID.randomUUID().toString())
        );
        Page<ProduitResponse> page = new PageImpl<>(produits, pageable, produits.size());

        when(logisticsDataService.findAllProduits(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v4/gestion/produit")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void deleteDuplicateProducts_should_return_success_message() throws Exception {
        doNothing().when(logisticsDataService).deleteDuplicateProducts();

        mockMvc.perform(post("/api/v4/gestion/produit/cleanup/duplicates"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nettoyage des produits dupliqués effectué avec succès.")));
    }

    @Test
    public void getColisSummary_should_return_summary_by_field() throws Exception {
        Map<String, Long> summary = Map.of("Zone Paris", 3L, "Zone Lyon", 2L);
        when(colisService.getColisSummary("zone")).thenReturn(summary);

        mockMvc.perform(get("/api/v4/gestion/colis/summary")
                        .param("groupByField", "zone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Zone Paris']", is(3)))
                .andExpect(jsonPath("$.['Zone Lyon']", is(2)));
    }

    @Test
    public void getDetailedColisSummary_should_return_list_of_map() throws Exception {
        List<Map<String, Object>> summary = List.of(
                Map.of("livreur", "Alice", "poidsTotal", 5.5, "nombreColis", 3),
                Map.of("livreur", "Bob", "poidsTotal", 10.2, "nombreColis", 5)
        );
        when(colisService.getDetailedColisSummary("livreur")).thenReturn(summary);

        mockMvc.perform(get("/api/v4/gestion/detailed-summary")
                        .param("groupByField", "livreur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].livreur", is("Alice")))
                .andExpect(jsonPath("$[1].livreur", is("Bob")));
    }
}
