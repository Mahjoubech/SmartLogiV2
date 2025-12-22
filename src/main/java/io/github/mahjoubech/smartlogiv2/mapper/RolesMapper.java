package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.RolesEntityRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.AssignResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.RolesResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolesMapper {

    RolesEntity toEntity(RolesEntityRequest request);
    RolesResponse toResponse(RolesEntity entity);
    @Mapping(source = ".", target = "role")
    AssignResponse toAssignResponse(RolesEntity role);

}
