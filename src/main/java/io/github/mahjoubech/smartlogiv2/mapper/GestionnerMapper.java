package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.model.entity.Gestionner;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GestionnerMapper {

    Gestionner toGestionner(RegisterRequest gestionner);
}
