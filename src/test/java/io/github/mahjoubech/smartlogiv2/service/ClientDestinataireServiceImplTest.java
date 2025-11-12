package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.mapper.ClientDestinataireMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.ClientExpediteur;
import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import io.github.mahjoubech.smartlogiv2.repository.ClientExpediteurRepository;
import io.github.mahjoubech.smartlogiv2.repository.DestinataireRepository;
import io.github.mahjoubech.smartlogiv2.service.impl.ClientDestinataireServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientDestinataireServiceImplTest {

    @Mock
    private ClientExpediteurRepository expediteurRepository;

    @Mock
    private DestinataireRepository destinataireRepository;

    @Mock
    private ClientDestinataireMapper clientDestinataireMapper;

    @InjectMocks
    private ClientDestinataireServiceImpl clientDestinataireService;

    private ClientDestinataireRequest request;
    private ClientExpediteur expediteur;
    private Destinataire destinataire;
    private ClientDestinataireResponse response;

    private final String CLIENT_ID = "CLI-001";
    private final String EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        request = new ClientDestinataireRequest();
        request.setEmail(EMAIL);
        request.setNom("Test");
        request.setPrenom("User");
        request.setTelephone("0612345678");

        expediteur = new ClientExpediteur();
        expediteur.setId(CLIENT_ID);
        expediteur.setEmail(EMAIL);
        expediteur.setNom("Test");
        expediteur.setPrenom("User");

        destinataire = new Destinataire();
        destinataire.setId(CLIENT_ID);
        destinataire.setEmail(EMAIL);
        destinataire.setNom("Test");
        destinataire.setPrenom("User");

        response = new ClientDestinataireResponse();
        response.setId(CLIENT_ID);
        response.setEmail(EMAIL);
    }
    @Test
    void findClientOrDestinataire_shouldReturnExpediteur_whenTargetTypeIsClientExpediteur() throws Exception {
        when(expediteurRepository.findById(CLIENT_ID)).thenReturn(Optional.of(expediteur));

        // Use reflection to access private method
        Method method = ClientDestinataireServiceImpl.class.getDeclaredMethod(
                "findClientOrDestinataire", String.class, Class.class
        );
        method.setAccessible(true);

        Optional<ClientExpediteur> result = (Optional<ClientExpediteur>) method.invoke(
                clientDestinataireService, CLIENT_ID, ClientExpediteur.class
        );

        assertTrue(result.isPresent());
        assertEquals(CLIENT_ID, result.get().getId());
        verify(expediteurRepository).findById(CLIENT_ID);
        verify(destinataireRepository, never()).findById(anyString());
    }

    @Test
    void findClientOrDestinataire_shouldReturnDestinataire_whenTargetTypeIsDestinataire() throws Exception {
        when(destinataireRepository.findById(CLIENT_ID)).thenReturn(Optional.of(destinataire));

        // Use reflection to access private method
        Method method = ClientDestinataireServiceImpl.class.getDeclaredMethod(
                "findClientOrDestinataire", String.class, Class.class
        );
        method.setAccessible(true);

        Optional<Destinataire> result = (Optional<Destinataire>) method.invoke(
                clientDestinataireService, CLIENT_ID, Destinataire.class
        );

        assertTrue(result.isPresent());
        assertEquals(CLIENT_ID, result.get().getId());
        verify(destinataireRepository).findById(CLIENT_ID);
        verify(expediteurRepository, never()).findById(anyString());
    }

    @Test
    void findClientOrDestinataire_shouldReturnEmptyOptional_whenTargetTypeIsUnknown() throws Exception {
        // Use reflection to access private method
        Method method = ClientDestinataireServiceImpl.class.getDeclaredMethod(
                "findClientOrDestinataire", String.class, Class.class
        );
        method.setAccessible(true);

        Optional<String> result = (Optional<String>) method.invoke(
                clientDestinataireService, CLIENT_ID, String.class
        );

        assertFalse(result.isPresent());
        verify(expediteurRepository, never()).findById(anyString());
        verify(destinataireRepository, never()).findById(anyString());
    }

    @Test
    void findClientOrDestinataire_shouldReturnEmptyOptional_whenExpediteurNotFound() throws Exception {
        when(expediteurRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

        // Use reflection to access private method
        Method method = ClientDestinataireServiceImpl.class.getDeclaredMethod(
                "findClientOrDestinataire", String.class, Class.class
        );
        method.setAccessible(true);

        Optional<ClientExpediteur> result = (Optional<ClientExpediteur>) method.invoke(
                clientDestinataireService, CLIENT_ID, ClientExpediteur.class);

        assertFalse(result.isPresent());
        verify(expediteurRepository).findById(CLIENT_ID);
    }

    @Test
    void findClientOrDestinataire_shouldReturnEmptyOptional_whenDestinataireNotFound() throws Exception {
        when(destinataireRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

        // Use reflection to access private method
        Method method = ClientDestinataireServiceImpl.class.getDeclaredMethod(
                "findClientOrDestinataire", String.class, Class.class
        );
        method.setAccessible(true);

        Optional<Destinataire> result = (Optional<Destinataire>) method.invoke(
                clientDestinataireService, CLIENT_ID, Destinataire.class
        );

        assertFalse(result.isPresent());
        verify(destinataireRepository).findById(CLIENT_ID);
    }

    // ========== CREATE EXPEDITEUR TESTS ==========

    @Test
    void createExpediteur_shouldCreateSuccessfully_whenEmailDoesNotExist() {
        when(expediteurRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(clientDestinataireMapper.toClientExpediteur(request)).thenReturn(expediteur);
        when(expediteurRepository.save(expediteur)).thenReturn(expediteur);
        when(clientDestinataireMapper.toClientResponse(expediteur)).thenReturn(response);

        ClientDestinataireResponse result = clientDestinataireService.createExpediteur(request);

        assertNotNull(result);
        assertEquals(CLIENT_ID, result.getId());
        assertEquals(EMAIL, result.getEmail());
        verify(expediteurRepository).findByEmail(EMAIL);
        verify(expediteurRepository).save(expediteur);
        verify(clientDestinataireMapper).toClientExpediteur(request);
        verify(clientDestinataireMapper).toClientResponse(expediteur);
    }

    @Test
    void createExpediteur_shouldThrowException_whenEmailAlreadyExists() {
        when(expediteurRepository.findByEmail(EMAIL)).thenReturn(Optional.of(expediteur));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clientDestinataireService.createExpediteur(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        assertTrue(exception.getMessage().contains(EMAIL));
        verify(expediteurRepository).findByEmail(EMAIL);
        verify(expediteurRepository, never()).save(any());
    }

    // ========== CREATE DESTINATAIRE TESTS ==========

    @Test
    void createDestinataire_shouldCreateSuccessfully_whenEmailDoesNotExist() {
        when(destinataireRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(clientDestinataireMapper.toDestinataire(request)).thenReturn(destinataire);
        when(destinataireRepository.save(destinataire)).thenReturn(destinataire);
        when(clientDestinataireMapper.toDestinataireResponse(destinataire)).thenReturn(response);

        ClientDestinataireResponse result = clientDestinataireService.createDestinataire(request);

        assertNotNull(result);
        assertEquals(CLIENT_ID, result.getId());
        assertEquals(EMAIL, result.getEmail());
        verify(destinataireRepository).findByEmail(EMAIL);
        verify(destinataireRepository).save(destinataire);
        verify(clientDestinataireMapper).toDestinataire(request);
        verify(clientDestinataireMapper).toDestinataireResponse(destinataire);
    }

    @Test
    void createDestinataire_shouldThrowException_whenEmailAlreadyExists() {
        when(destinataireRepository.findByEmail(EMAIL)).thenReturn(Optional.of(destinataire));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clientDestinataireService.createDestinataire(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        assertTrue(exception.getMessage().contains(EMAIL));
        verify(destinataireRepository).findByEmail(EMAIL);
        verify(destinataireRepository, never()).save(any());
    }

    // ========== GET CLIENT BY ID TESTS ==========

    @Test
    void getClientById_shouldReturnExpediteur_whenExpediteurExists() {
        when(expediteurRepository.findById(CLIENT_ID)).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());
        when(clientDestinataireMapper.toClientResponse(expediteur)).thenReturn(response);

        ClientDestinataireResponse result = clientDestinataireService.getClientById(CLIENT_ID);

        assertNotNull(result);
        assertEquals(CLIENT_ID, result.getId());
        verify(expediteurRepository).findById(CLIENT_ID);
        verify(clientDestinataireMapper).toClientResponse(expediteur);
        verify(clientDestinataireMapper, never()).toDestinataireResponse(any());
    }

    @Test
    void getClientById_shouldReturnDestinataire_whenOnlyDestinataireExists() {
        when(expediteurRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());
        when(destinataireRepository.findById(CLIENT_ID)).thenReturn(Optional.of(destinataire));
        when(clientDestinataireMapper.toDestinataireResponse(destinataire)).thenReturn(response);

        ClientDestinataireResponse result = clientDestinataireService.getClientById(CLIENT_ID);

        assertNotNull(result);
        assertEquals(CLIENT_ID, result.getId());
        verify(expediteurRepository).findById(CLIENT_ID);
        verify(destinataireRepository).findById(CLIENT_ID);
        verify(clientDestinataireMapper).toDestinataireResponse(destinataire);
        verify(clientDestinataireMapper, never()).toClientResponse(any());
    }

    @Test
    void getClientById_shouldThrowException_whenClientNotFound() {
        when(expediteurRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());
        when(destinataireRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clientDestinataireService.getClientById(CLIENT_ID)
        );

        assertTrue(exception.getMessage().contains(CLIENT_ID));
        verify(expediteurRepository).findById(CLIENT_ID);
        verify(destinataireRepository).findById(CLIENT_ID);
    }

    // ========== UPDATE CLIENT TESTS ==========

    @Test
    void updateClient_shouldUpdateExpediteur_whenExpediteurExists() {
        when(expediteurRepository.findById(CLIENT_ID)).thenReturn(Optional.of(expediteur));
        doNothing().when(clientDestinataireMapper).updateExpediteur(request, expediteur);
        when(expediteurRepository.save(expediteur)).thenReturn(expediteur);
        when(clientDestinataireMapper.toClientResponse(expediteur)).thenReturn(response);

        ClientDestinataireResponse result = clientDestinataireService.updateClient(CLIENT_ID, request);

        assertNotNull(result);
        assertEquals(CLIENT_ID, result.getId());
        verify(expediteurRepository).findById(CLIENT_ID);
        verify(clientDestinataireMapper).updateExpediteur(request, expediteur);
        verify(expediteurRepository).save(expediteur);
        verify(destinataireRepository, never()).findById(any());
    }

    @Test
    void updateClient_shouldUpdateDestinataire_whenOnlyDestinataireExists() {
        when(expediteurRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());
        when(destinataireRepository.findById(CLIENT_ID)).thenReturn(Optional.of(destinataire));
        doNothing().when(clientDestinataireMapper).updateDestinataire(request, destinataire);
        when(destinataireRepository.save(destinataire)).thenReturn(destinataire);
        when(clientDestinataireMapper.toDestinataireResponse(destinataire)).thenReturn(response);

        ClientDestinataireResponse result = clientDestinataireService.updateClient(CLIENT_ID, request);

        assertNotNull(result);
        assertEquals(CLIENT_ID, result.getId());
        verify(expediteurRepository).findById(CLIENT_ID);
        verify(destinataireRepository).findById(CLIENT_ID);
        verify(clientDestinataireMapper).updateDestinataire(request, destinataire);
        verify(destinataireRepository).save(destinataire);
    }

    @Test
    void updateClient_shouldThrowException_whenClientNotFound() {
        when(expediteurRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());
        when(destinataireRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clientDestinataireService.updateClient(CLIENT_ID, request)
        );

        assertTrue(exception.getMessage().contains(CLIENT_ID));
        verify(expediteurRepository).findById(CLIENT_ID);
        verify(destinataireRepository).findById(CLIENT_ID);
        verify(expediteurRepository, never()).save(any());
        verify(destinataireRepository, never()).save(any());
    }

    // ========== DELETE CLIENT TESTS ==========

    @Test
    void deleteClient_shouldDeleteExpediteur_whenExpediteurExists() {
        when(expediteurRepository.existsById(CLIENT_ID)).thenReturn(true);
        doNothing().when(expediteurRepository).deleteById(CLIENT_ID);

        assertDoesNotThrow(() -> clientDestinataireService.deleteClient(CLIENT_ID));

        verify(expediteurRepository).existsById(CLIENT_ID);
        verify(expediteurRepository).deleteById(CLIENT_ID);
        verify(destinataireRepository, never()).existsById(any());
    }

    @Test
    void deleteClient_shouldDeleteDestinataire_whenOnlyDestinataireExists() {
        when(expediteurRepository.existsById(CLIENT_ID)).thenReturn(false);
        when(destinataireRepository.existsById(CLIENT_ID)).thenReturn(true);
        doNothing().when(destinataireRepository).deleteById(CLIENT_ID);

        assertDoesNotThrow(() -> clientDestinataireService.deleteClient(CLIENT_ID));

        verify(expediteurRepository).existsById(CLIENT_ID);
        verify(destinataireRepository).existsById(CLIENT_ID);
        verify(destinataireRepository).deleteById(CLIENT_ID);
    }

    @Test
    void deleteClient_shouldThrowException_whenClientNotFound() {
        when(expediteurRepository.existsById(CLIENT_ID)).thenReturn(false);
        when(destinataireRepository.existsById(CLIENT_ID)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> clientDestinataireService.deleteClient(CLIENT_ID)
        );

        assertTrue(exception.getMessage().contains("not found for deletion"));
        assertTrue(exception.getMessage().contains(CLIENT_ID));
        verify(expediteurRepository).existsById(CLIENT_ID);
        verify(destinataireRepository).existsById(CLIENT_ID);
        verify(expediteurRepository, never()).deleteById(any());
        verify(destinataireRepository, never()).deleteById(any());
    }

    // ========== GET ALL CLIENTS TESTS ==========

    @Test
    void getAllClients_shouldReturnCombinedPage_whenBothExist() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientExpediteur> expPage = new PageImpl<>(
                Arrays.asList(expediteur),
                pageable,
                1
        );
        Page<Destinataire> destPage = new PageImpl<>(
                Arrays.asList(destinataire),
                pageable,
                1
        );

        when(expediteurRepository.findAll(pageable)).thenReturn(expPage);
        when(destinataireRepository.findAll(pageable)).thenReturn(destPage);
        when(clientDestinataireMapper.toClientResponse(expediteur)).thenReturn(response);
        when(clientDestinataireMapper.toDestinataireResponse(destinataire)).thenReturn(response);

        Page<ClientDestinataireResponse> result = clientDestinataireService.getAllClients(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(expediteurRepository).findAll(pageable);
        verify(destinataireRepository).findAll(pageable);
    }

    @Test
    void getAllClients_shouldReturnEmptyPage_whenNoClientsExist() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientExpediteur> expPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Page<Destinataire> destPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(expediteurRepository.findAll(pageable)).thenReturn(expPage);
        when(destinataireRepository.findAll(pageable)).thenReturn(destPage);

        Page<ClientDestinataireResponse> result = clientDestinataireService.getAllClients(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void getAllClients_shouldReturnOnlyExpediteurs_whenNoDestinataires() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientExpediteur> expPage = new PageImpl<>(
                Arrays.asList(expediteur),
                pageable,
                1
        );
        Page<Destinataire> destPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(expediteurRepository.findAll(pageable)).thenReturn(expPage);
        when(destinataireRepository.findAll(pageable)).thenReturn(destPage);
        when(clientDestinataireMapper.toClientResponse(expediteur)).thenReturn(response);

        Page<ClientDestinataireResponse> result = clientDestinataireService.getAllClients(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllClients_shouldReturnOnlyDestinataires_whenNoExpediteurs() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientExpediteur> expPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Page<Destinataire> destPage = new PageImpl<>(
                Arrays.asList(destinataire),
                pageable,
                1
        );

        when(expediteurRepository.findAll(pageable)).thenReturn(expPage);
        when(destinataireRepository.findAll(pageable)).thenReturn(destPage);
        when(clientDestinataireMapper.toDestinataireResponse(destinataire)).thenReturn(response);

        Page<ClientDestinataireResponse> result = clientDestinataireService.getAllClients(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    // ========== SEARCH CLIENTS TESTS ==========

    @Test
    void searchClients_shouldReturnCombinedResults_whenBothMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "test";

        Page<ClientExpediteur> expPage = new PageImpl<>(
                Arrays.asList(expediteur),
                pageable,
                1
        );
        Page<Destinataire> destPage = new PageImpl<>(
                Arrays.asList(destinataire),
                pageable,
                1
        );

        when(expediteurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(expPage);

        when(destinataireRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(destPage);

        when(clientDestinataireMapper.toClientResponse(expediteur)).thenReturn(response);
        when(clientDestinataireMapper.toDestinataireResponse(destinataire)).thenReturn(response);

        Page<ClientDestinataireResponse> result = clientDestinataireService.searchClients(keyword, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void searchClients_shouldReturnEmptyPage_whenNoMatches() {
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "nonexistent";

        Page<ClientExpediteur> expPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Page<Destinataire> destPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(expediteurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(expPage);

        when(destinataireRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(destPage);

        Page<ClientDestinataireResponse> result = clientDestinataireService.searchClients(keyword, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void searchClients_shouldReturnOnlyExpediteurs_whenOnlyExpediteursMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "exp";

        Page<ClientExpediteur> expPage = new PageImpl<>(
                Arrays.asList(expediteur),
                pageable,
                1
        );
        Page<Destinataire> destPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(expediteurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(expPage);

        when(destinataireRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(destPage);

        when(clientDestinataireMapper.toClientResponse(expediteur)).thenReturn(response);

        Page<ClientDestinataireResponse> result = clientDestinataireService.searchClients(keyword, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void searchClients_shouldReturnOnlyDestinataires_whenOnlyDestinatairesMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "dest";

        Page<ClientExpediteur> expPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Page<Destinataire> destPage = new PageImpl<>(
                Arrays.asList(destinataire),
                pageable,
                1
        );

        when(expediteurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(expPage);

        when(destinataireRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(destPage);

        when(clientDestinataireMapper.toDestinataireResponse(destinataire)).thenReturn(response);

        Page<ClientDestinataireResponse> result = clientDestinataireService.searchClients(keyword, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void searchClients_shouldHandleMultipleResults_correctly() {
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "test";

        ClientExpediteur expediteur2 = new ClientExpediteur();
        expediteur2.setId("CLI-002");
        expediteur2.setEmail("test2@example.com");

        Destinataire destinataire2 = new Destinataire();
        destinataire2.setId("CLI-003");
        destinataire2.setEmail("test3@example.com");

        Page<ClientExpediteur> expPage = new PageImpl<>(
                Arrays.asList(expediteur, expediteur2),
                pageable,
                2
        );
        Page<Destinataire> destPage = new PageImpl<>(
                Arrays.asList(destinataire, destinataire2),
                pageable,
                2
        );

        when(expediteurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(expPage);

        when(destinataireRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        )).thenReturn(destPage);

        ClientDestinataireResponse response1 = new ClientDestinataireResponse();
        response1.setId("CLI-001");
        ClientDestinataireResponse response2 = new ClientDestinataireResponse();
        response2.setId("CLI-002");
        ClientDestinataireResponse response3 = new ClientDestinataireResponse();
        response3.setId("CLI-003");
        ClientDestinataireResponse response4 = new ClientDestinataireResponse();
        response4.setId("CLI-004");

        when(clientDestinataireMapper.toClientResponse(expediteur)).thenReturn(response1);
        when(clientDestinataireMapper.toClientResponse(expediteur2)).thenReturn(response2);
        when(clientDestinataireMapper.toDestinataireResponse(destinataire)).thenReturn(response3);
        when(clientDestinataireMapper.toDestinataireResponse(destinataire2)).thenReturn(response4);

        Page<ClientDestinataireResponse> result = clientDestinataireService.searchClients(keyword, pageable);

        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(4, result.getContent().size());
    }
}
