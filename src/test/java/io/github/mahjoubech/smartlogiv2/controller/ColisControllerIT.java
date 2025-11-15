package io.github.mahjoubech.smartlogiv2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ColisControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createZoneAndGetId() {
        try {
            String zoneJson = """
                    {
                        "nom": "Test Zone",
                        "codePostal": "75001"
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

    private void createClient(String email, boolean expediteur) throws Exception {
        ClientDestinataireRequest req = new ClientDestinataireRequest();
        req.setNom("AA");
        req.setPrenom("BB");
        req.setEmail(email);
        req.setAdresse("RR");
        req.setTelephone("077778888");
        String path = expediteur ? "/api/v3/clients/register/expediteur" : "/api/v3/clients/register/destinataire";
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    private String createColisAndGetId(String expediteurEmail, String destinataireEmail) throws Exception {
        ColisRequest req = new ColisRequest();
        req.setPoids(3.2);
        req.setDescription("TestColisDesc");
        req.setPriorite("NORMAL");
        req.setVilleDestination("Paris");
        req.setClientExpediteurEmail(expediteurEmail);
        req.setDestinataireEmail(destinataireEmail);
        req.setCodePostal("75001");
        req.setProduits(java.util.Collections.emptyList());

        String response = mockMvc.perform(post("/api/v2/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    void createColis_shouldPersistAndReturnCreated() throws Exception {
        createZoneAndGetId();
        createClient("expo@t.com", true);
        createClient("dest@t.com", false);

        ColisRequest req = new ColisRequest();
        req.setPoids(3.2);
        req.setDescription("TestColisDesc");
        req.setPriorite("NORMAL");
        req.setVilleDestination("Paris");
        req.setClientExpediteurEmail("expo@t.com");
        req.setDestinataireEmail("dest@t.com");
        req.setCodePostal("75001");
        req.setProduits(java.util.Collections.emptyList());

        mockMvc.perform(post("/api/v2/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("TestColisDesc")))
                .andExpect(jsonPath("$.priorite", is("NORMAL")))
                .andExpect(jsonPath("$.villeDestination", is("Paris")));
    }

    @Test
    void getAllColis_shouldReturnPagedList() throws Exception {
        mockMvc.perform(get("/api/v2/colis")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "dateCreation")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getColisById_shouldReturnDetails() throws Exception {
        createZoneAndGetId();
        createClient("expo@t.com", true);
        createClient("dest@t.com", false);

        String colisId = createColisAndGetId("expo@t.com", "dest@t.com");

        mockMvc.perform(get("/api/v2/colis/{colisId}", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(colisId)))
                .andExpect(jsonPath("$.description", is("TestColisDesc")));
    }

    @Test
    void updateColis_shouldUpdateAndReturnOk() throws Exception {
        createZoneAndGetId();
        createClient("expo@t.com", true);
        createClient("dest@t.com", false);

        String colisId = createColisAndGetId("expo@t.com", "dest@t.com");

        ColisRequest update = new ColisRequest();
        update.setPoids(5.0);
        update.setDescription("Update Desc");
        update.setPriorite("URGENT");
        update.setVilleDestination("Casablanca");
        update.setClientExpediteurEmail("expo@t.com");
        update.setDestinataireEmail("dest@t.com");
        update.setCodePostal("75001");
        update.setProduits(java.util.Collections.emptyList());

        mockMvc.perform(put("/api/v2/colis/{colisId}", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Update Desc")))
                .andExpect(jsonPath("$.priorite", is("URGENT")))
                .andExpect(jsonPath("$.villeDestination", is("Casablanca")));
    }

    @Test
    void deleteColis_shouldDeleteAndReturnOk() throws Exception {
        createZoneAndGetId();
        createClient("expo@t.com", true);
        createClient("dest@t.com", false);

        String colisId = createColisAndGetId("expo@t.com", "dest@t.com");

        // Remettre le statut à CREE si besoin

        HistoriqueLivraisonRequest histReq = new HistoriqueLivraisonRequest();
        histReq.setStatut("CREE");
        histReq.setCommentaire("pour suppression");
        mockMvc.perform(put("/api/v2/colis/{colisId}/status", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(histReq)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v2/colis/{colisId}", colisId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Colis deleted successfully")));
    }

    @Test
    void updateColisStatus_shouldUpdateStatus() throws Exception {
        createZoneAndGetId();
        createClient("expo@t.com", true);
        createClient("dest@t.com", false);
        String colisId = createColisAndGetId("expo@t.com", "dest@t.com");

        HistoriqueLivraisonRequest histReq = new HistoriqueLivraisonRequest();
        histReq.setStatut("EN_TRANSIT");
        histReq.setCommentaire("en cours");
        mockMvc.perform(put("/api/v2/colis/{colisId}/status", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(histReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut", is("EN_TRANSIT")));
    }

    // Pour assignation, il faut créer aussi un livreur lié à la zone
    private String createLivreurAndGetId(String zoneId) {
        try {
            String json = """
                    {
                        "nom": "LivreurTest",
                        "prenom": "Tst",
                        "telephone": "0111122223",
                        "vehicule": "Camion",
                        "zoneAssigneeId": "%s"
                    }
                    """.formatted(zoneId);

            String response = mockMvc.perform(post("/api/v1/gestionner/livreur")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
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
    void assignColisToLivreur_shouldAssignAndReturnOk() throws Exception {
        String zoneId = createZoneAndGetId();
        createClient("expo@t.com", true);
        createClient("dest@t.com", false);

        String colisId = createColisAndGetId("expo@t.com", "dest@t.com");
        String livreurId = createLivreurAndGetId(zoneId);

        mockMvc.perform(put("/api/v2/colis/gestionner/livreur/{colisId}/assign", colisId)
                        .param("livreurId", livreurId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(colisId)));
    }

    @Test
    void findColisByCriteria_shouldReturnPagedResult() throws Exception {
        mockMvc.perform(get("/api/v2/colis/search")
                        .param("statut", "CREE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getColisByExpediteur_shouldReturnPagedList() throws Exception {
        createZoneAndGetId();
        createClient("expo@t.com", true);
        createClient("dest@t.com", false);
        createColisAndGetId("expo@t.com", "dest@t.com");

        mockMvc.perform(get("/api/v2/colis/expediteur/{expediteurId}", "expo@t.com")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getColisHistory_shouldReturnPagedHistory() throws Exception {
        createZoneAndGetId();
        createClient("expo@t.com", true);
        createClient("dest@t.com", false);

        String colisId = createColisAndGetId("expo@t.com", "dest@t.com");

        mockMvc.perform(get("/api/v2/colis/{colisId}/history", colisId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getColisSummary_shouldReturnMap() throws Exception {
        mockMvc.perform(get("/api/v2/colis/summary")
                        .param("groupByField", "statut"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }
}
