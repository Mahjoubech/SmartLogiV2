package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.LivreurColisResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {ZoneMapper.class})
public interface LivreurMapper {
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "zoneAssigned", ignore = true)
    Livreur toEntity(LivreurRequest dto);
    @Mapping(target = "zoneAssigned", ignore = true)
    Livreur toLivreur(RegisterRequest livreur);
    @Mapping(target = "zoneAssignee", source = "zoneAssigned")
    LivreurResponse toResponse(Livreur entity);
    @Mapping(target = "nomComplet", expression = "java(entity.getNom() + \" \" + entity.getPrenom())")
    LivreurColisResponse toColisResponse(Livreur entity);
    default String map(RolesEntity role) {
        return role != null ? role.getName().name() : null;
    }
}
