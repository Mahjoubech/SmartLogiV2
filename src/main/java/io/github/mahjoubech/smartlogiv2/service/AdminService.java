package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.AssignRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.PermissionRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RolesEntityRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ColisResponseBasic;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.AssignResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.PermissionResponseDetail;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.RolesResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
  PermissionResponseDetail createPermission(PermissionRequest request);
  void deletetePermission(String id);
  RolesResponse createRoles(RolesEntityRequest request);
  void deleteRoles(String id);
  AssignResponse assignPermissionToRole(AssignRequest request);
    Page<PermissionResponseDetail> getAllPermissions(Pageable pageable);
}
