package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.ClientExpediteur;
import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientDestinataireMapper {

    ClientExpediteur toClientExpediteur(ClientDestinataireRequest dto);
    ClientDestinataireResponse toClientResponse(ClientExpediteur entity);
    Destinataire toDestinataire(ClientDestinataireRequest dto);
    ClientDestinataireResponse toDestinataireResponse(Destinataire entity);

    // Nst3mlo had l'Method Bach n7awlo Listat (Pagination)
     List<ClientDestinataireResponse> toResponseList(List<ClientExpediteur> entities);
}
