package io.github.mahjoubech.smartlogiv2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LogisticsDataControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void createZone_should_return_zone_created() throws Exception {
        ZoneRequest zoneRequest = new ZoneRequest();
        zoneRequest.setNom("Zone Test");
        zoneRequest.setCodePostal("75000");

        mockMvc.perform(post("/api/v4/gestion/zone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Zone Test"))
                .andExpect(jsonPath("$.codePostal").value("75000"));
    }

    @Test
    public void updateZone_should_return_zone_updated() throws Exception {
        // First create zone (integration test DB should support this)
        String zoneId = createZoneAndGetId();

        ZoneRequest updateRequest = new ZoneRequest();
        updateRequest.setNom("Zone Updated");
        updateRequest.setCodePostal("75100");

        mockMvc.perform(put("/api/v4/gestion/zone/{zoneId}", zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Zone Updated"))
                .andExpect(jsonPath("$.codePostal").value("75100"));
    }

    @Test
    public void deleteZone_should_delete_zone_and_return_ok() throws Exception {
        String zoneId = createZoneAndGetId();

        mockMvc.perform(delete("/api/v4/gestion/zone/{zoneId}", zoneId))
                .andExpect(status().isOk())
                .andExpect(content().string("Zone deleted successfully"));
    }

    @Test
    public void getZone_should_return_zone_details() throws Exception {
        String zoneId = createZoneAndGetId();

        mockMvc.perform(get("/api/v4/gestion/zone/{zoneId}", zoneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(zoneId));
    }

    @Test
    public void getAllZones_should_return_paginated_zones() throws Exception {
        mockMvc.perform(get("/api/v4/gestion/zone")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void createProduit_should_return_produit_created() throws Exception {
        ProduitRequest produitRequest = new ProduitRequest();
        produitRequest.setNom("Produit Test");
        produitRequest.setCategorie("Catégorie Test");
        produitRequest.setPoids(2.5);
        produitRequest.setPrix(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/v4/gestion/produit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produitRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Produit Test"))
                .andExpect(jsonPath("$.categorie").value("Catégorie Test"));
    }

    @Test
    public void updateProduit_should_return_produit_updated() throws Exception {
        String produitId = createProduitAndGetId();

        ProduitRequest updateRequest = new ProduitRequest();
        updateRequest.setNom("Produit Updated");
        updateRequest.setCategorie("Catégorie Updated");
        updateRequest.setPoids(3.0);
        updateRequest.setPrix(BigDecimal.valueOf(150));

        mockMvc.perform(put("/api/v4/gestion/produit/{produitId}", produitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Produit Updated"))
                .andExpect(jsonPath("$.categorie").value("Catégorie Updated"));
    }

    @Test
    public void deleteProduit_should_return_no_content() throws Exception {
        String produitId = createProduitAndGetId();

        mockMvc.perform(delete("/api/v4/gestion/produit{produitId}", produitId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getProduitById_should_return_produit_details() throws Exception {
        String produitId = createProduitAndGetId();

        mockMvc.perform(get("/api/v4/gestion/produit/{produitId}", produitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(produitId));
    }

    @Test
    public void findAllProduits_should_return_paginated_produits() throws Exception {
        mockMvc.perform(get("/api/v4/gestion/produit")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void deleteDuplicateProducts_should_return_success_message() throws Exception {
        mockMvc.perform(post("/api/v4/gestion/produit/cleanup/duplicates"))
                .andExpect(status().isOk())
                .andExpect(content().string("Nettoyage des produits dupliqués effectué avec succès."));
    }

    @Test
    public void getColisSummary_should_return_summary_map() throws Exception {
        mockMvc.perform(get("/api/v4/gestion/colis/summary")
                        .param("groupByField", "status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    public void getDetailedColisSummary_should_return_list_of_maps() throws Exception {
        mockMvc.perform(get("/api/v4/gestion/detailed-summary")
                        .param("groupByField", "livreur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // Helper methods to create entities and return their IDs
    private String createZoneAndGetId() throws Exception {
        ZoneRequest zoneRequest = new ZoneRequest();
        zoneRequest.setNom("Temp Zone");
        zoneRequest.setCodePostal("75000");

        String response = mockMvc.perform(post("/api/v4/gestion/zone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneRequest)))
                .andReturn().getResponse().getContentAsString();

        ZoneResponse zoneResponse = objectMapper.readValue(response, ZoneResponse.class);
        return zoneResponse.getId();
    }

    private String createProduitAndGetId() throws Exception {
        ProduitRequest produitRequest = new ProduitRequest();
        produitRequest.setNom("Temp Produit");
        produitRequest.setCategorie("Categorie Temp");
        produitRequest.setPoids(1.0);
        produitRequest.setPrix(java.math.BigDecimal.valueOf(50));

        String response = mockMvc.perform(post("/api/v4/gestion/produit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produitRequest)))
                .andReturn().getResponse().getContentAsString();

        ProduitResponse produitResponse = objectMapper.readValue(response, ProduitResponse.class);
        return produitResponse.getId();
    }
}
