package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Data
@SuperBuilder
@Table(name = "client_expediteur")
@NoArgsConstructor
@AllArgsConstructor
public class ClientExpediteur extends User{
    @Column(name = "adresse", nullable = false)
    private String adresse ;
     @OneToMany(mappedBy = "clientExpediteur")
     private Set<Colis> colis;

}
