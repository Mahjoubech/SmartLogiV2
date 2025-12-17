package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.ColisProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisProduitResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.ColisProduit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProduitMapper.class})
public interface ColisProduitMapper {
    @Mapping(target = "colis", ignore = true)
    @Mapping(target = "produit", ignore = true)
    ColisProduit toEntity(ColisProduitRequest dto);

    @Mapping(target = "produit", source = "produit")
    ColisProduitResponse toResponse(ColisProduit entity);
}
