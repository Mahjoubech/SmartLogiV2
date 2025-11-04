package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ZoneMapper {
    Zone toEntity(ZoneRequest dto);
    ZoneResponse toResponse(Zone entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "colis", ignore = true)
    void updateEntityFromRequest(ZoneRequest request, @MappingTarget Zone entity);
}
