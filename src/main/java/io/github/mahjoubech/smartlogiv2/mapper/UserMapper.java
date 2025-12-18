package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.LoginRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.AuthResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toRegister(RegisterRequest register);
    User toLogin(LoginRequest login);
    AuthResponse toAuthResponse(User user);

}
