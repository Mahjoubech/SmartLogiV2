package io.github.mahjoubech.smartlogiv2.utils;

import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.model.entity.User;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.repository.RolesEntityRepository;
import io.github.mahjoubech.smartlogiv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataAdminUtils implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RolesEntityRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
            if (userRepository.count() > 0) {
                log.info("Users already exist, skipping admin creation");
                return;
            }
            RolesEntity adminRole = roleRepository.findByName(Roles.ADMIN)
                    .orElseGet(() -> {
                        RolesEntity role = RolesEntity.builder()
                                .name(Roles.ADMIN)
                                .build();
                        return roleRepository.save(role);
                    });
            User admin = User.builder()
                    .nom("Admin")
                    .prenom("System")
                    .email("admin@gmail.com")
                    .telephone("0600000000")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .build();

            userRepository.save(admin);

            log.info(" Admin created: admin@smartlogi.com / admin123");
        }

    }

