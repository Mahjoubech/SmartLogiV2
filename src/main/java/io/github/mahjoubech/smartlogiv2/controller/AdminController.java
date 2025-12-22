package io.github.mahjoubech.smartlogiv2.controller;

import io.github.mahjoubech.smartlogiv2.dto.request.AssignRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.PermissionRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.AssignResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.PermissionResponseDetail;
import io.github.mahjoubech.smartlogiv2.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.auditing.AuditingHandlerSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_PERMISSION')")
    @PostMapping("/permission/create")
    public ResponseEntity<PermissionResponseDetail> createPermission(@Valid @RequestBody PermissionRequest request){
        PermissionResponseDetail response = adminService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('DELETE_PERMISSIONS')")
    @DeleteMapping("/permission/delete/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable String id) {
        adminService.deletetePermission(id);
        return ResponseEntity.ok("Permission deleted successfully");
    }
    @PreAuthorize("hasRole('ADMIN') ")
    @GetMapping("/permission/all")
    public ResponseEntity<Page<PermissionResponseDetail>> getAllPermission(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "dateCreation") String sortBy,
                                                                     @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page , size, sort);
        Page<PermissionResponseDetail> permissionPages = adminService.getAllPermissions(pageable);
        return ResponseEntity.ok().body(permissionPages);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/Roles/AssignPermission")
    public ResponseEntity<AssignResponse> assignPermissionToRole( @Valid @RequestBody AssignRequest request) {
    AssignResponse response = adminService.assignPermissionToRole(request);
        return ResponseEntity.ok().body(response);
    }

}
