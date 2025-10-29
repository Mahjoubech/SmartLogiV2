package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@Table(name = "client_expediteur")
@NoArgsConstructor
@AllArgsConstructor
public class ClientExpediteur {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id ;
    @Column(name = "nom", nullable = false)
    private String nom ;
    @Column(name = "prenom", nullable = false)
    private String prenom ;
    @Column(name = "email", nullable = false, unique = true)
    private String email ;
    @Column(name = "telephone", nullable = false, unique = true)
    private String telephone ;
    @Column(name = "adresse", nullable = false)
    private String adresse ;
     @OneToMany(mappedBy = "clientExpediteur")
     private Set<Colis> colis;
}
