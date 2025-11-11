package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.mapper.HistoriqueLivraisonMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.*;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import io.github.mahjoubech.smartlogiv2.repository.*;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import io.github.mahjoubech.smartlogiv2.service.EmailService;
import io.github.mahjoubech.smartlogiv2.service.impl.ColisServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    @Mock private EmailService emailService;

    @InjectMocks
    private ColisServiceImpl colisService;
    private final String VALID_EMAIL = "test@example.com";
    private final String COLIS_ID = "COL-123";
    private final String ZONE_CODE = "RABAT10";
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
        expediteur = new ClientExpediteur(); expediteur.setId("EXP-001"); expediteur.setEmail(VALID_EMAIL);
        destinataire = new Destinataire(); destinataire.setId("DEST-001"); destinataire.setEmail("dest@example.com");
        zone = new Zone(); zone.setId("Z-001"); zone.setCodePostal(ZONE_CODE);
        livreur = new Livreur(); livreur.setId("LIV-001"); livreur.setZoneAssigned(zone);

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
    }

    @Test
    void createDelivery_shouldSucceedWithUpsertProducts() {
        when(expediteurRepository.findByEmail(anyString())).thenReturn(Optional.of(expediteur));
        when(destinataireRepository.findByEmail(anyString())).thenReturn(Optional.of(destinataire));
        when(zoneRepository.findByCodePostal(anyString())).thenReturn(Optional.of(zone));

        when(produitRepository.findByNomIgnoreCase(anyString())).thenReturn(Optional.empty()); // New Product (Creation)

        when(produitRepository.save(any(Produit.class))).thenReturn(newSavedProduct);

        when(colisMapper.toEntity(any(ColisRequest.class))).thenReturn(colisEntity);
        when(colisRepository.save(any(Colis.class))).thenReturn(colisEntity);
        when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());

        colisService.createDelivery(validRequest);

        verify(produitRepository, times(1)).save(any(Produit.class));
        verify(colisRepository, times(2)).save(any(Colis.class)); // Save l'Lwli + Save l'Final
        verify(historiqueRepository, times(1)).save(any(HistoriqueLivraison.class));
    }
    @Test
    void createDelivery_shouldThrowConflictException_whenColisIsDuplicate() {
        List<Colis> existingList = Collections.singletonList(colisEntity);
        when(colisRepository.findByClientExpediteurEmailAndDestinataireEmailAndPoidsAndStatusAndVilleDestinationAndPrioriteStatus(
                 anyDouble(), any(ColisStatus.class), anyString(), any(PrioriteStatus.class)
        )).thenReturn(existingList);

        assertThrows(ConflictStateException.class, () -> colisService.createDelivery(validRequest));

        verify(colisRepository, never()).save(any());
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

       when(colisRepository.save(any(Colis.class))).thenReturn(colisEntity);
       when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());

       // Exécution
       colisService.updateColis(COLIS_ID, validRequest);

       // Vérification des updates
       assertEquals("Description mise à jour", colisEntity.getDescription());
       assertEquals(10.0, colisEntity.getPoids());
       verify(colisRepository, times(1)).save(colisEntity); // Doit faire un MERGE
   }
    @Test
    void updateColisStatus_shouldSucceedAndUpdateToCollecte() {
        HistoriqueLivraisonRequest updateRequest = new HistoriqueLivraisonRequest();
        updateRequest.setStatut("COLLECTE");

        colisEntity.setLivreur(new Livreur());
        when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colisEntity));
        when(colisRepository.save(any(Colis.class))).thenReturn(colisEntity);
        when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());
        colisService.updateColisStatus(COLIS_ID, updateRequest);
        verify(historiqueRepository, times(1)).save(any(HistoriqueLivraison.class));
        assertEquals(ColisStatus.COLLECTE, colisEntity.getStatus());
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
       verify(colisRepository,times(1)).delete(colisEntity);
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
        verify(colisRepository, times(1)).findByClientExpediteurId("EXP-001",pageable);
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
        verify(colisRepository, times(1)).findByDestinataireId("DEST-001",pageable);
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
    void assignColisToLivreur_shouldAssignAndUpdateStatus_whenValid() {
       Colis colis = new Colis();
         colis.setId(COLIS_ID);
         colis.setZone(zone);
         colis.setStatus(ColisStatus.CREE);
         when(colisRepository.findById(COLIS_ID)).thenReturn(Optional.of(colis));
            when(livreurRepository.findById("LIV-001")).thenReturn(Optional.of(livreur));
            colis.setLivreur(livreur);
            when(colisRepository.save(any(Colis.class))).thenReturn(colis);
            when(colisMapper.toResponse(any(Colis.class))).thenReturn(new ColisResponse());
            ColisResponse response = colisService.assignColisToLivreur(COLIS_ID, "LIV-001");
            assertNotNull(response);
            assertEquals(ColisStatus.COLLECTE, colis.getStatus());
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
    void getDelayedOrHighPriorityColis_shouldReturnColisList() {
        ZonedDateTime dateLimiteCheck = ZonedDateTime.now().minusHours(48);
        Colis delayedColis = new Colis();
        delayedColis.setId("COL-DEL-001");
        List<Colis> mockColisList = List.of(delayedColis);
        when(colisRepository.findByPrioriteOrDelayed(PrioriteStatus.URGENT, dateLimiteCheck))
                .thenReturn(mockColisList);
        when(colisMapper.toResponse(delayedColis)).thenReturn(new ColisResponse());
        List<ColisResponse> result = colisService.getDelayedOrHighPriorityColis(dateLimiteCheck);
        assertEquals(1, result.size());
    }
}