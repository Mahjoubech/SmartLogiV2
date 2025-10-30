package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ZoneResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ZoneService {
    ZoneResponse createZone(ZoneRequest request);
    ZoneResponse getZoneById(String zoneId);
    Page<ZoneResponse> findAll(Pageable pageable);
    ZoneResponse updateZone(String zoneId, ZoneRequest request);
    void deleteZone(String zoneId);
}
