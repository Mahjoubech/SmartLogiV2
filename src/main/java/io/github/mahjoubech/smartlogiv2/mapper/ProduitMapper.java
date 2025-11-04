package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.request.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Produit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProduitMapper {
    Produit toEntity(ProduitRequest dto);

    ProduitResponse toResponse(Produit entity);
}
