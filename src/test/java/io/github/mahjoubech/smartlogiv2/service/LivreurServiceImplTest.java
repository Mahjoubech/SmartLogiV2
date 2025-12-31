package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.mapper.LivreurMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.repository.*;
import io.github.mahjoubech.smartlogiv2.repository.ColisRepository;
import io.github.mahjoubech.smartlogiv2.repository.LivreurRepository;
import io.github.mahjoubech.smartlogiv2.repository.ZoneRepository;
import io.github.mahjoubech.smartlogiv2.service.impl.LivreurServiceImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivreurServiceImplTest {

    @Mock
    private LivreurRepository livreurRepository;

    @Mock
    private ColisRepository colisRepository;
    @Mock
    private RolesEntityRepository rolesEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private LivreurMapper livreurMapper;

    @Mock
    private ColisMapper colisMapper;

    @InjectMocks
    private LivreurServiceImpl livreurService;

    private LivreurRequest livreurRequest;
    private Livreur livreur;
    private Zone zone;
    private LivreurResponse livreurResponse;
    private Colis colis;
    private ColisResponse colisResponse;

    private final String LIVREUR_ID = "LIV-001";
    private final String ZONE_ID = "ZONE-001";
    private final String NEW_ZONE_ID = "ZONE-002";

    @BeforeEach
    void setUp() {
        zone = new Zone();
        zone.setId(ZONE_ID);
        zone.setNom("Zone Test");
        zone.setCodePostal("20000");

        livreurRequest = new LivreurRequest();
        livreurRequest.setNom("Alami");
        livreurRequest.setPrenom("Mohammed");
        livreurRequest.setTelephone("0612345678");
        livreurRequest.setVehicule("Moto");
        livreurRequest.setPassword("123456");
        livreurRequest.setConfirmPassword("123456");
        livreurRequest.setZoneAssigneeId(ZONE_ID);

        livreur = new Livreur();
        livreur.setId(LIVREUR_ID);
        livreur.setNom("Alami");
        livreur.setPrenom("Mohammed");
        livreur.setTelephone("0612345678");
        livreur.setVehicule("Moto");
        livreurRequest.setPassword("123456");
        livreurRequest.setConfirmPassword("123456");
        livreur.setZoneAssigned(zone);

        livreurResponse = new LivreurResponse();
        livreurResponse.setId(LIVREUR_ID);
        livreurResponse.setNom("Alami");
        livreurResponse.setPrenom("Mohammed");
        livreurRequest.setPassword("123456");
        livreurRequest.setConfirmPassword("123456");
        livreurResponse.setTelephone("0612345678");

        colis = new Colis();
        colis.setId("COL-001");
        colis.setLivreur(livreur);

        colisResponse = new ColisResponse();
        colisResponse.setId("COL-001");
    }

    // ========== CREATE LIVREUR TESTS ==========

    @Test
    void createLivreur_shouldCreateSuccessfully_whenZoneExists() {

        RolesEntity roleLivreur = new RolesEntity();
        roleLivreur.setName(Roles.LIVREUR);

        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.of(zone));
        when(rolesEntityRepository.findByName(Roles.LIVREUR))
                .thenReturn(Optional.of(roleLivreur));
        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");
        when(livreurMapper.toEntity(livreurRequest)).thenReturn(livreur);
        when(livreurRepository.save(livreur)).thenReturn(livreur);
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);

        LivreurResponse result = livreurService.createLivreur(livreurRequest);

        assertNotNull(result);
        assertEquals(LIVREUR_ID, result.getId());
    }



    @Test
    void createLivreur_shouldThrowException_whenZoneNotFound() {
        when(zoneRepository.findById(ZONE_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> livreurService.createLivreur(livreurRequest)
        );

        assertTrue(exception.getMessage().contains("Zone not found"));
        assertTrue(exception.getMessage().contains(ZONE_ID));
        verify(zoneRepository).findById(ZONE_ID);
        verify(livreurRepository, never()).save(any());
    }

    // ========== GET ALL LIVREURS TESTS ==========

    @Test
    void getAllLivreurs_shouldReturnPaginatedLivreurs() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Livreur> livreurList = Arrays.asList(livreur);
        Page<Livreur> livreurPage = new PageImpl<>(livreurList, pageable, 1);

        when(livreurRepository.findAll(pageable)).thenReturn(livreurPage);
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);

        Page<LivreurResponse> result = livreurService.getAllLivreurs(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(LIVREUR_ID, result.getContent().get(0).getId());
        verify(livreurRepository).findAll(pageable);
        verify(livreurMapper).toResponse(livreur);
    }

    @Test
    void getAllLivreurs_shouldReturnEmptyPage_whenNoLivreurs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Livreur> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(livreurRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<LivreurResponse> result = livreurService.getAllLivreurs(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(livreurRepository).findAll(pageable);
    }

    @Test
    void getAllLivreurs_shouldReturnMultipleLivreurs() {
        Pageable pageable = PageRequest.of(0, 10);

        Livreur livreur2 = new Livreur();
        livreur2.setId("LIV-002");
        livreur2.setNom("Bennani");

        Livreur livreur3 = new Livreur();
        livreur3.setId("LIV-003");
        livreur3.setNom("Tahiri");

        List<Livreur> livreurList = Arrays.asList(livreur, livreur2, livreur3);
        Page<Livreur> livreurPage = new PageImpl<>(livreurList, pageable, 3);

        LivreurResponse response2 = new LivreurResponse();
        response2.setId("LIV-002");

        LivreurResponse response3 = new LivreurResponse();
        response3.setId("LIV-003");

        when(livreurRepository.findAll(pageable)).thenReturn(livreurPage);
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);
        when(livreurMapper.toResponse(livreur2)).thenReturn(response2);
        when(livreurMapper.toResponse(livreur3)).thenReturn(response3);

        Page<LivreurResponse> result = livreurService.getAllLivreurs(pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
    }

    // ========== GET LIVREUR BY ID TESTS ==========

    @Test
    void getLivreurById_shouldReturnLivreur_whenExists() {
        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.of(livreur));
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);

        LivreurResponse result = livreurService.getLivreurById(LIVREUR_ID);

        assertNotNull(result);
        assertEquals(LIVREUR_ID, result.getId());
        assertEquals("Alami", result.getNom());
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(livreurMapper).toResponse(livreur);
    }

    @Test
    void getLivreurById_shouldThrowException_whenNotFound() {
        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> livreurService.getLivreurById(LIVREUR_ID)
        );

        assertTrue(exception.getMessage().contains("Livreur not found"));
        assertTrue(exception.getMessage().contains(LIVREUR_ID));
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(livreurMapper, never()).toResponse(any());
    }

    // ========== DELETE LIVREUR TESTS ==========

    @Test
    void deleteLivreur_shouldDeleteSuccessfully_whenLivreurExists() {
        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.of(livreur));
        doNothing().when(livreurRepository).delete(livreur);

        assertDoesNotThrow(() -> livreurService.deleteLivreur(LIVREUR_ID));

        verify(livreurRepository).findById(LIVREUR_ID);
        verify(livreurRepository).delete(livreur);
    }

    @Test
    void deleteLivreur_shouldThrowException_whenLivreurNotFound() {
        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> livreurService.deleteLivreur(LIVREUR_ID)
        );

        assertTrue(exception.getMessage().contains("Livreur not found"));
        assertTrue(exception.getMessage().contains(LIVREUR_ID));
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(livreurRepository, never()).delete(any());
    }

    // ========== UPDATE LIVREUR TESTS ==========

    @Test
    void updateLivreur_shouldUpdateSuccessfully_whenZoneUnchanged() {
        livreurRequest.setNom("Updated Name");
        livreurRequest.setPrenom("Updated Prenom");
        livreurRequest.setTelephone("0698765432");
        livreurRequest.setVehicule("Voiture");
        livreurRequest.setZoneAssigneeId(ZONE_ID);

        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.of(livreur));
        when(livreurRepository.save(livreur)).thenReturn(livreur);
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);

        LivreurResponse result = livreurService.updateLivreur(LIVREUR_ID, livreurRequest);

        assertNotNull(result);
        assertEquals("Updated Name", livreur.getNom());
        assertEquals("Updated Prenom", livreur.getPrenom());
        assertEquals("0698765432", livreur.getTelephone());
        assertEquals("Voiture", livreur.getVehicule());
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(livreurRepository).save(livreur);
        verify(zoneRepository, never()).findById(anyString());
    }

    @Test
    void updateLivreur_shouldUpdateZone_whenZoneChanged() {
        Zone newZone = new Zone();
        newZone.setId(NEW_ZONE_ID);
        newZone.setNom("New Zone");

        livreurRequest.setZoneAssigneeId(NEW_ZONE_ID);

        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.of(livreur));
        when(zoneRepository.findById(NEW_ZONE_ID)).thenReturn(Optional.of(newZone));
        when(livreurRepository.save(livreur)).thenReturn(livreur);
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);

        LivreurResponse result = livreurService.updateLivreur(LIVREUR_ID, livreurRequest);

        assertNotNull(result);
        assertEquals(newZone, livreur.getZoneAssigned());
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(zoneRepository).findById(NEW_ZONE_ID);
        verify(livreurRepository).save(livreur);
    }

    @Test
    void updateLivreur_shouldThrowException_whenLivreurNotFound() {
        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> livreurService.updateLivreur(LIVREUR_ID, livreurRequest)
        );

        assertTrue(exception.getMessage().contains("Livreur not found"));
        assertTrue(exception.getMessage().contains(LIVREUR_ID));
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(livreurRepository, never()).save(any());
    }

    @Test
    void updateLivreur_shouldThrowException_whenNewZoneNotFound() {
        livreurRequest.setZoneAssigneeId(NEW_ZONE_ID);

        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.of(livreur));
        when(zoneRepository.findById(NEW_ZONE_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> livreurService.updateLivreur(LIVREUR_ID, livreurRequest)
        );

        assertTrue(exception.getMessage().contains("New Zone not found"));
        assertTrue(exception.getMessage().contains(NEW_ZONE_ID));
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(zoneRepository).findById(NEW_ZONE_ID);
        verify(livreurRepository, never()).save(any());
    }

    // ========== GET ASSIGNED COLIS TESTS ==========

    @Test
    void getAssignedColis_shouldReturnPaginatedColis_whenLivreurExists() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Colis> colisList = Arrays.asList(colis);
        Page<Colis> colisPage = new PageImpl<>(colisList, pageable, 1);

        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.of(livreur));
        when(colisRepository.findByLivreurId(LIVREUR_ID, pageable)).thenReturn(colisPage);
        when(colisMapper.toResponse(colis)).thenReturn(colisResponse);

        Page<ColisResponse> result = livreurService.getAssignedColis(LIVREUR_ID, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(colisRepository).findByLivreurId(LIVREUR_ID, pageable);
        verify(colisMapper).toResponse(colis);
    }

    @Test
    void getAssignedColis_shouldReturnEmptyPage_whenNoColisAssigned() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Colis> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.of(livreur));
        when(colisRepository.findByLivreurId(LIVREUR_ID, pageable)).thenReturn(emptyPage);

        Page<ColisResponse> result = livreurService.getAssignedColis(LIVREUR_ID, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(colisRepository).findByLivreurId(LIVREUR_ID, pageable);
    }

    @Test
    void getAssignedColis_shouldThrowException_whenLivreurNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> livreurService.getAssignedColis(LIVREUR_ID, pageable)
        );

        assertTrue(exception.getMessage().contains(LIVREUR_ID));
        verify(livreurRepository).findById(LIVREUR_ID);
        verify(colisRepository, never()).findByLivreurId(anyString(), any());
    }

    @Test
    void getAssignedColis_shouldReturnMultipleColis() {
        Pageable pageable = PageRequest.of(0, 10);

        Colis colis2 = new Colis();
        colis2.setId("COL-002");

        Colis colis3 = new Colis();
        colis3.setId("COL-003");

        List<Colis> colisList = Arrays.asList(colis, colis2, colis3);
        Page<Colis> colisPage = new PageImpl<>(colisList, pageable, 3);

        ColisResponse response2 = new ColisResponse();
        response2.setId("COL-002");

        ColisResponse response3 = new ColisResponse();
        response3.setId("COL-003");

        when(livreurRepository.findById(LIVREUR_ID)).thenReturn(Optional.of(livreur));
        when(colisRepository.findByLivreurId(LIVREUR_ID, pageable)).thenReturn(colisPage);
        when(colisMapper.toResponse(colis)).thenReturn(colisResponse);
        when(colisMapper.toResponse(colis2)).thenReturn(response2);
        when(colisMapper.toResponse(colis3)).thenReturn(response3);

        Page<ColisResponse> result = livreurService.getAssignedColis(LIVREUR_ID, pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
    }

    // ========== SEARCH LIVREURS TESTS ==========

    @Test
    void searchLivreurs_shouldReturnMatchingLivreurs() {
        String keyword = "Alami";
        Pageable pageable = PageRequest.of(0, 10);
        List<Livreur> livreurList = Arrays.asList(livreur);
        Page<Livreur> livreurPage = new PageImpl<>(livreurList, pageable, 1);

        when(livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                keyword, keyword, pageable
        )).thenReturn(livreurPage);
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);

        Page<LivreurResponse> result = livreurService.searchLivreurs(keyword, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(livreurRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                keyword, keyword, pageable
        );
        verify(livreurMapper).toResponse(livreur);
    }

    @Test
    void searchLivreurs_shouldReturnEmptyPage_whenNoMatches() {
        String keyword = "NonExistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Livreur> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                keyword, keyword, pageable
        )).thenReturn(emptyPage);

        Page<LivreurResponse> result = livreurService.searchLivreurs(keyword, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(livreurRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                keyword, keyword, pageable
        );
    }

    @Test
    void searchLivreurs_shouldReturnMultipleResults_whenMultipleMatch() {
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 10);

        Livreur livreur2 = new Livreur();
        livreur2.setId("LIV-002");
        livreur2.setNom("TestName");

        Livreur livreur3 = new Livreur();
        livreur3.setId("LIV-003");
        livreur3.setPrenom("TestPrenom");

        List<Livreur> livreurList = Arrays.asList(livreur, livreur2, livreur3);
        Page<Livreur> livreurPage = new PageImpl<>(livreurList, pageable, 3);

        LivreurResponse response2 = new LivreurResponse();
        response2.setId("LIV-002");

        LivreurResponse response3 = new LivreurResponse();
        response3.setId("LIV-003");

        when(livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                keyword, keyword, pageable
        )).thenReturn(livreurPage);
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);
        when(livreurMapper.toResponse(livreur2)).thenReturn(response2);
        when(livreurMapper.toResponse(livreur3)).thenReturn(response3);

        Page<LivreurResponse> result = livreurService.searchLivreurs(keyword, pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
    }

    @Test
    void searchLivreurs_shouldBeCaseInsensitive() {
        String keyword = "ALAMI";
        Pageable pageable = PageRequest.of(0, 10);
        List<Livreur> livreurList = Arrays.asList(livreur);
        Page<Livreur> livreurPage = new PageImpl<>(livreurList, pageable, 1);

        when(livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                keyword, keyword, pageable
        )).thenReturn(livreurPage);
        when(livreurMapper.toResponse(livreur)).thenReturn(livreurResponse);

        Page<LivreurResponse> result = livreurService.searchLivreurs(keyword, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(livreurRepository).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                keyword, keyword, pageable
        );
    }

    // ========== GET LIVREUR COLIS COUNTS TESTS ==========

    @Test
    void getLivreurColisCounts_shouldReturnPaginatedCounts() {
        Pageable pageable = PageRequest.of(0, 10);

        LivreurColisResponse mockResponse = mock(LivreurColisResponse.class);

        List<LivreurColisResponse> countList = Arrays.asList(mockResponse);
        Page<LivreurColisResponse> countsPage = new PageImpl<>(countList, pageable, 1);

        when(livreurRepository.getColisEvryLivreur(pageable)).thenReturn(countsPage);

        Page<LivreurColisResponse> result = livreurService.getLivreurColisCounts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(livreurRepository).getColisEvryLivreur(pageable);
    }

    @Test
    void getLivreurColisCounts_shouldReturnEmptyPage_whenNoLivreurs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LivreurColisResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(livreurRepository.getColisEvryLivreur(pageable)).thenReturn(emptyPage);

        Page<LivreurColisResponse> result = livreurService.getLivreurColisCounts(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(livreurRepository).getColisEvryLivreur(pageable);
    }

    @Test
    void getLivreurColisCounts_shouldReturnMultipleCounts() {
        Pageable pageable = PageRequest.of(0, 10);

        LivreurColisResponse mockResponse1 = mock(LivreurColisResponse.class);
        LivreurColisResponse mockResponse2 = mock(LivreurColisResponse.class);

        List<LivreurColisResponse> countList = Arrays.asList(mockResponse1, mockResponse2);
        Page<LivreurColisResponse> countsPage = new PageImpl<>(countList, pageable, 2);

        when(livreurRepository.getColisEvryLivreur(pageable)).thenReturn(countsPage);

        Page<LivreurColisResponse> result = livreurService.getLivreurColisCounts(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(livreurRepository).getColisEvryLivreur(pageable);
    }
}
