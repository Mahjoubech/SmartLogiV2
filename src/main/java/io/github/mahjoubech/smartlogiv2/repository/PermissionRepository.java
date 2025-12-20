package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String> {
   Optional<Permission> findByName(String name);
}
