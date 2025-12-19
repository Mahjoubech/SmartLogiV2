package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.LoginRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.AuthResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", ignore = true)
    User toRegister(RegisterRequest register);
    User toLogin(LoginRequest login);
    @Mapping(target = "role", ignore = true)
    AuthResponse toAuthResponse(User user);
    default String map(RolesEntity role) {
        return role != null ? role.getName().name() : null;
    }
}
