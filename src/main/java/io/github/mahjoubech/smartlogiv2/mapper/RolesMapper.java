package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.RolesEntityRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.RolesResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RolesMapper {

    RolesEntity toEntity(RolesEntityRequest request);
    RolesResponse toResponse(RolesEntity entity);

}
