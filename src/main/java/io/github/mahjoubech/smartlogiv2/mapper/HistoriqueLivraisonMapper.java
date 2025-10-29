package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.HistoriqueLivraison;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoriqueLivraisonMapper {
    @Mapping(target = "colis", ignore = true)
    HistoriqueLivraison toEntity(HistoriqueLivraisonRequest dto);

    @Mapping(target = "statut", source = "statut.name")
    HistoriqueLivraisonResponse toResponse(HistoriqueLivraison entity);
}
