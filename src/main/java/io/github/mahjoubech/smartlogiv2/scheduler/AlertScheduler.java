package io.github.mahjoubech.smartlogiv2.scheduler;

import io.github.mahjoubech.smartlogiv2.dto.response.detail.ColisResponse;
import io.github.mahjoubech.smartlogiv2.service.ColisService;
import io.github.mahjoubech.smartlogiv2.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlertScheduler {

    private final ColisService colisService;
    private final EmailService emailService;
    private final String MANAGER_EMAIL = "charkaouielmahjoub50@gmail.com";

    @Scheduled(fixedRate = 600000)
    public void checkAndSendAlerts() {
        LocalDateTime dateLimiteRetard = LocalDateTime.now().minusHours(48);
        List<ColisResponse> delayedColis = colisService.getDelayedOrHighPriorityColis(dateLimiteRetard);
        if (!delayedColis.isEmpty()) {
            String subject = "🚨 ALERTE URGENTE SDMS: " + delayedColis.size() + " colis nécessitent une action immédiate.";
            String colisListDetails = delayedColis.stream()
                    .map(c -> String.format("- Colis ID: %s, Statut: %s, Priorité: %s",
                            c.getId(), c.getStatut(), c.getPriorite()))
                    .collect(Collectors.joining("\n"));
            String body = String.format(
                    "Bonjour Gestionnaire,\n\nVeuillez trouver ci-dessous la liste des colis en retard ou à haute priorité:\n\n%s\n\nMerci de traiter ces colis en urgence.",
                    colisListDetails
            );
            emailService.sendNotification(MANAGER_EMAIL, subject, body);
        }
    }
}