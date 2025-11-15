package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.service.ClientDestinataireService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ClientDestinataireController.class)
public class ClientDestinataireControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ClientDestinataireService clientDestinataireService;


    private ClientDestinataireRequest createSampleClientRequest() {
        ClientDestinataireRequest req = new ClientDestinataireRequest();
        req.setNom("Martin");
        req.setPrenom("Jean");
        req.setEmail("jean.dupont@example.com");
        req.setTelephone("+33612345678");
        req.setAdresse("456 Avenue des Champs, 75008 Paris");
        return req;
    }

    private ClientDestinataireResponse createSampleClientResponse(String id, String role) {
        ClientDestinataireResponse response = new ClientDestinataireResponse();
        response.setId(id);
        response.setNom("Dupont");
        response.setPrenom("Jean");
        response.setEmail("jean.dupont@example.com");
        response.setTelephone("+33612345678");
        response.setAdresse("123 Rue de Paris, 75001 Paris");
        response.setRole(role);
        return response;
    }

    @Test
     void createExpediteur_should_create_and_return_created_status() throws Exception {
        ClientDestinataireRequest request = createSampleClientRequest();
        String clientId = "12731273182";
        ClientDestinataireResponse response = createSampleClientResponse(clientId, "Expéditeur");

        when(clientDestinataireService.createExpediteur(request)).thenReturn(response);
        mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.nom", is("Dupont")));
    }

    @Test
     void createExpediteur_should_return_bad_request_when_validation_fails() throws Exception {
        ClientDestinataireRequest request = new ClientDestinataireRequest();
        mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
     void createExpediteur_should_validate_email_format() throws Exception {
        ClientDestinataireRequest request = createSampleClientRequest();
        request.setEmail("invalid-email");

        mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
     void createDestinataire_should_create_and_return_created_status() throws Exception {
        ClientDestinataireRequest request = createSampleClientRequest();
        String clientId = UUID.randomUUID().toString();
        ClientDestinataireResponse response = createSampleClientResponse(clientId, "DESTINATAIRE");

        when(clientDestinataireService.createDestinataire(any(ClientDestinataireRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v3/clients/register/destinataire")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.nom", is("Dupont")))
                .andExpect(jsonPath("$.role", is("DESTINATAIRE")));
    }

    @Test
     void createDestinataire_should_return_bad_request_when_validation_fails() throws Exception {
        ClientDestinataireRequest request = new ClientDestinataireRequest();
        request.setEmail("test@example.com"); // Only email, missing other required fields

        mockMvc.perform(post("/api/v3/clients/register/destinataire")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
     void getClientById_should_return_client_details() throws Exception {
        String clientId = UUID.randomUUID().toString();
        ClientDestinataireResponse response = createSampleClientResponse(clientId, "EXPEDITEUR");

        when(clientDestinataireService.getClientById(clientId)).thenReturn(response);

        mockMvc.perform(get("/api/v3/clients/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.nom", is("Dupont")))
                .andExpect(jsonPath("$.prenom", is("Jean")))
                .andExpect(jsonPath("$.email", is("jean.dupont@example.com")));
    }

    @Test
     void updateClient_should_update_and_return_ok() throws Exception {
        String clientId = UUID.randomUUID().toString();
        ClientDestinataireRequest request = createSampleClientRequest();
        request.setNom("Martin");
        request.setAdresse("456 Avenue des Champs, 75008 Paris");

        ClientDestinataireResponse response = createSampleClientResponse(clientId, "EXPEDITEUR");
        response.setNom("Martin");
        response.setAdresse("456 Avenue des Champs, 75008 Paris");

        when(clientDestinataireService.updateClient(eq(clientId), any(ClientDestinataireRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v3/clients/{clientId}", clientId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.nom", is("Martin")))
                .andExpect(jsonPath("$.adresse", is("456 Avenue des Champs, 75008 Paris")));
    }

    @Test
     void updateClient_should_return_bad_request_when_validation_fails() throws Exception {
        String clientId = UUID.randomUUID().toString();
        ClientDestinataireRequest request = new ClientDestinataireRequest();
        // Missing required fields

        mockMvc.perform(put("/api/v3/clients/{clientId}", clientId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
     void deleteClient_should_delete_and_return_success_message() throws Exception {
        String clientId = UUID.randomUUID().toString();

        doNothing().when(clientDestinataireService).deleteClient(clientId);

        mockMvc.perform(delete("/api/v3/clients/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Client deleted successfully")))
                .andExpect(content().string(containsString(clientId)));
    }

    @Test
     void getAllClients_should_return_paginated_list() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDestinataireResponse> clientList = List.of(
                createSampleClientResponse(UUID.randomUUID().toString(), "EXPEDITEUR"),
                createSampleClientResponse(UUID.randomUUID().toString(), "DESTINATAIRE")
        );
        Page<ClientDestinataireResponse> page = new PageImpl<>(clientList, pageable, clientList.size());

        when(clientDestinataireService.getAllClients(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v3/clients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "dateCreation")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[0].role", is("EXPEDITEUR")))
                .andExpect(jsonPath("$.content[1].role", is("DESTINATAIRE")));
    }

    @Test
     void getAllClients_should_support_ascending_sort() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDestinataireResponse> clientList = List.of(
                createSampleClientResponse(UUID.randomUUID().toString(), "EXPEDITEUR")
        );
        Page<ClientDestinataireResponse> page = new PageImpl<>(clientList, pageable, clientList.size());

        when(clientDestinataireService.getAllClients(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v3/clients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "nom")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
     void searchClients_should_return_filtered_results_by_keyword() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDestinataireResponse> clientList = List.of(
                createSampleClientResponse(UUID.randomUUID().toString(), "EXPEDITEUR")
        );
        Page<ClientDestinataireResponse> page = new PageImpl<>(clientList, pageable, clientList.size());

        when(clientDestinataireService.searchClients(eq("Dupont"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v3/clients/search")
                        .param("keyword", "Dupont")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nom", is("Dupont")));
    }

    @Test
     void searchClients_should_search_by_email() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDestinataireResponse> clientList = List.of(
                createSampleClientResponse(UUID.randomUUID().toString(), "DESTINATAIRE")
        );
        Page<ClientDestinataireResponse> page = new PageImpl<>(clientList, pageable, clientList.size());

        when(clientDestinataireService.searchClients(eq("jean.dupont@example.com"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v3/clients/search")
                        .param("keyword", "jean.dupont@example.com")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is("jean.dupont@example.com")));
    }

    @Test
     void searchClients_should_search_by_telephone() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDestinataireResponse> clientList = List.of(
                createSampleClientResponse(UUID.randomUUID().toString(), "EXPEDITEUR")
        );
        Page<ClientDestinataireResponse> page = new PageImpl<>(clientList, pageable, clientList.size());

        when(clientDestinataireService.searchClients(eq("+33612345678"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v3/clients/search")
                        .param("keyword", "+33612345678")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].telephone", is("+33612345678")));
    }

    @Test
     void createExpediteur_should_validate_all_required_fields() throws Exception {
        ClientDestinataireRequest request = new ClientDestinataireRequest();
        request.setNom(""); // Blank nom
        request.setPrenom(""); // Blank prenom
        request.setEmail("invalid"); // Invalid email
        request.setTelephone(""); // Blank telephone
        request.setAdresse(""); // Blank adresse

        mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
     void getAllClients_should_use_default_pagination_params() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDestinataireResponse> clientList = List.of(
                createSampleClientResponse(UUID.randomUUID().toString(), "EXPEDITEUR")
        );
        Page<ClientDestinataireResponse> page = new PageImpl<>(clientList, pageable, clientList.size());

        when(clientDestinataireService.getAllClients(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v3/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
}
