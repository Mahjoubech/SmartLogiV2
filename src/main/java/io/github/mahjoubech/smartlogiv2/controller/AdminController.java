package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.PermissionRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.PermissionResponseDetail;
import io.github.mahjoubech.smartlogiv2.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/permission")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @PostMapping("/create")
    public ResponseEntity<PermissionResponseDetail> createPermission(@Valid @RequestBody PermissionRequest request){
        PermissionResponseDetail response = adminService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
