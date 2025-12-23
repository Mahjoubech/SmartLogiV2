package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.mapper.ColisMapper;
import io.github.mahjoubech.smartlogiv2.mapper.LivreurMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.repository.ColisRepository;
import io.github.mahjoubech.smartlogiv2.repository.LivreurRepository;
import io.github.mahjoubech.smartlogiv2.repository.RolesEntityRepository;
import io.github.mahjoubech.smartlogiv2.repository.ZoneRepository;
import io.github.mahjoubech.smartlogiv2.service.LivreurService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivreurServiceImpl implements LivreurService {

    private final LivreurRepository livreurRepository;
    private final ColisRepository colisRepository;
    private final ZoneRepository zoneRepository;
    private final LivreurMapper livreurMapper;
    private final ColisMapper colisMapper;
    private final RolesEntityRepository rolesEntityRepository;

    @Override
    @Transactional
    public LivreurResponse createLivreur(LivreurRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords don't match");
        }
        Zone zone = zoneRepository.findById(request.getZoneAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with ID: " + request.getZoneAssigneeId()));
        Optional<RolesEntity> roleLivreur = rolesEntityRepository.findByName(Roles.LIVREUR);
        Livreur livreur = livreurMapper.toEntity(request);
        livreur.setZoneAssigned(zone);
        livreur.setRole(roleLivreur.get());
        return livreurMapper.toResponse(livreurRepository.save(livreur));
    }
   @Override
   public  Page<LivreurResponse> getAllLivreurs(Pageable pageable) {
       Page<Livreur> livreurPage = livreurRepository.findAll(pageable);
       return livreurPage.map(livreurMapper::toResponse);
   }

    @Override
    public LivreurResponse getLivreurById(String livreurId) {
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur not found with ID: " + livreurId));
        return livreurMapper.toResponse(livreur);
    }
    @Override
    public void deleteLivreur(String livreurId){
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow( () -> new ResourceNotFoundException("Livreur not found with ID: " + livreurId));
        livreurRepository.delete(livreur);
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

    public Page<ColisResponse> getAssignedColis(String livreurId, Pageable pageable) {
        livreurRepository.findById(livreurId)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur", "ID", livreurId));
        Page<Colis> colisPage = colisRepository.findByLivreurId(livreurId, pageable);
        return colisPage.map(colisMapper::toResponse);
    }
    @Override
    public Page<LivreurResponse> searchLivreurs(String keyword, Pageable pageable) {
        Page<Livreur> livreurPage = livreurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword, pageable);

        return livreurPage.map(livreurMapper::toResponse);
    }
    @Override
    public Page<LivreurColisResponse> getLivreurColisCounts(Pageable pageable){
         return livreurRepository.getColisEvryLivreur(pageable);
    }


}
