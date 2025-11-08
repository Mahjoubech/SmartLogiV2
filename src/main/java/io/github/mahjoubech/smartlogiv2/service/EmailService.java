package io.github.mahjoubech.smartlogiv2.service;

public interface EmailService {
    void sendNotification(String toEmail, String subject, String body);
    void sendAlert(String subject, String body);
}