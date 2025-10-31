package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.mapper.ZoneMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import io.github.mahjoubech.smartlogiv2.repository.ZoneRepository;
import io.github.mahjoubech.smartlogiv2.service.ZoneService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;

    @Override
    @Transactional
    public ZoneResponse createZone(ZoneRequest request) {
        Zone zone = zoneMapper.toEntity(request);
        return zoneMapper.toResponse(zoneRepository.save(zone));
    }

    @Override
    public ZoneResponse getZoneById(String zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found with ID: " + zoneId));
        return zoneMapper.toResponse(zone);
    }

    @Override
    public Page<ZoneResponse> findAll(Pageable pageable) {
        Page<Zone> zonePage = zoneRepository.findAll(pageable);
        return zonePage.map(zoneMapper::toResponse);
    }

    @Override
    @Transactional
    public ZoneResponse updateZone(String zoneId, ZoneRequest request) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found for update with ID: " + zoneId));

        zoneMapper.updateEntityFromRequest(request, zone);

        return zoneMapper.toResponse(zoneRepository.save(zone));
    }

    @Override
    @Transactional
    public void deleteZone(String zoneId) {
        if (!zoneRepository.existsById(zoneId)) {
            throw new RuntimeException("Zone not found for deletion with ID: " + zoneId);
        }
        zoneRepository.deleteById(zoneId);
    }
}