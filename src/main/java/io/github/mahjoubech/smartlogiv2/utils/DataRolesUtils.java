package io.github.mahjoubech.smartlogiv2.utils;

import io.github.mahjoubech.smartlogiv2.model.entity.RolesEntity;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.repository.RolesEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataRolesUtils implements CommandLineRunner {
    private final RolesEntityRepository rolesRepository;
    @Override
    public void run(String... args) throws Exception {
        for (Roles roleEnum : Roles.values()) {

            rolesRepository.findByName(roleEnum)
                    .orElseGet(() -> {
                        RolesEntity role = RolesEntity.builder()
                                .name(roleEnum)
                                .build();
                        log.info("Creating role: {}", roleEnum);
                        return rolesRepository.save(role);
                    });
        }

        log.info("Roles seeding finished");
    }

}
