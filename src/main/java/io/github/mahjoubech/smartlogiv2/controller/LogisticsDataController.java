package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.service.LogisticsDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v4/gestion")
@RequiredArgsConstructor
public class LogisticsDataController {
    private final LogisticsDataService logisticsDataService;

    @PostMapping("/zone")
    public ResponseEntity<ZoneResponse> createZone(@Valid @RequestBody ZoneRequest request){
        ZoneResponse zoneResponse= logisticsDataService.createZone(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(zoneResponse);
    }

    @PutMapping("/zone/{zoneId}")
    public ResponseEntity<ZoneResponse> updateZone(@PathVariable String zoneId,
                                                   @Valid @RequestBody ZoneRequest request){
        ZoneResponse zoneResponse= logisticsDataService.updateZone(zoneId,request);
        return ResponseEntity.ok(zoneResponse);
    }

    @DeleteMapping("/zone/{zoneId}")
    public ResponseEntity<String> deleteZone(@PathVariable String zoneId){
        logisticsDataService.deleteZone(zoneId);
        return ResponseEntity.ok("Zone deleted successfully");
    }
    @GetMapping("/zone/{zoneId}")
    public  ResponseEntity<ZoneResponse> getZone(@PathVariable String zoneId){
        ZoneResponse zoneResponse= logisticsDataService.getZoneById(zoneId);
        return ResponseEntity.ok(zoneResponse);
    }

    @GetMapping("/zone")
    public ResponseEntity<Page<ZoneResponse>> getAllZones(Pageable pageable){
        Page<ZoneResponse> zoneResponses= logisticsDataService.getAllZones(pageable);
        return ResponseEntity.ok(zoneResponses);

    }
}
