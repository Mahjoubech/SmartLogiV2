package io.github.mahjoubech.smartlogiv2.repository;

import io.github.mahjoubech.smartlogiv2.model.entity.Colis;
import io.github.mahjoubech.smartlogiv2.model.entity.Livreur;
import io.github.mahjoubech.smartlogiv2.model.enums.ColisStatus;
import io.github.mahjoubech.smartlogiv2.model.enums.PrioriteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ColisRepository extends JpaRepository<Colis,String> , JpaSpecificationExecutor<Colis> {

    Page<Colis> findByLivreurId(String livreurId, Pageable pageable);
    Page<Colis> findByClientExpediteurId(String expediteurId, Pageable pageable);
    Page<Colis> findByDestinataireId(String destinataireId,Pageable pageable);
    @Query(value = "SELECT c.status AS status, COUNT(c.id) AS count " +
            "FROM Colis c GROUP BY c.status")
    List<Map<String, Object>> countColisByStatut();
    @Query("SELECT c FROM Colis c WHERE c.prioriteStatus = :priorite " +
            "OR (c.status NOT IN ('LIVRE', 'ANNULE') AND c.dateCreation < :dateLimite)")
    List<Colis> findByPrioriteOrDelayed(
            @Param("priorite") PrioriteStatus priorite,
            @Param("dateLimite") ZonedDateTime dateLimite
    );
    @Query("SELECT SUM(c.poids) FROM Colis c WHERE c.zone.id = :zoneId")
    Double sumPoidsByZoneId(@Param("zoneId") String zoneId);
    @Query("SELECT c FROM Colis c WHERE " +
            "c.poids = :poids AND " +
            "c.status = :status AND " +
            "c.villeDestination = :villeDestination AND " +
            "c.prioriteStatus = :prioriteStatus")
    List<Colis> findByClientExpediteurEmailAndDestinataireEmailAndPoidsAndStatusAndVilleDestinationAndPrioriteStatus(
            @Param("poids") double poids,
            @Param("status") ColisStatus status,
            @Param("villeDestination") String villeDestination,
            @Param("prioriteStatus") PrioriteStatus prioriteStatus
    );
}

