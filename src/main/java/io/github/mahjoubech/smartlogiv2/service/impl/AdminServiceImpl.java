package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.AssignRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.GestionnerRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.PermissionRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RolesEntityRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.AssignResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.GestionResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.PermissionResponseDetail;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.RolesResponse;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.mapper.GestionnerMapper;
import io.github.mahjoubech.smartlogiv2.mapper.PermissionMapper;
import io.github.mahjoubech.smartlogiv2.mapper.RolesMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import io.github.mahjoubech.smartlogiv2.model.entity.Gestionner;
import io.github.mahjoubech.smartlogiv2.model.entity.Permission;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.repository.GestionnerRepository;
import io.github.mahjoubech.smartlogiv2.repository.PermissionRepository;
import io.github.mahjoubech.smartlogiv2.repository.RolesEntityRepository;
import io.github.mahjoubech.smartlogiv2.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RolesEntityRepository rolesEntityRepository;
    private  final RolesMapper rolesMapper;
    private final GestionnerRepository gestionnerRepository;
    private final GestionnerMapper gestionnerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PermissionResponseDetail createPermission(PermissionRequest request) {
        String name = request.getName().toUpperCase().trim().replaceAll("[^A-Z0-9]+", "_");
        Optional<Permission> permission = permissionRepository.findByName(name);
        if(permission.isPresent()){
            throw new ConflictStateException("Permission with name " + name + " already exists");
        }
        request.setName(name);
        Permission permissionEntity = permissionMapper.toEntity(request);
        return permissionMapper.toResponseDetail(permissionRepository.save(permissionEntity));

    }

    @Override
    public void deletetePermission(String id) {
        permissionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RolesResponse createRoles(RolesEntityRequest request) {
        Optional<RolesEntity> rolesEntity = rolesEntityRepository.findByName(request.getName());
        if(rolesEntity.isPresent()){
            throw new ConflictStateException("Role with name " + request.getName() + " already exists");
        }
        RolesEntity roleEntity = rolesMapper.toEntity(request);
        return rolesMapper.toResponse(rolesEntityRepository.save(roleEntity));
    }

    @Override
    public void deleteRoles(String id) {
        rolesEntityRepository.deleteById(id);
    }
    @Override
    @Transactional
    public AssignResponse assignPermissionToRole(AssignRequest request) {
        String roleId = request.getRoleId();
        String permissionId = request.getPermissionId();
        RolesEntity role = rolesEntityRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + roleId + " not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission with id " + permissionId + " not found"));

        role.getPermissions().add(permission);
        rolesEntityRepository.save(role);

        return rolesMapper.toAssignResponse(rolesEntityRepository.save(role));
    }
    @Override
    public Page<PermissionResponseDetail> getAllPermissions(Pageable pageable) {
        Page<Permission> colisPage = permissionRepository.findAll(pageable);
        return colisPage.map(permissionMapper::toResponseDetail);
    }
    @Override
    public Page<RolesResponse> getAllRoles(Pageable pageable) {
        Page<RolesEntity> colisPage = rolesEntityRepository.findAll(pageable);
        return colisPage.map(rolesMapper::toResponse);
    }

    @Override
    @Transactional
    public GestionResponse CreateManager(GestionnerRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords don't match");
        }
        Optional<RolesEntity> rolesEntity = rolesEntityRepository.findByName(Roles.MANAGER);
        Gestionner gestionner = gestionnerMapper.toGestionner(request);
        gestionner.setPassword(passwordEncoder.encode(request.getPassword()));
        gestionner.setRole(rolesEntity.get());
        return gestionnerMapper.toResponse(gestionnerRepository.save(gestionner));
    }

    @Override
    public void deleteManager(String id) {
        gestionnerRepository.deleteById(id);
    }

    @Override
    public Page<GestionResponse> getAllGestionners(Pageable pageable) {
        Page<Gestionner> gestionnerPage = gestionnerRepository.findAll(pageable);
        return gestionnerPage.map(gestionnerMapper::toResponse);
    }

}
