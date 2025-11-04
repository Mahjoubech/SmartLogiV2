package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.dto.response.basic.ClientDestinataireResponseBasic;
import io.github.mahjoubech.smartlogiv2.model.entity.ClientExpediteur;
import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientDestinataireMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "colis", ignore = true)
    void updateDestinataire(ClientDestinataireRequest request, @MappingTarget Destinataire entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "colis", ignore = true)
    void updateExpediteur(ClientDestinataireRequest request, @MappingTarget ClientExpediteur entity);

    ClientExpediteur toClientExpediteur(ClientDestinataireRequest dto);
    ClientDestinataireResponse toClientResponse(ClientExpediteur entity);
    Destinataire toDestinataire(ClientDestinataireRequest dto);
    ClientDestinataireResponse toDestinataireResponse(Destinataire entity);
     List<ClientDestinataireResponse> toResponseList(List<ClientExpediteur> entities);


    @Mapping(target = "nom_complet", expression = "java(entity.getNom() + \" \" + entity.getPrenom())")
     ClientDestinataireResponseBasic  toClientResponseBasic(ClientExpediteur entity);
    @Mapping(target = "nom_complet", expression = "java(entity.getNom() + \" \" + entity.getPrenom())")
    ClientDestinataireResponseBasic  toDestinataireResponseBasic(Destinataire entity);

}
