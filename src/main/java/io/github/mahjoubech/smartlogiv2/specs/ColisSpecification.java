package io.github.mahjoubech.smartlogiv2.specs;

import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ColisSpecification {


    public static Specification<Colis> hasStatut(ColisStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Colis> hasZoneId(String zoneId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("zone").get("id"), zoneId);
    }

    public static Specification<Colis> hasVilleDestination(String ville) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("villeDestination")), "%" + ville.toLowerCase() + "%");
    }

    public static Specification<Colis> hasPriorite(PrioriteStatus priorite) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("prioriteStatus"), priorite);
    }
}