package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.PermissionRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RolesEntityRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.PermissionResponseDetail;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.RolesResponse;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.mapper.PermissionMapper;
import io.github.mahjoubech.smartlogiv2.mapper.RolesMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.Permission;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.repository.PermissionRepository;
import io.github.mahjoubech.smartlogiv2.repository.RolesEntityRepository;
import io.github.mahjoubech.smartlogiv2.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RolesEntityRepository rolesEntityRepository;
    private  final RolesMapper rolesMapper;
    @Override
    public PermissionResponseDetail createPermission(PermissionRequest request) {
        String name = request.getName().toUpperCase().trim().replaceAll("\\s+", "_");
        Optional<Permission> permission = permissionRepository.findByName(name);
        if(permission.isPresent()){
            throw new ConflictStateException("Permission with name " + name + " already exists");
        }
        Permission permissionEntity = permissionMapper.toEntity(request);
        return permissionMapper.toResponseDetail(permissionRepository.save(permissionEntity));

    }

    @Override
    public void deletetePermission(String id) {
        permissionRepository.deleteById(id);
    }

    @Override
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
}
