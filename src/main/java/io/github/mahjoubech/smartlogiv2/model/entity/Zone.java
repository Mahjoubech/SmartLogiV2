package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Data
@SuperBuilder
@Table(name = "zone")
@NoArgsConstructor
@AllArgsConstructor
public class Zone extends BaseEntity{

    @Column(name = "nom", nullable = false)
    private String nom;
    @Column(name = "code_postal", nullable = false)
    private  String codePostal;
    @OneToMany(mappedBy = "zone")
    private Set<Colis> colis;

}
