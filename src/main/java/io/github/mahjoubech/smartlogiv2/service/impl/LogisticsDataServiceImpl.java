package io.github.mahjoubech.smartlogiv2.service.impl;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.mapper.ZoneMapper;
import io.github.mahjoubech.smartlogiv2.mapper.ProduitMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import io.github.mahjoubech.smartlogiv2.model.entity.Produit;
import io.github.mahjoubech.smartlogiv2.repository.ZoneRepository;
import io.github.mahjoubech.smartlogiv2.repository.ProduitRepository;
import io.github.mahjoubech.smartlogiv2.service.LogisticsDataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogisticsDataServiceImpl implements LogisticsDataService {

    private final ZoneRepository zoneRepository;
    private final ProduitRepository produitRepository;
    private final ZoneMapper zoneMapper;
    private final ProduitMapper produitMapper;
    @Override
    @Transactional
    public ZoneResponse createZone(ZoneRequest request) {

        Optional<Zone> existingZone = zoneRepository.findByCodePostal(request.getCodePostal());
        if (existingZone.isPresent()) {
            throw new ConflictStateException("Zone avec code postal " + request.getCodePostal() + " existe déjà.");
        }

        boolean isValidInJson = false;
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = getClass().getResourceAsStream("/data/zone.json")) {

            if (is == null) {
                throw new RuntimeException("Erreur de configuration: Fichier zone.json non trouvé.");
            }

            List<ZoneRequest> zonesList = Arrays.asList(mapper.readValue(is, ZoneRequest[].class));

            isValidInJson = zonesList.stream()
                    .anyMatch(z -> z.getCodePostal().equals(request.getCodePostal()));

        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture du JSON zones.", e);
        }

        if (isValidInJson) {
            throw new  ConflictStateException("Zone avec code postal " + request.getCodePostal() + " existe déjà sur JSON file.");
        }

        Zone zone = zoneMapper.toEntity(request);

        return zoneMapper.toResponse(zoneRepository.save(zone));
    }

    @Override
    public ZoneResponse getZoneById(String zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "ID", zoneId));
        return zoneMapper.toResponse(zone);
    }

    @Override
    public Page<ZoneResponse> getAllZones(Pageable pageable) {
        return zoneRepository.findAll(pageable).map(zoneMapper::toResponse);
    }

    @Override
    @Transactional
    public ZoneResponse updateZone(String zoneId, ZoneRequest request) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", "ID", zoneId));
        boolean isValidInJson = false;
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = getClass().getResourceAsStream("/data/zone.json")) {

            if (is == null) {
                throw new RuntimeException("Erreur de configuration: Fichier zone.json non trouvé.");
            }

            List<ZoneRequest> zonesList = Arrays.asList(mapper.readValue(is, ZoneRequest[].class));

            isValidInJson = zonesList.stream()
                    .anyMatch(z -> z.getCodePostal().equals(zone.getCodePostal()));

        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture du JSON zones.", e);
        }

        if (isValidInJson) {
            throw new  ConflictStateException("Zone avec code postal " + request.getCodePostal() + " existe déjà sur JSON file.");
        }
        zoneMapper.updateEntityFromRequest(request, zone);
        return zoneMapper.toResponse(zoneRepository.save(zone));
    }

    @Override
    @Transactional
    public void deleteZone(String zoneId) {
        if (!zoneRepository.existsById(zoneId)) {
            throw new ResourceNotFoundException("Zone", "ID", zoneId);
        }
        zoneRepository.deleteById(zoneId);
    }


    @Override
    @Transactional
    public ProduitResponse createProduit(ProduitRequest request) {
        Produit produit = produitMapper.toEntity(request);
        // Tqdr tzid hna Anti-Duplication check 3la nom
        return produitMapper.toResponse(produitRepository.save(produit));
    }

    @Override
    public ProduitResponse getProduitById(String produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "ID", produitId));
        return produitMapper.toResponse(produit);
    }

    @Override
    public Page<ProduitResponse> findAllProduits(Pageable pageable) {
        return produitRepository.findAll(pageable).map(produitMapper::toResponse);
    }

    @Override
    @Transactional
    public ProduitResponse updateProduit(String produitId, ProduitRequest request) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", "ID", produitId));
        produitMapper.updateEntityFromRequest(request, produit);
        return produitMapper.toResponse(produitRepository.save(produit));
    }

    @Override
    @Transactional
    public void deleteProduit(String produitId) {
        if (!produitRepository.existsById(produitId)) {
            throw new ResourceNotFoundException("Produit", "ID", produitId);
        }
        produitRepository.deleteById(produitId);
    }

    @Override
    @Transactional
    public void deleteDuplicateProducts() {
        List<Produit> allProducts = produitRepository.findAll();
        Map<String, List<Produit>> groupedByName = allProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getNom().toLowerCase()));

        for (List<Produit> duplicates : groupedByName.values()) {
            if (duplicates.size() > 1) {
                Produit keepProduct = duplicates.get(0);
                for (int i = 1; i < duplicates.size(); i++) {
                    Produit duplicate = duplicates.get(i);
                    produitRepository.delete(duplicate);
                }
            }
        }
    }
}