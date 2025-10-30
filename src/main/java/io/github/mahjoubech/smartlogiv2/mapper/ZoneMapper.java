package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.ZoneRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ZoneResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Zone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneMapper {
    Zone toEntity(ZoneRequest dto);
    ZoneResponse toResponse(Zone entity);
}
