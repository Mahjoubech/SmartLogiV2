package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.PermissionRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RolesEntityRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.PermissionResponseDetail;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.RolesResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;

public interface AdminService {
  PermissionResponseDetail createPermission(PermissionRequest request);
  void deletetePermission(String id);
  RolesResponse createRoles(RolesEntityRequest request);
  void deleteRoles(String id);
}
