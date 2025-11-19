package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.LoginRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    void toRegister(RegisterRequest register, @MappingTarget User user);
    void toLogin(LoginRequest login, @MappingTarget User user);

}
