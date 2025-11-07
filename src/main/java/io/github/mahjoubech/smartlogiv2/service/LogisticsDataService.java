package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface LogisticsDataService {

    ZoneResponse createZone(ZoneRequest request);
    ZoneResponse getZoneById(String zoneId);
    Page<ZoneResponse> getAllZones(Pageable pageable);
    ZoneResponse updateZone(String zoneId, ZoneRequest request);
    void deleteZone(String zoneId);

    ProduitResponse createProduit(ProduitRequest request);
    ProduitResponse getProduitById(String produitId);
    Page<ProduitResponse> findAllProduits(Pageable pageable);
    ProduitResponse updateProduit(String produitId, ProduitRequest request);
    void deleteProduit(String produitId);

    void deleteDuplicateProducts();
}