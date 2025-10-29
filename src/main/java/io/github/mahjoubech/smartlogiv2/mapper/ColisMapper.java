package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.ColisRequest;
import io.github.mahjoubech.smartlogiv2.dto.ColisResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        ClientDestinataireMapper.class,
        LivreurMapper.class,
        ZoneMapper.class,
        HistoriqueLivraisonMapper.class,
        ColisProduitMapper.class
})
public interface ColisMapper {
        @Mapping(target = "clientExpediteur", ignore = true)
        @Mapping(target = "destinataire", ignore = true)
        @Mapping(target = "zone", ignore = true)
        @Mapping(target = "livreur", ignore = true)
        @Mapping(target = "historique", ignore = true)
        @Mapping(target = "produits", ignore = true)
        Colis toEntity(ColisRequest dto);

        @Mapping(target = "statut", source = "statut.name")
        @Mapping(target = "priorite", source = "priorite.name")
        @Mapping(target = "clientExpediteur", source = "clientExpediteur", qualifiedByName = "toClientResponse")
        @Mapping(target = "destinataire", source = "destinataire", qualifiedByName = "toDestinataireResponse")
        ColisResponse toResponse(Colis entity);

}
