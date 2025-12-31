package io.github.mahjoubech.smartlogiv2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.mapper.ProduitMapper;
import io.github.mahjoubech.smartlogiv2.mapper.ZoneMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import io.github.mahjoubech.smartlogiv2.model.entity.Produit;
import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.repository.ProduitRepository;
import io.github.mahjoubech.smartlogiv2.repository.ZoneRepository;
import io.github.mahjoubech.smartlogiv2.service.impl.LogisticsDataServiceImpl;
import jakarta.transaction.Transactional;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogisticsDataServiceImplTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ZoneMapper zoneMapper;
    @Mock
    private InputStream is;
    @Mock
    private ProduitMapper produitMapper;

    @InjectMocks
    private LogisticsDataServiceImpl logisticsDataService;

    private ZoneRequest zoneRequest;
    private Zone zone;
    private ZoneResponse zoneResponse;
    private ProduitRequest produitRequest;
    private Produit produit;
    private ProduitResponse produitResponse;

    private final String ZONE_ID = "ZONE-001";
    private final String CODE_POSTAL = "20000";
    private final String PRODUIT_ID = "PROD-001";

    @BeforeEach
    void setUp() {
        zoneRequest = new ZoneRequest();
        zoneRequest.setCodePostal(CODE_POSTAL);
        zoneRequest.setNom("Casablanca");

        zone = new Zone();
        zone.setId(ZONE_ID);
        zone.setCodePostal(CODE_POSTAL);
        zone.setNom("Casablanca");

        zoneResponse = new ZoneResponse();
        zoneResponse.setId(ZONE_ID);
        zoneResponse.setCodePostal(CODE_POSTAL);
        zoneResponse.setNom("Casablanca");

        produitRequest = new ProduitRequest();
        produitRequest.setNom("Produit Test");
        produitRequest.setCategorie("Electronique");
        produitRequest.setPoids(2.5);
        produitRequest.setPrix(new BigDecimal("500.00"));

        produit = new Produit();
        produit.setId(PRODUIT_ID);
        produit.setNom("Produit Test");
        produit.setCategorie("Electronique");
        produit.setPoids(2.5);
        produit.setPrix(new BigDecimal("500.00"));

        produitResponse = new ProduitResponse();
        produitResponse.setId(PRODUIT_ID);
        produitResponse.setNom("Produit Test");
        Produit produit1 = new Produit();
        produit1.setId("PROD-001");
        produit1.setNom("Phone");

        Produit produit2 = new Produit();
        produit2.setId("PROD-002");
        produit2.setNom("phone");

        Produit produit3 = new Produit();
        produit3.setId("PROD-003");
        produit3.setNom("PHONE");

        Produit produit4 = new Produit();
        produit4.setId("PROD-004");
        produit4.setNom("Phone");

    }

    // ========== CREATE ZONE TESTS ==========

    @Test
    void createZone_shouldThrowException_whenZoneAlreadyExistsInDatabase() {
        when(zoneRepository.findByCodePostal(CODE_POSTAL)).thenReturn(Optional.of(zone));

        ConflictStateException exception = assertThrows(
                ConflictStateException.class,
                () -> logisticsDataService.createZone(zoneRequest)
        );

        assertTrue(exception.getMessage().contains("existe déjà"));
        assertTrue(exception.getMessage().contains(CODE_POSTAL));
        verify(zoneRepository).findByCodePostal(CODE_POSTAL);
        verify(zoneRepository, never()).save(any());
    }

    // ========== CREATE ZONE TESTS ==========


    @Test
    void createZone_shouldSaveZone_whenZoneNotInJson() {

        ZoneRequest request = new ZoneRequest();
        request.setCodePostal("12345");

        Zone zoneEntity = new Zone();
        zoneEntity.setCodePostal("12345");

        ZoneResponse expectedResponse = new ZoneResponse();
        expectedResponse.setCodePostal("12345");

        when(zoneRepository.findByCodePostal("12345")).thenReturn(Optional.empty());
        when(zoneMapper.toEntity(request)).thenReturn(zoneEntity);
        when(zoneRepository.save(zoneEntity)).thenReturn(zoneEntity);
        when(zoneMapper.toResponse(zoneEntity)).thenReturn(expectedResponse);

        ZoneResponse actualResponse = logisticsDataService.createZone(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getCodePostal(), actualResponse.getCodePostal());

        verify(zoneRepository).save(zoneEntity);
    }

    @Test
    void createZone_shouldThrowRuntimeException_onIOException() throws IOException {
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.empty());
        ZoneRequest request = new ZoneRequest();
        request.setCodePostal("99999");
        when(zoneRepository.save(any(Zone.class))).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> logisticsDataService.createZone(request));


        assertTrue(exception.getMessage().contains("Erreur de lecture du JSON zones."));
        verify(zoneRepository, never()).save(any(Zone.class));
    }



    // ========== GET ZONE BY ID TESTS ==========

    @Test
    void getZoneById_shouldReturnZone_whenExists() {
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.of(zone));
        when(zoneMapper.toResponse(zone)).thenReturn(zoneResponse);

        ZoneResponse result = logisticsDataService.getZoneById(ZONE_ID);

        assertNotNull(result);
        assertEquals(ZONE_ID, result.getId());
        assertEquals(CODE_POSTAL, result.getCodePostal());
        verify(zoneRepository).findById(ZONE_ID);
        verify(zoneMapper).toResponse(zone);
    }

    @Test
    void getZoneById_shouldThrowException_whenNotFound() {
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> logisticsDataService.getZoneById(ZONE_ID)
        );

        assertTrue(exception.getMessage().contains("Zone"));
        assertTrue(exception.getMessage().contains(ZONE_ID));
        verify(zoneRepository).findById(ZONE_ID);
        verify(zoneMapper, never()).toResponse(any());
    }

    // ========== GET ALL ZONES TESTS ==========

    @Test
    void getAllZones_shouldReturnPaginatedZones() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Zone> zoneList = Arrays.asList(zone);
        Page<Zone> zonePage = new PageImpl<>(zoneList, pageable, 1);

        when(zoneRepository.findAll(pageable)).thenReturn(zonePage);
        when(zoneMapper.toResponse(zone)).thenReturn(zoneResponse);

        Page<ZoneResponse> result = logisticsDataService.getAllZones(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(zoneRepository).findAll(pageable);
    }

    @Test
    void getAllZones_shouldReturnEmptyPage_whenNoZones() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Zone> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(zoneRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<ZoneResponse> result = logisticsDataService.getAllZones(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getAllZones_shouldReturnMultipleZones() {
        Pageable pageable = PageRequest.of(0, 10);

        Zone zone2 = new Zone();
        zone2.setId("ZONE-002");
        zone2.setNom("Rabat");

        List<Zone> zoneList = Arrays.asList(zone, zone2);
        Page<Zone> zonePage = new PageImpl<>(zoneList, pageable, 2);

        ZoneResponse response2 = new ZoneResponse();
        response2.setId("ZONE-002");

        when(zoneRepository.findAll(pageable)).thenReturn(zonePage);
        when(zoneMapper.toResponse(zone)).thenReturn(zoneResponse);
        when(zoneMapper.toResponse(zone2)).thenReturn(response2);

        Page<ZoneResponse> result = logisticsDataService.getAllZones(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    // ========== UPDATE ZONE TESTS ==========

    @Test
    void updateZone_shouldThrowException_whenZoneNotFound() {
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> logisticsDataService.updateZone(ZONE_ID, zoneRequest)
        );

        assertTrue(exception.getMessage().contains("Zone"));
        verify(zoneRepository).findById(ZONE_ID);
        verify(zoneRepository, never()).save(any());
    }
    @Test
    void updateZone_shouldThrowRuntimeException_onIOException() throws IOException {
        Zone zn = new Zone();
        zn.setId("ZONE-005");
        when(zoneRepository.findById(anyString())).thenReturn(Optional.of(zn));
        when(zoneRepository.save(any(Zone.class))).thenThrow(new RuntimeException());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> logisticsDataService.updateZone(anyString(),zoneRequest));

        assertTrue(exception.getMessage().contains("Erreur de lecture du JSON zones."));
    }
    @Test
    void updateZone_shouldThrowException_whenJsonFileNotFound() {
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.of(zone));

        assertThrows(RuntimeException.class,
                () -> logisticsDataService.updateZone(ZONE_ID, zoneRequest));
    }

    // ========== DELETE ZONE TESTS ==========

    @Test
    void deleteZone_shouldDeleteSuccessfully_whenZoneExists() {
        when(zoneRepository.existsById(ZONE_ID)).thenReturn(true);
        doNothing().when(zoneRepository).deleteById(ZONE_ID);

        assertDoesNotThrow(() -> logisticsDataService.deleteZone(ZONE_ID));

        verify(zoneRepository).existsById(ZONE_ID);
        verify(zoneRepository).deleteById(ZONE_ID);
    }

    @Test
    void deleteZone_shouldThrowException_whenZoneNotFound() {
        when(zoneRepository.existsById(ZONE_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> logisticsDataService.deleteZone(ZONE_ID)
        );

        assertTrue(exception.getMessage().contains("Zone"));
        assertTrue(exception.getMessage().contains(ZONE_ID));
        verify(zoneRepository).existsById(ZONE_ID);
        verify(zoneRepository, never()).deleteById(anyString());
    }

    // ========== CREATE PRODUIT TESTS ==========

    @Test
    void createProduit_shouldCreateSuccessfully() {
        when(produitMapper.toEntity(produitRequest)).thenReturn(produit);
        when(produitRepository.save(produit)).thenReturn(produit);
        when(produitMapper.toResponse(produit)).thenReturn(produitResponse);

        ProduitResponse result = logisticsDataService.createProduit(produitRequest);

        assertNotNull(result);
        assertEquals(PRODUIT_ID, result.getId());
        assertEquals("Produit Test", result.getNom());
        verify(produitMapper).toEntity(produitRequest);
        verify(produitRepository).save(produit);
        verify(produitMapper).toResponse(produit);
    }

    // ========== GET PRODUIT BY ID TESTS ==========

    @Test
    void getProduitById_shouldReturnProduit_whenExists() {
        when(produitRepository.findById(PRODUIT_ID)).thenReturn(Optional.of(produit));
        when(produitMapper.toResponse(produit)).thenReturn(produitResponse);

        ProduitResponse result = logisticsDataService.getProduitById(PRODUIT_ID);

        assertNotNull(result);
        assertEquals(PRODUIT_ID, result.getId());
        verify(produitRepository).findById(PRODUIT_ID);
        verify(produitMapper).toResponse(produit);
    }

    @Test
    void getProduitById_shouldThrowException_whenNotFound() {
        when(produitRepository.findById(PRODUIT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> logisticsDataService.getProduitById(PRODUIT_ID)
        );

        assertTrue(exception.getMessage().contains("Produit"));
        assertTrue(exception.getMessage().contains(PRODUIT_ID));
        verify(produitRepository).findById(PRODUIT_ID);
        verify(produitMapper, never()).toResponse(any());
    }

    // ========== GET ALL PRODUITS TESTS ==========

    @Test
    void findAllProduits_shouldReturnPaginatedProduits() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Produit> produitList = Arrays.asList(produit);
        Page<Produit> produitPage = new PageImpl<>(produitList, pageable, 1);

        when(produitRepository.findAll(pageable)).thenReturn(produitPage);
        when(produitMapper.toResponse(produit)).thenReturn(produitResponse);

        Page<ProduitResponse> result = logisticsDataService.findAllProduits(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(produitRepository).findAll(pageable);
    }

    @Test
    void findAllProduits_shouldReturnEmptyPage_whenNoProduits() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produit> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(produitRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<ProduitResponse> result = logisticsDataService.findAllProduits(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void findAllProduits_shouldReturnMultipleProduits() {
        Pageable pageable = PageRequest.of(0, 10);

        Produit produit2 = new Produit();
        produit2.setId("PROD-002");
        produit2.setNom("Produit 2");

        List<Produit> produitList = Arrays.asList(produit, produit2);
        Page<Produit> produitPage = new PageImpl<>(produitList, pageable, 2);

        ProduitResponse response2 = new ProduitResponse();
        response2.setId("PROD-002");

        when(produitRepository.findAll(pageable)).thenReturn(produitPage);
        when(produitMapper.toResponse(produit)).thenReturn(produitResponse);
        when(produitMapper.toResponse(produit2)).thenReturn(response2);

        Page<ProduitResponse> result = logisticsDataService.findAllProduits(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    // ========== UPDATE PRODUIT TESTS ==========

    @Test
    void updateProduit_shouldUpdateSuccessfully_whenProduitExists() {
        produitRequest.setNom("Updated Produit");
        produitRequest.setPrix(new BigDecimal("600.00"));

        when(produitRepository.findById(PRODUIT_ID)).thenReturn(Optional.of(produit));
        doNothing().when(produitMapper).updateEntityFromRequest(produitRequest, produit);
        when(produitRepository.save(produit)).thenReturn(produit);
        when(produitMapper.toResponse(produit)).thenReturn(produitResponse);

        ProduitResponse result = logisticsDataService.updateProduit(PRODUIT_ID, produitRequest);

        assertNotNull(result);
        verify(produitRepository).findById(PRODUIT_ID);
        verify(produitMapper).updateEntityFromRequest(produitRequest, produit);
        verify(produitRepository).save(produit);
        verify(produitMapper).toResponse(produit);
    }

    @Test
    void updateProduit_shouldThrowException_whenProduitNotFound() {
        when(produitRepository.findById(PRODUIT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> logisticsDataService.updateProduit(PRODUIT_ID, produitRequest)
        );

        assertTrue(exception.getMessage().contains("Produit"));
        assertTrue(exception.getMessage().contains(PRODUIT_ID));
        verify(produitRepository).findById(PRODUIT_ID);
        verify(produitRepository, never()).save(any());
    }

    // ========== DELETE PRODUIT TESTS ==========

    @Test
    void deleteProduit_shouldDeleteSuccessfully_whenProduitExists() {
        when(produitRepository.existsById(PRODUIT_ID)).thenReturn(true);
        doNothing().when(produitRepository).deleteById(PRODUIT_ID);

        assertDoesNotThrow(() -> logisticsDataService.deleteProduit(PRODUIT_ID));

        verify(produitRepository).existsById(PRODUIT_ID);
        verify(produitRepository).deleteById(PRODUIT_ID);
    }

    @Test
    void deleteProduit_shouldThrowException_whenProduitNotFound() {
        when(produitRepository.existsById(PRODUIT_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> logisticsDataService.deleteProduit(PRODUIT_ID)
        );

        assertTrue(exception.getMessage().contains("Produit"));
        assertTrue(exception.getMessage().contains(PRODUIT_ID));
        verify(produitRepository).existsById(PRODUIT_ID);
        verify(produitRepository, never()).deleteById(anyString());
    }


    @Test
    void deleteDuplicateProducts_shouldDeleteDuplicates_whenDuplicatesExist() {
        Produit produit1 = new Produit();
        produit1.setId("PROD-001");
        produit1.setNom("Laptop");

        Produit produit2 = new Produit();
        produit2.setId("PROD-002");
        produit2.setNom("Laptop");

        Produit produit3 = new Produit();
        produit3.setId("PROD-003");
        produit3.setNom("LAPTOP");

        Produit produit4 = new Produit();
        produit4.setId("PROD-004");
        produit4.setNom("Mouse");

        List<Produit> allProducts = Arrays.asList(produit1, produit2, produit3, produit4);
        when(produitRepository.findAll()).thenReturn(allProducts);
        doNothing().when(produitRepository).delete(any(Produit.class));

        logisticsDataService.deleteDuplicateProducts();

        verify(produitRepository).findAll();
        verify(produitRepository, times(2)).delete(any(Produit.class));
    }

    @Test
    void deleteDuplicateProducts_shouldNotDelete_whenNoDuplicates() {
        Produit produit1 = new Produit();
        produit1.setId("PROD-001");
        produit1.setNom("Laptop");

        Produit produit2 = new Produit();
        produit2.setId("PROD-002");
        produit2.setNom("Mouse");

        List<Produit> allProducts = Arrays.asList(produit1, produit2);
        when(produitRepository.findAll()).thenReturn(allProducts);

        logisticsDataService.deleteDuplicateProducts();

        verify(produitRepository).findAll();
        verify(produitRepository, never()).delete(any(Produit.class));
    }

    @Test
    void deleteDuplicateProducts_shouldHandleEmptyList() {
        when(produitRepository.findAll()).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> logisticsDataService.deleteDuplicateProducts());

        verify(produitRepository).findAll();
        verify(produitRepository, never()).delete(any(Produit.class));
    }

    @Test
    void updateZone_shouldThrowConflictException_whenCodePostalExistsInJsonFile() {
        // 1. Arrange: T'7diid l'Code Postal (li moujoud f'l'File zone.json)
        final String CODE_POSTAL_IN_JSON = "10000"; // ⚠️ Assumption: Khass l'Code 10000 ykoun moujoud f'zone.json

        // 2. Setup l'Entity l'Qdima (li jbna mn findById)
        zone.setCodePostal(CODE_POSTAL_IN_JSON); // 👈 FIX: Khass l'Zone Entity dyal DB tmatchi m3a JSON
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.of(zone));

        // 3. Setup l'Request: Request jdida (mawa7dch m3a l'Code Postal dyal l'Entity)
        zoneRequest.setCodePostal("99999"); // Katbghi tbddel l'Code, walakin l'Logic kayghltt
        zoneRequest.setNom("Updated Name");


        // 4. Act & Assert
        ConflictStateException exception = assertThrows(
                ConflictStateException.class,
                () -> logisticsDataService.updateZone(ZONE_ID, zoneRequest)
        );

        // 5. Verification:
        // Hna l'Code dyalek ghadi yrmmi ConflictStateException
        assertTrue(exception.getMessage().contains("existe déjà sur JSON file"));
        verify(zoneRepository).findById(ZONE_ID);
        verify(zoneRepository, never()).save(any());
    }

    @Test
    void updateZone_shouldUpdateSuccessfully_whenCodePostalNotInJson() {
        zone.setCodePostal("88888");
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.of(zone));

        zoneRequest.setCodePostal("88888");

        doNothing().when(zoneMapper).updateEntityFromRequest(zoneRequest, zone);
        when(zoneRepository.save(zone)).thenReturn(zone);
        when(zoneMapper.toResponse(zone)).thenReturn(zoneResponse);

        ZoneResponse result = logisticsDataService.updateZone(ZONE_ID, zoneRequest);

        assertNotNull(result);
        verify(zoneRepository).findById(ZONE_ID);
        verify(zoneMapper).updateEntityFromRequest(zoneRequest, zone);
        verify(zoneRepository).save(zone);
    }




}
