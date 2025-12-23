package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ClientDestinataireResponseBasic;
import io.github.mahjoubech.smartlogiv2.model.entity.ClientExpediteur;
import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientDestinataireMapper {
    @Mapping(target = "role", ignore = true)
    ClientExpediteur toClientExpediteur(RegisterRequest expediteur);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "colis", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateDestinataire(ClientDestinataireRequest request, @MappingTarget Destinataire entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "colis", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateExpediteur(ClientDestinataireRequest request, @MappingTarget ClientExpediteur entity);
    @Mapping(target = "role", ignore = true)
    ClientExpediteur toClientExpediteur(ClientDestinataireRequest dto);
    ClientDestinataireResponse toClientResponse(ClientExpediteur entity);
    @AfterMapping
    default void setExpediteurRole(ClientExpediteur entity, @MappingTarget ClientDestinataireResponse response) {
        response.setRole("Expéditeur");
    }
    Destinataire toDestinataire(ClientDestinataireRequest dto);
    ClientDestinataireResponse toDestinataireResponse(Destinataire entity);
     List<ClientDestinataireResponse> toResponseList(List<ClientExpediteur> entities);
    @AfterMapping
    default void setDestinataireRole(Destinataire entity, @MappingTarget ClientDestinataireResponse response) {
        response.setRole("Destinataire");
    }
    @Mapping(target = "nom_complet", expression = "java(entity.getNom() + \" \" + entity.getPrenom())")
     ClientDestinataireResponseBasic  toClientResponseBasic(ClientExpediteur entity);
    @Mapping(target = "nom_complet", expression = "java(entity.getNom() + \" \" + entity.getPrenom())")
    ClientDestinataireResponseBasic  toDestinataireResponseBasic(Destinataire entity);
    default String map(RolesEntity role) {
        return role != null ? role.getName().name() : null;
    }

}
