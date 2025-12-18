package io.github.mahjoubech.smartlogiv2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.*;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.mapper.HistoriqueLivraisonMapper;
import io.github.mahjoubech.smartlogiv2.mapper.ZoneMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.*;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import io.github.mahjoubech.smartlogiv2.repository.*;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import io.github.mahjoubech.smartlogiv2.service.EmailService;
import io.github.mahjoubech.smartlogiv2.service.impl.ColisServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ColisServiceImplTest {

    @Mock private ColisRepository colisRepository;
    @Mock private ClientExpediteurRepository expediteurRepository;
    @Mock private DestinataireRepository destinataireRepository;
    @Mock private ZoneRepository zoneRepository;
    @Mock private ProduitRepository produitRepository;
    @Mock private HistoriqueLivraisonRepository historiqueRepository;
    @Mock private LivreurRepository livreurRepository;
    @Mock private ColisProduitRepository colisProduitRepository;
    @Mock private ColisMapper colisMapper;
    @Mock private HistoriqueLivraisonMapper historiqueMapper;
    @Mock private ZoneMapper zoneMapper;
    @Mock private EmailService emailService;

    @InjectMocks
    private ColisServiceImpl colisService;

    private final String VALID_EMAIL = "test@example.com";
    private final String COLIS_ID = "COL-123";
    private final String ZONE_CODE = "20000";
    private ColisRequest validRequest;
    private Colis colisEntity;
    private Produit existingProduct;
    private ClientExpediteur expediteur;
    private Destinataire destinataire;
    private Zone zone;
    private Produit newSavedProduct;
    private Livreur livreur;

    @BeforeEach
    void setUp() {
        expediteur = new ClientExpediteur();
        expediteur.setId("EXP-001");
        expediteur.setEmail(VALID_EMAIL);
        expediteur.setNom("Nom Exp");
        expediteur.setPrenom("Prenom Exp");

        destinataire = new Destinataire();
        destinataire.setId("DEST-001");
        destinataire.setEmail("dest@example.com");
        destinataire.setNom("Nom Dest");

        zone = new Zone();
        zone.setId("Z-001");
        zone.setCodePostal(ZONE_CODE);
        zone.setNom("Zone Test");

        livreur = new Livreur();
        livreur.setId("LIV-001");
        livreur.setZoneAssigned(zone);

        existingProduct = new Produit();
        existingProduct.setId("P-001");
        existingProduct.setNom("Produit Existant");
        existingProduct.setPrix(new BigDecimal("100.00"));

        newSavedProduct = new Produit();
        newSavedProduct.setId("P-002");
        newSavedProduct.setPrix(new BigDecimal("50.00"));

        validRequest = new ColisRequest();
        validRequest.setClientExpediteurEmail(VALID_EMAIL);
        validRequest.setDestinataireEmail(destinataire.getEmail());
        validRequest.setVilleDestination("Rabat");
        validRequest.setCodePostal(ZONE_CODE);
        validRequest.setPriorite("NORMAL");
        validRequest.setPoids(5.0);

        ProduitRequest newProductRequest = new ProduitRequest();
        newProductRequest.setNom("Produit Jdid");
        newProductRequest.setPrix(new BigDecimal("50.00"));
        newProductRequest.setColisProduit(new ColisProduitRequest(2));

        validRequest.setProduits(Collections.singletonList(newProductRequest));

        colisEntity = new Colis();
        colisEntity.setId(COLIS_ID);
        colisEntity.setStatus(ColisStatus.CREE);
        colisEntity.setDestinataire(destinataire);
        colisEntity.setClientExpediteur(expediteur);
        colisEntity.setLivreur(livreur);
        colisEntity.setZone(zone);
    }

    @Test
    void createDelivery_shouldSucceedWithUpsertProducts() {
        // Mock duplicate check
        when(colisRepository.findByClientExpediteurEmailAndDestinataireEmailAndPoidsAndStatusAndVilleDestinationAndPrioriteStatus(
                anyDouble(), eq(ColisStatus.CREE), anyString(), any(PrioriteStatus.class)))
                .thenReturn(Collections.emptyList());

        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.of(zone));

        when(produitRepository.findByNomIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(produitRepository.save(any(Produit.class))).thenReturn(newSavedProduct);

        when(colisMapper.toEntity(any(ColisRequest.class))).thenReturn(colisEntity);
        when(colisRepository.save(any(Colis.class))).thenReturn(colisEntity);
        when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());

        colisService.createDelivery(validRequest);

        verify(produitRepository, times(1)).save(any(Produit.class));
        verify(colisRepository, times(2)).save(any(Colis.class));
        verify(historiqueRepository, times(1)).save(any(HistoriqueLivraison.class));
    }

    @Test
    void createDelivery_shouldThrowConflictException_whenColisIsDuplicate() {
        List<Colis> existingList = Collections.singletonList(colisEntity);

        when(colisRepository.findByClientExpediteurEmailAndDestinataireEmailAndPoidsAndStatusAndVilleDestinationAndPrioriteStatus(
                anyDouble(), eq(ColisStatus.CREE), anyString(), any(PrioriteStatus.class)))
                .thenReturn(existingList);

        assertThrows(ConflictStateException.class, () -> colisService.createDelivery(validRequest));

        verify(colisRepository, never()).save(any());
    }

    @Test
    void createDelivery_shouldCreateNewZoneAndSaveColis() {
        when(colisRepository.findByClientExpediteurEmailAndDestinataireEmailAndPoidsAndStatusAndVilleDestinationAndPrioriteStatus(
                anyDouble(), eq(ColisStatus.CREE), anyString(), any(PrioriteStatus.class)))
                .thenReturn(Collections.emptyList());
        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(ZONE_CODE)).thenReturn(Optional.empty());
        when(zoneMapper.toEntity(any(ZoneRequest.class))).thenReturn(zone);
        when(zoneRepository.save(any(Zone.class))).thenReturn(zone);
        when(produitRepository.findByNomIgnoreCase(anyString())).thenReturn(Optional.empty());

        Produit savedProductWithPrice = new Produit();
        savedProductWithPrice.setId("prod1");
        savedProductWithPrice.setPrix(new BigDecimal("50.00"));
        savedProductWithPrice.setNom("Produit Test");
        when(produitRepository.save(any(Produit.class))).thenReturn(savedProductWithPrice);
        when(colisMapper.toEntity(any(ColisRequest.class))).thenReturn(colisEntity);
        when(colisRepository.save(any(Colis.class))).thenReturn(colisEntity);
        when(historiqueRepository.save(any(HistoriqueLivraison.class)))
                .thenReturn(new HistoriqueLivraison());
        ColisResponse expectedResponse = new ColisResponse();
        when(colisMapper.toResponse(any(Colis.class))).thenReturn(expectedResponse);

        ColisResponse result = colisService.createDelivery(validRequest);

        assertNotNull(result);
        verify(zoneRepository).save(any(Zone.class));
        verify(produitRepository, times(validRequest.getProduits().size())).save(any(Produit.class));
        verify(colisRepository, times(2)).save(any(Colis.class));
        verify(historiqueRepository).save(any(HistoriqueLivraison.class));
    }

    @Test
    void updateColis_shouldUpdateFieldsAndRelations_whenStatusIsCREE() {
        validRequest.setDescription("Description mise à jour");
        validRequest.setPoids(10.0);

        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));
        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.of(zone));

        when(produitRepository.findByNomIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(produitRepository.save(any(Produit.class))).thenReturn(newSavedProduct);
        when(colisProduitRepository.findById(any())).thenReturn(Optional.empty());

        when(colisRepository.save(any(Colis.class))).thenReturn(colisEntity);
        when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());

        ColisResponse response = colisService.updateColis(COLIS_ID, validRequest);

        assertNotNull(response);
        assertEquals("Description mise à jour", colisEntity.getDescription());
        assertEquals(10.0, colisEntity.getPoids());
        verify(colisRepository, times(1)).save(colisEntity);
    }

    @Test
    void updateColis_shouldThrowValidationException_ifStatusIsNotCREE() {
        colisEntity.setStatus(ColisStatus.COLLECTE);
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));

        assertThrows(ValidationException.class, () -> colisService.updateColis(COLIS_ID, validRequest));
        verify(colisRepository, never()).save(any());
    }



    @Test
    void updateColis_shouldThrowValidationException_ifPrioriteIsInvalid() {
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));
        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.of(zone));

        validRequest.setPriorite("ULTRA_HAUTE");

        assertThrows(ValidationException.class, () -> colisService.updateColis(COLIS_ID, validRequest));
        verify(colisRepository, never()).save(any());
    }

    @Test
    void updateColis_shouldThrowRuntimeException_onZoneJsonReadingError() {
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));
        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.empty());

        ColisRequest requestForTest = new ColisRequest();
        requestForTest.setCodePostal("20000");
        requestForTest.setClientExpediteurEmail(VALID_EMAIL);
        requestForTest.setDestinataireEmail("dest@example.com");
        requestForTest.setPriorite("NORMAL");

        assertThrows(RuntimeException.class, () -> colisService.updateColis(COLIS_ID, requestForTest));
    }

    @Test
    void updateColis_shouldUpdateExistingProduct_andSaveItsDetails() {
        ProduitRequest existingProductRequest = new ProduitRequest();
        existingProductRequest.setNom("Existing Product");
        existingProductRequest.setCategorie("New Category");
        existingProductRequest.setPoids(1.5);
        existingProductRequest.setPrix(new BigDecimal("120.00"));
        existingProductRequest.setColisProduit(new ColisProduitRequest(10));
        validRequest.setProduits(List.of(existingProductRequest));

        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));
        when(produitRepository.findByNomIgnoreCase(anyString())).thenReturn(Optional.of(existingProduct));
        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.of(zone));
        when(produitRepository.save(any(Produit.class))).thenReturn(existingProduct);
        when(colisProduitRepository.findById(any())).thenReturn(Optional.of(new ColisProduit()));
        when(colisRepository.save(any(Colis.class))).thenReturn(colisEntity);
        when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());

        ColisResponse response = colisService.updateColis(COLIS_ID, validRequest);

        assertNotNull(response);
        verify(produitRepository, times(1)).save(existingProduct);
    }

    @Test
    void updateColisStatus_shouldSucceedAndUpdateToCollecte() {
        HistoriqueLivraisonRequest updateRequest = new HistoriqueLivraisonRequest();
        updateRequest.setStatut("COLLECTE");
        updateRequest.setCommentaire("Colis collecté");

        colisEntity.setLivreur(livreur);

        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));
        when(historiqueRepository.save(any(HistoriqueLivraison.class))).thenReturn(new HistoriqueLivraison());
        when(colisRepository.save(any(Colis.class))).thenReturn(colisEntity);
        when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());
        doNothing().when(emailService).sendNotification(anyString(), anyString(), anyString());

        ColisResponse response = colisService.updateColisStatus(COLIS_ID, updateRequest);

        assertNotNull(response);
        verify(historiqueRepository, times(1)).save(any(HistoriqueLivraison.class));
        assertEquals(ColisStatus.COLLECTE, colisEntity.getStatus());
        verify(emailService, times(2)).sendNotification(anyString(), anyString(), anyString());
    }

    @Test
    void updateColisStatus_shouldThrowValidationException_forFinishedColis() {
        colisEntity.setStatus(ColisStatus.LIVRE);
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));

        HistoriqueLivraisonRequest updateRequest = new HistoriqueLivraisonRequest();
        updateRequest.setStatut("EN_TRANSIT");

        assertThrows(ValidationException.class, () -> colisService.updateColisStatus(COLIS_ID, updateRequest));
    }

    @Test
    void updateStatus_shouldThrowValidationException_ifLivreurIsNullForTransit() {
        colisEntity.setStatus(ColisStatus.CREE);
        colisEntity.setLivreur(null);
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));

        HistoriqueLivraisonRequest updateRequest = new HistoriqueLivraisonRequest();
        updateRequest.setStatut("EN_TRANSIT");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                colisService.updateColisStatus(COLIS_ID, updateRequest));

        assertTrue(exception.getMessage().contains("aucun Livreur n'est assigné"));
        verify(colisRepository, never()).save(any());
    }

    @Test
    void updateStatus_shouldThrowValidationException_forInvalidEnumString() {
        colisEntity.setStatus(ColisStatus.CREE);
        colisEntity.setLivreur(new Livreur());
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));

        HistoriqueLivraisonRequest invalidRequest = new HistoriqueLivraisonRequest();
        invalidRequest.setStatut("NON_EXISTANT_STATUS");

        ValidationException exception = assertThrows(ValidationException.class, () ->
                colisService.updateColisStatus(COLIS_ID, invalidRequest));

        assertTrue(exception.getMessage().contains("Statut invalide fourni"));
        verify(colisRepository, never()).save(any());
    }

    @Test
    void getColisById_shouldReturnColisResponse_whenColisExists() {
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));
        ColisResponse expectedResponse = new ColisResponse();
        when(colisMapper.toResponse(colisEntity)).thenReturn(expectedResponse);

        ColisResponse actualResponse = colisService.getColisById(COLIS_ID);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getColisById_shouldThrowResourceNotFoundException_whenColisDoesNotExist() {
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> colisService.getColisById(COLIS_ID));
    }

    @Test
    void getAllColis_shouldReturnPaginatedColisResponses() {
        Pageable pageable = Pageable.unpaged();
        Page<Colis> colisPage = mock(Page.class);
        when(colisRepository.findAll(pageable)).thenReturn(colisPage);
        ColisResponseBasic colisResponseBasic = new ColisResponseBasic();
        when(colisPage.map(any())).thenReturn(Page.empty());

        Page<ColisResponseBasic> result = colisService.getAllColis(pageable);

        verify(colisPage, times(1)).map(any(Function.class));
        verify(colisRepository, times(1)).findAll(pageable);
        assertNotNull(result);
    }

    @Test
    void deleteColis_shouldDeleteColis_whenStatusIsCREE() {
        colisEntity.setStatus(ColisStatus.CREE);
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));

        colisService.deleteColis(COLIS_ID);

        verify(colisRepository, times(1)).delete(colisEntity);
    }

    @Test
    void deleteColis_shouldThrowValidationException_whenStatusIsNotCREE() {
        colisEntity.setStatus(ColisStatus.COLLECTE);
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));

        assertThrows(ValidationException.class, () -> colisService.deleteColis(COLIS_ID));
        verify(colisRepository, never()).delete(colisEntity);
    }

    @Test
    void getColisHistory_shouldReturnPaginatedHistoriqueResponses() {
        Pageable pageable = Pageable.unpaged();
        Page<HistoriqueLivraison> historiquePage = mock(Page.class);

        Colis colisEntity = new Colis();
        colisEntity.setId(COLIS_ID);

        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));
        when(historiqueRepository.findByColisId(COLIS_ID, pageable)).thenReturn(historiquePage);

        when(historiquePage.map(any())).thenReturn(Page.empty());

        Page<HistoriqueLivraisonResponse> result = colisService.getColisHistory(COLIS_ID, pageable);

        verify(colisRepository, times(1)).findById(COLIS_ID);
        verify(historiqueRepository, times(1)).findByColisId(COLIS_ID, pageable);
        verify(historiquePage, times(1)).map(any(Function.class));

        assertNotNull(result);
    }

    @Test
    void findColisByCriteria_shouldReturnMappedPage_whenCriteriaValid() {
        Colis colis = new Colis();
        Pageable pageable = Pageable.unpaged();
        colis.setId("1");
        ColisResponse colisResponse = new ColisResponse();
        Page<Colis> colisPage = new PageImpl<>(List.of(colis));

        when(colisRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(colisPage);
        when(colisMapper.toResponse(colis)).thenReturn(colisResponse);

        Page<ColisResponse> result = colisService.findColisByCriteria("CREE", "ZONE1", "Casablanca", "URGENT", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(colisRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(colisMapper, times(1)).toResponse(colis);
    }

    @Test
    void findColisByCriteria_shouldThrowValidationException_onInvalidStatut() {
        String invalidStatus = "STATUS_Faut";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                colisService.findColisByCriteria(invalidStatus, null, null, null, Pageable.unpaged()));

        assertTrue(exception.getMessage().contains("Le statut fourni dans la recherche est invalide"));
        verify(colisRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findColisByCriteria_shouldThrowValidationException_onInvalidPriorite() {
        String invalidPriorite = "ULTRA_HAUTE";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                colisService.findColisByCriteria("CREE", null, null, invalidPriorite, Pageable.unpaged()));

        assertTrue(exception.getMessage().contains("La priorité fournie dans la recherche est invalide"));
        verify(colisRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findByExpediteur_shouldReturnPaginatedColisResponses_whenExpediteurExists() {
        Pageable pageable = Pageable.unpaged();
        Page<Colis> colisPage = mock(Page.class);
        Colis colisEntity = new Colis();
        colisEntity.setId(COLIS_ID);

        when(expediteurRepository.findById("EXP-001")).thenReturn(Optional.of(expediteur));
        when(colisRepository.findByClientExpediteurId("EXP-001", pageable)).thenReturn(colisPage);
        when(colisPage.map(any())).thenReturn(Page.empty());

        Page<ColisResponse> result = colisService.findByExpediteur("EXP-001", pageable);

        verify(expediteurRepository, times(1)).findById("EXP-001");
        verify(colisRepository, times(1)).findByClientExpediteurId("EXP-001", pageable);
        verify(colisPage, times(1)).map(any(Function.class));
        assertNotNull(result);
    }

    @Test
    void findByDestinataire_shouldReturnPaginatedColisResponses_whenExpediteurExists() {
        Pageable pageable = Pageable.unpaged();
        Page<Colis> colisPage = mock(Page.class);
        Colis colisEntity = new Colis();
        colisEntity.setId(COLIS_ID);

        when(destinataireRepository.findById("DEST-001")).thenReturn(Optional.of(destinataire));
        when(colisRepository.findByDestinataireId("DEST-001", pageable)).thenReturn(colisPage);
        when(colisPage.map(any())).thenReturn(Page.empty());

        Page<ColisResponse> result = colisService.findByDestinataire("DEST-001", pageable);

        verify(destinataireRepository, times(1)).findById("DEST-001");
        verify(colisRepository, times(1)).findByDestinataireId("DEST-001", pageable);
        verify(colisPage, times(1)).map(any(Function.class));
        assertNotNull(result);
    }

    @Test
    void getColisSummary_shouldReturnCountGroupedByStatus() {
        List<Map<String, Object>> mockResults = List.of(
                Map.of("status", "CREE", "count", 5L),
                Map.of("status", "COLLECTE", "count", 3L)
        );
        when(colisRepository.countColisByStatut()).thenReturn(mockResults);

        Map<String, Long> summary = colisService.getColisSummary("status");

        assertEquals(2, summary.size());
        assertEquals(5L, summary.get("CREE"));
        assertEquals(3L, summary.get("COLLECTE"));
    }

    @Test
    void getColisSummary_shouldThrowValidationException_onMissingGroupByField() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                colisService.getColisSummary(""));

        assertTrue(exception.getMessage().contains("Le champ de regroupement (statut, zoneId, etc.) est obligatoire."));
    }

    @Test
    void getColisSummary_shouldThrowValidationException_onUnsupportedGroupByField() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                colisService.getColisSummary(ZONE_CODE));

        assertTrue(exception.getMessage().contains("Le regroupement par champ '" + ZONE_CODE + "' n'est pas supporté."));
    }

    @Test
    void assignColisToLivreur_shouldAssignAndUpdateStatus_whenValid() {
        Colis colis = new Colis();
        colis.setId(COLIS_ID);
        colis.setZone(zone);
        colis.setStatus(ColisStatus.CREE);
        colis.setDestinataire(destinataire);
        colis.setClientExpediteur(expediteur);

        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colis));
        when(livreurRepository.findById("LIV-001")).thenReturn(Optional.of(livreur));
        when(colisRepository.save(any(Colis.class))).thenAnswer(invocation -> {
            Colis savedColis = invocation.getArgument(0);
            return savedColis;
        });
        when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());

        ColisResponse response = colisService.assignColisToLivreur(COLIS_ID, "LIV-001");

        assertNotNull(response);
        assertEquals(livreur, colis.getLivreur());
        assertEquals(ColisStatus.COLLECTE, colis.getStatus());
        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void assignColisToLivreur_shouldThrowValidationException_onZoneMismatch() {
        Zone differentZone = new Zone();
        differentZone.setId("Z-002");
        differentZone.setNom("Different Zone");

        Livreur livreurWithDifferentZone = new Livreur();
        livreurWithDifferentZone.setId("LIV-003");
        livreurWithDifferentZone.setZoneAssigned(differentZone);

        Colis colis = new Colis();
        colis.setId(COLIS_ID);
        colis.setZone(zone);

        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colis));
        when(livreurRepository.findById("LIV-003")).thenReturn(Optional.of(livreurWithDifferentZone));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                colisService.assignColisToLivreur(COLIS_ID, "LIV-003"));

        assertTrue(exception.getMessage().contains("ne correspond pas à la zone assignée au livreur"));
    }

    @Test
    void getDetailedColisSummary_shouldReturnSummaryGroupedByLivreur() {
        List<Map<String, Object>> mockResults = List.of(
                Map.of("livreurId", "LIV-001", "totalPoids", 100.0, "totalColis", 10L),
                Map.of("livreurId", "LIV-002", "totalPoids", 150.0, "totalColis", 15L)
        );
        when(colisRepository.calculateSummaryByLivreur()).thenReturn(mockResults);

        List<Map<String, Object>> summary = colisService.getDetailedColisSummary("livreur");

        assertEquals(2, summary.size());
        assertEquals("LIV-001", summary.get(0).get("livreurId"));
        assertEquals(100.0, summary.get(0).get("totalPoids"));
        assertEquals(10L, summary.get(0).get("totalColis"));
    }

    @Test
    void getDetailedColisSummary_shouldReturnSummaryGroupedByZone() {
        List<Map<String, Object>> mockResults = List.of(
                Map.of("zoneCode", "20000", "totalPoids", 200.0, "totalColis", 20L),
                Map.of("zoneCode", "30000", "totalPoids", 250.0, "totalColis", 25L)
        );
        when(colisRepository.calculateSummaryByZone()).thenReturn(mockResults);

        List<Map<String, Object>> summary = colisService.getDetailedColisSummary("zone");

        assertEquals(2, summary.size());
        assertEquals("20000", summary.get(0).get("zoneCode"));
        assertEquals(200.0, summary.get(0).get("totalPoids"));
        assertEquals(20L, summary.get(0).get("totalColis"));
    }

    @Test
    void getDetailedColisSummary_shouldReturnSummaryThrowValidationException_onUnsupportedGroupByField() {
        final String groupFld = "null";

        ValidationException exception = assertThrows(ValidationException.class, () ->
                colisService.getDetailedColisSummary("null"));

        assertTrue(exception.getMessage().contains("Le regroupement par champ '" + groupFld + "' n'est pas supporté (livreur ou zone sont acceptés"));
    }

    @Test
    void getDelayedOrHighPriorityColis_shouldReturnColisList() {
        LocalDateTime dateLimiteCheck = LocalDateTime.now().minusHours(48);
        Colis delayedColis = new Colis();
        delayedColis.setId("COL-DEL-001");
        List<Colis> mockColisList = List.of(delayedColis);

        when(colisRepository.findByPrioriteOrDelayed(PrioriteStatus.URGENT, dateLimiteCheck))
                .thenReturn(mockColisList);
        when(colisMapper.toResponse(delayedColis)).thenReturn(new ColisResponse());

        List<ColisResponse> result = colisService.getDelayedOrHighPriorityColis(dateLimiteCheck);

        assertEquals(1, result.size());
    }
    @Test
    void shouldThrowRuntimeException_onIOException() throws IOException {
        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.empty());
        when(zoneRepository.findByCodePostal(any())).thenReturn(Optional.empty());
        when(zoneRepository.save(any(Zone.class))).thenThrow(new RuntimeException());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> colisService.createDelivery(validRequest));
        assertTrue(exception.getMessage().contains("Erreur lecture JSON zones"));

        verify(colisRepository, never()).save(any(Colis.class));
    }

    @Test
    void update_shouldThrowRuntimeException_onIOException() throws IOException {
      Colis colis = new Colis();
      colis.setId("COL-DEL-001");
      colis.setStatus(ColisStatus.CREE);
        when(colisRepository.findById("COL-DEL-001")).thenReturn(Optional.of(colis));
        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.empty());
        when(zoneRepository.findByCodePostal(any())).thenReturn(Optional.empty());
        when(zoneRepository.save(any(Zone.class))).thenThrow(new RuntimeException());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> colisService.updateColis("COL-DEL-001",validRequest));
        assertTrue(exception.getMessage().contains("Erreur lecture JSON zones"));

        verify(colisRepository, never()).save(any(Colis.class));
    }


}
