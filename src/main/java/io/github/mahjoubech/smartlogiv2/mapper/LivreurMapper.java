package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.LivreurRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.LivreurResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {ZoneMapper.class})
public interface LivreurMapper {
    @Mapping(target = "zoneAssignee", ignore = true)
    Livreur toEntity(LivreurRequest dto);
    @Mapping(target = "zoneAssignee", source = "zoneAssignee")
    @Mapping(target = "totalColis", ignore = true)
    LivreurResponse toResponse(Livreur entity);
}
