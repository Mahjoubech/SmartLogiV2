package io.github.mahjoubech.smartlogiv2.model.entity;


import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
public abstract  class User extends BaseEntity {
        @Column(name = "nom", nullable = false)
        protected String nom ;
        @Column(name = "prenom", nullable = false)
        protected String prenom ;
        @Column(name = "email", nullable = false, unique = true)
        protected String email ;
        @Column(name = "telephone", nullable = false, unique = true)
        protected String telephone ;
        @Column(name = "password", nullable = false)
        protected String password ;
        @Enumerated(EnumType.STRING)
        @Column(name = "role")
        protected Roles role;

}
