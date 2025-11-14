package io.github.mahjoubech.smartlogiv2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.service.LivreurService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LivreurController.class)
public class LivreurControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private LivreurService livreurService;

    private LivreurRequest createSampleLivreurRequest() {
        LivreurRequest request = new LivreurRequest();
        request.setNom("livreur 1");
        request.setPrenom("mol chi");
        request.setTelephone("06526325");
        request.setVehicule("dacia");
        request.setZoneAssigneeId("1526352");
        return request;
    }
    private LivreurResponse createSampleLivreurResponse(String id) {
        LivreurResponse response = new LivreurResponse();
        response.setId(id);
        response.setNom("livreur 2");
        response.setPrenom("mol chi");
        response.setTelephone("06526325");
        response.setVehicule("dacia");
        ZoneResponse zoneResponse = new ZoneResponse();
        zoneResponse.setId(UUID.randomUUID().toString());
        zoneResponse.setNom("casa");
        zoneResponse.setCodePostal("200000");
        response.setZoneAssignee(zoneResponse);
        return response;
    }
    private LivreurColisResponse createSampleLivreurColisResponse() {
        LivreurColisResponse response = new LivreurColisResponse();
        response.setNomComplet("livreur casa");
        response.setColisCont(3);
        return response;
    }
    private ColisResponse createSampleColisResponse(String id) {
        ColisResponse response = new ColisResponse();
        response.setId(id);
        response.setDescription("colis livreur");
        response.setPoids(1.0);
        response.setStatut("EN_TRANSIT");
        return response;
    }

    @Test
    public void createLivreur_should_create_and_return_created_status() throws Exception {
        LivreurRequest request = createSampleLivreurRequest();
        String id = UUID.randomUUID().toString();
        LivreurResponse response = createSampleLivreurResponse(id);

        when(livreurService.createLivreur(any(LivreurRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/gestionner/livreur")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.nom", is("livreur 2")))
                .andExpect(jsonPath("$.telephone", is("06526325")));
    }

    @Test
    public void getAllLivreurs_should_return_paginated_list() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<LivreurResponse> livreurList = List.of(
                createSampleLivreurResponse(UUID.randomUUID().toString()),
                createSampleLivreurResponse(UUID.randomUUID().toString())
        );
        Page<LivreurResponse> page = new PageImpl<>(livreurList, pageable, livreurList.size());

        when(livreurService.getAllLivreurs(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/gestionner/livreur")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void getLivreurById_should_return_details() throws Exception {
        String id = UUID.randomUUID().toString();
        LivreurResponse response = createSampleLivreurResponse(id);

        when(livreurService.getLivreurById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/gestionner/livreur/{livreur_id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.nom", is("livreur 2")));
    }

    @Test
    public void updateLivreur_should_update_and_return_ok() throws Exception {
        String id = UUID.randomUUID().toString();
        LivreurRequest request = createSampleLivreurRequest();
        LivreurResponse response = createSampleLivreurResponse(id);
        response.setNom("updated nom");

        when(livreurService.updateLivreur(eq(id), any(LivreurRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/gestionner/livreur/{livreur_id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.nom", is("updated nom")));
    }

    @Test
    public void deleteLivreur_should_delete_and_return_success_message() throws Exception {
        String id = UUID.randomUUID().toString();

        doNothing().when(livreurService).deleteLivreur(id);

        mockMvc.perform(delete("/api/v1/gestionner/livreur/{livreur_id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Livreur deleted successfully with ID: " + id)));
    }

    @Test
    public void searchLivreurs_should_return_paginated_search_results() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<LivreurResponse> livreurList = List.of(
                createSampleLivreurResponse(UUID.randomUUID().toString())
        );
        Page<LivreurResponse> page = new PageImpl<>(livreurList, pageable, livreurList.size());

        when(livreurService.searchLivreurs(eq("casa"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/gestionner/livreur/search")
                        .param("keyword", "casa")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void getLivreurColisCounts_should_return_paginated_counts() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<LivreurColisResponse> counts = List.of(createSampleLivreurColisResponse());
        Page<LivreurColisResponse> page = new PageImpl<>(counts, pageable, counts.size());

        when(livreurService.getLivreurColisCounts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/gestionner/livreur/counts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nomComplet", is("livreur casa")))
                .andExpect(jsonPath("$.content[0].colisCont", is(3)));
    }

    @Test
    public void getAssignedColis_should_return_livreur_colis() throws Exception {
        String livreurId = UUID.randomUUID().toString();
        Pageable pageable = PageRequest.of(0, 10);
        List<ColisResponse> colisList = List.of(
                createSampleColisResponse(UUID.randomUUID().toString())
        );
        Page<ColisResponse> page = new PageImpl<>(colisList, pageable, colisList.size());

        when(livreurService.getAssignedColis(eq(livreurId), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/gestionner/livreur/{livreurId}/colis", livreurId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].description", is("colis livreur")));
    }
    @Test
    public void searchLivreurs_keywordNull_should_useEmptyString() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<LivreurResponse> livreurs = List.of(createSampleLivreurResponse(UUID.randomUUID().toString()));
        Page<LivreurResponse> page = new PageImpl<>(livreurs, pageable, livreurs.size());

        when(livreurService.searchLivreurs(eq(""), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/gestionner/livreur/search")
                        // No keyword param --> null
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void searchLivreurs_keywordNotNull_should_search_withKeyword() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<LivreurResponse> livreurs = List.of(createSampleLivreurResponse(UUID.randomUUID().toString()));
        Page<LivreurResponse> page = new PageImpl<>(livreurs, pageable, livreurs.size());

        when(livreurService.searchLivreurs(eq("test"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/gestionner/livreur/search")
                        .param("keyword", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
    @Test
    public void getAllLivreurs_should_sort_ascending() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<LivreurResponse> livreurs = List.of(createSampleLivreurResponse(UUID.randomUUID().toString()));
        Page<LivreurResponse> page = new PageImpl<>(livreurs, pageable, livreurs.size());

        when(livreurService.getAllLivreurs(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/gestionner/livreur")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void getAllLivreurs_should_sort_descending() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        List<LivreurResponse> livreurs = List.of(createSampleLivreurResponse(UUID.randomUUID().toString()));
        Page<LivreurResponse> page = new PageImpl<>(livreurs, pageable, livreurs.size());

        when(livreurService.getAllLivreurs(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/gestionner/livreur")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

}
