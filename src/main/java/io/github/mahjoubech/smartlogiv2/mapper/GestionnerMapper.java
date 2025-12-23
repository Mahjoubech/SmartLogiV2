package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.GestionnerRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.GestionResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Gestionner;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GestionnerMapper {
    @Mapping(target = "role", ignore = true)
    Gestionner toGestionner(GestionnerRequest gestionner);
    @Mapping(target = "role", source = "role")
    GestionResponse toResponse(Gestionner entity);
    default String map(RolesEntity role) {
        return role != null ? role.getName().name() : null;
    }
}
