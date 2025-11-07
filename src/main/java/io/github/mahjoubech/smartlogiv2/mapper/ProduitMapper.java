package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProduitMapper {
    Produit toEntity(ProduitRequest dto);

    ProduitResponse toResponse(Produit entity);
    @Mapping(target = "id", ignore = true) // Matbdlich l'ID dyal l'Produit l'MOUJOUD
    void updateEntityFromRequest(ProduitRequest request, @MappingTarget Produit entity);
}
