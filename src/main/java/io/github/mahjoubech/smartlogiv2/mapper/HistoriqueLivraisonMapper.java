package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.HistoriqueLivraisonRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.HistoriqueLivraisonResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.HistoriqueLivraison;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoriqueLivraisonMapper {
    @Mapping(target = "colis", ignore = true)
    HistoriqueLivraison toEntity(HistoriqueLivraisonRequest dto);

    @Mapping(target = "statut", source = "status")
    HistoriqueLivraisonResponse toResponse(HistoriqueLivraison entity);
}
