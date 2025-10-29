package io.github.mahjoubech.smartlogiv2.mapper;

import io.github.mahjoubech.smartlogiv2.dto.ProduitRequest;
import io.github.mahjoubech.smartlogiv2.dto.ProduitResponse;
import io.github.mahjoubech.smartlogiv2.model.entity.Produit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProduitMapper {
    Produit toEntity(ProduitRequest dto);

    ProduitResponse toResponse(Produit entity);
}
