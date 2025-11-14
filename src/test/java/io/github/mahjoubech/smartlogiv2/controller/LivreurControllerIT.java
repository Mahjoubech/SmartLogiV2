package io.github.mahjoubech.smartlogiv2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import io.github.mahjoubech.smartlogiv2.repository.LivreurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LivreurControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LivreurRepository livreurRepository;

    private LivreurRequest createSampleLivreurRequest() {
        LivreurRequest request = new LivreurRequest();
        request.setNom("Integration");
        request.setPrenom("Test");
        request.setTelephone("0123456789");
        request.setVehicule("Fiat");
        request.setZoneAssigneeId(createZoneAndGetId());
        return request;
    }

    private String createZoneAndGetId() {
        try {
            String zoneJson = """
                    {
                        "nom": "Test Zone",
                        "codePostal": "75000"
                    }
                    """;

            String response = mockMvc.perform(post("/api/v4/gestion/zone")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(zoneJson))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readTree(response).get("id").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createLivreur_shouldPersistAndReturnCreated() throws Exception {
        LivreurRequest request = createSampleLivreurRequest();

        mockMvc.perform(post("/api/v1/gestionner/livreur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.nom", is("Integration")))
                .andExpect(jsonPath("$.prenom", is("Test")))
                .andExpect(jsonPath("$.vehicule", is("Fiat")));

        Page<Livreur> allLivreurs = livreurRepository.findAll(Pageable.unpaged());
        assertTrue(allLivreurs.getTotalElements() > 0);
    }

    @Test
    public void getAllLivreurs_shouldReturnPagedList() throws Exception {
        mockMvc.perform(get("/api/v1/gestionner/livreur")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void getLivreurById_shouldReturnDetails() throws Exception {
        LivreurRequest request = createSampleLivreurRequest();
        String responseContent = mockMvc.perform(post("/api/v1/gestionner/livreur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        String livreurId = objectMapper.readTree(responseContent).get("id").asText();

        mockMvc.perform(get("/api/v1/gestionner/livreur/{livreur_id}", livreurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(livreurId)))
                .andExpect(jsonPath("$.nom", is("Integration")));
    }

    @Test
    public void updateLivreur_shouldUpdateAndReturnOk() throws Exception {
        LivreurRequest request = createSampleLivreurRequest();
        String responseContent = mockMvc.perform(post("/api/v1/gestionner/livreur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        String livreurId = objectMapper.readTree(responseContent).get("id").asText();

        request.setNom("Updated Name");

        mockMvc.perform(put("/api/v1/gestionner/livreur/{livreur_id}", livreurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Updated Name")));
    }

    @Test
    public void deleteLivreur_shouldDeleteAndReturnOk() throws Exception {
        LivreurRequest request = createSampleLivreurRequest();
        String responseContent = mockMvc.perform(post("/api/v1/gestionner/livreur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        String livreurId = objectMapper.readTree(responseContent).get("id").asText();

        mockMvc.perform(delete("/api/v1/gestionner/livreur/{livreur_id}", livreurId))
                .andExpect(status().isOk())
                .andExpect(content().string("Livreur deleted successfully with ID: " + livreurId));
    }

    @Test
    public void searchLivreurs_shouldReturnFilteredPage() throws Exception {
        mockMvc.perform(get("/api/v1/gestionner/livreur/search")
                        .param("keyword", "Integration")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void getLivreurColisCounts_shouldReturnPage() throws Exception {
        mockMvc.perform(get("/api/v1/gestionner/livreur/counts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void getAssignedColis_shouldReturnPagedList() throws Exception {
        LivreurRequest request = createSampleLivreurRequest();
        String responseContent = mockMvc.perform(post("/api/v1/gestionner/livreur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        String livreurId = objectMapper.readTree(responseContent).get("id").asText();

        mockMvc.perform(get("/api/v1/gestionner/livreur/{livreurId}/colis", livreurId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
