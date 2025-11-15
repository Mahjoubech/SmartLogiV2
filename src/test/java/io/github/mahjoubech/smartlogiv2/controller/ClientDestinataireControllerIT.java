package io.github.mahjoubech.smartlogiv2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.repository.ClientExpediteurRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClientDestinataireControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientExpediteurRepository clientDestinataireRepository;

    @BeforeEach
    public void setUp() {
        // Pre-test cleanup if needed
        clientDestinataireRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        clientDestinataireRepository.deleteAll();
    }

    private ClientDestinataireRequest createSampleClientRequest() {
        ClientDestinataireRequest req = new ClientDestinataireRequest();
        req.setNom("Dupont");
        req.setPrenom("Jean");
        req.setEmail("jean.dupont@example.com");
        req.setTelephone("+33612345678");
        req.setAdresse("456 Avenue des Champs, 75008 Paris");
        return req;
    }

    @Test
    public void createExpediteur_shouldCreateAndReturnCreated() throws Exception {
        ClientDestinataireRequest request = createSampleClientRequest();

        String responseString = mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom", is("Dupont")))
                .andExpect(jsonPath("$.prenom", is("Jean")))
                .andExpect(jsonPath("$.email", is("jean.dupont@example.com")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClientDestinataireResponse response = objectMapper.readValue(responseString, ClientDestinataireResponse.class);
        assertNotNull(response.getId());
        assertEquals("Dupont", response.getNom());

        // Verify persistence
        var persisted = clientDestinataireRepository.findById(response.getId());
        assertTrue(persisted.isPresent());
        assertEquals("Dupont", persisted.get().getNom());
    }

    @Test
    public void createDestinataire_shouldCreateSuccessfully() throws Exception {
        ClientDestinataireRequest request = createSampleClientRequest();
        request.setEmail("destinataire@example.com"); // Make email unique to avoid conflicts

        mockMvc.perform(post("/api/v3/clients/register/destinataire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("destinataire@example.com")));
    }

    @Test
    public void getClientById_shouldReturnClientDetails() throws Exception {
        // Create first
        ClientDestinataireRequest request = createSampleClientRequest();
        String createResponse = mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        ClientDestinataireResponse created = objectMapper.readValue(createResponse, ClientDestinataireResponse.class);

        // Fetch by ID
        mockMvc.perform(get("/api/v3/clients/{clientId}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId())))
                .andExpect(jsonPath("$.nom", is("Dupont")));
    }

    @Test
    public void updateClient_shouldUpdateAndReturnOk() throws Exception {
        ClientDestinataireRequest request = createSampleClientRequest();
        String createResponse = mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        ClientDestinataireResponse created = objectMapper.readValue(createResponse, ClientDestinataireResponse.class);

        ClientDestinataireRequest updateRequest = createSampleClientRequest();
        updateRequest.setNom("UpdatedName");
        updateRequest.setAdresse("New Address 1234");

        mockMvc.perform(put("/api/v3/clients/{clientId}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("UpdatedName")))
                .andExpect(jsonPath("$.adresse", is("New Address 1234")));
    }

    @Test
    public void deleteClient_shouldDeleteAndReturnOk() throws Exception {
        ClientDestinataireRequest request = createSampleClientRequest();
        String createResponse = mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        ClientDestinataireResponse created = objectMapper.readValue(createResponse, ClientDestinataireResponse.class);

        mockMvc.perform(delete("/api/v3/clients/{clientId}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Client deleted successfully")));

        var deleted = clientDestinataireRepository.findById(created.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    public void getAllClients_shouldReturnPagedList() throws Exception {
        mockMvc.perform(get("/api/v3/clients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "dateCreation")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void searchClients_shouldReturnFilterByKeyword() throws Exception {
        ClientDestinataireRequest request = createSampleClientRequest();

        mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v3/clients/search")
                        .param("keyword", "Dupont")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nom", is("Dupont")));
    }

    @Test
    public void createExpediteur_shouldReturnBadRequestForInvalidInput() throws Exception {
        ClientDestinataireRequest invalidRequest = new ClientDestinataireRequest();
        invalidRequest.setNom("");

        mockMvc.perform(post("/api/v3/clients/register/expediteur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
