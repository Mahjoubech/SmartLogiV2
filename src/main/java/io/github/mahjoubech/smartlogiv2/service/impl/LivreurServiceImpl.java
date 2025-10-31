package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.mapper.LivreurMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.repository.ColisRepository;
import io.github.mahjoubech.smartlogiv2.repository.LivreurRepository;
import io.github.mahjoubech.smartlogiv2.repository.ZoneRepository;
import io.github.mahjoubech.smartlogiv2.service.LivreurService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LivreurServiceImpl implements LivreurService {

    private final LivreurRepository livreurRepository;
    private final ColisRepository colisRepository;
    private final ZoneRepository zoneRepository;
    private final LivreurMapper livreurMapper;
    private final ColisMapper colisMapper;

    @Override
    @Transactional
    public LivreurResponse createLivreur(LivreurRequest request) {
        Zone zone = zoneRepository.findById(request.getZoneAssigneeId())
                .orElseThrow(() -> new RuntimeException("Zone not found with ID: " + request.getZoneAssigneeId()));

        Livreur livreur = livreurMapper.toEntity(request);
        livreur.setZoneAssigned(zone);

        return livreurMapper.toResponse(livreurRepository.save(livreur));
    }

    @Override
    public LivreurResponse getLivreurById(String livreurId) {
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur not found with ID: " + livreurId));
        return livreurMapper.toResponse(livreur);
    }

    @Override
    @Transactional
    public LivreurResponse updateLivreur(String livreurId, LivreurRequest request) {
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur not found with ID: " + livreurId));

        if (!livreur.getZoneAssigned().getId().equals(request.getZoneAssigneeId())) {
            Zone newZone = zoneRepository.findById(request.getZoneAssigneeId())
                    .orElseThrow(() -> new RuntimeException("New Zone not found with ID: " + request.getZoneAssigneeId()));
            livreur.setZoneAssigned(newZone);
        }

        livreur.setNom(request.getNom());
        livreur.setPrenom(request.getPrenom());
        livreur.setTelephone(request.getTelephone());
        livreur.setVehicule(request.getVehicule());

        return livreurMapper.toResponse(livreurRepository.save(livreur));
    }

    @Override
    @Transactional
    public ColisResponse assignColisToLivreur(String colisId, String livreurId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis not found with ID: " + colisId));

        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur not found with ID: " + livreurId));

        colis.setLivreur(livreur);
        if (colis.getStatus() == ColisStatus.CREE) {
            colis.setStatus(ColisStatus.COLLECTE);
        }

        return colisMapper.toResponse(colisRepository.save(colis));
    }

    @Override
    public Page<ColisResponse> getAssignedColis(String livreurId, Pageable pageable) {
        Page<Colis> colisPage = colisRepository.findByLivreurId(livreurId, pageable);
        return colisPage.map(colisMapper::toResponse);
    }

    @Override
    public Page<LivreurResponse> searchLivreurs(String keyword, Pageable pageable) {
        Page<Livreur> livreurPage = livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword, pageable);

        return livreurPage.map(livreurMapper::toResponse);
    }
}
