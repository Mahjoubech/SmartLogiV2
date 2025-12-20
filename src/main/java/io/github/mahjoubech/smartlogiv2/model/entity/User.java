package io.github.mahjoubech.smartlogiv2.model.entity;


import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
public  class User extends BaseEntity implements UserDetails {
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
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "role_id")
        private RolesEntity role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if(role != null){
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name().toUpperCase()));

            authorities.addAll(
                    role.getPermissions().stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                            .collect(Collectors.toSet())
            );
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String getPassword() {
        return password;
    }
}
