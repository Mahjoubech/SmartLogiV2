package io.github.mahjoubech.smartlogiv2.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Table(name = "gestionners")
@NoArgsConstructor
public class Gestionner extends User{

}
