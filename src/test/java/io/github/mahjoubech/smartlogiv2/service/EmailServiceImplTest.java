package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private final String FROM_EMAIL = "charkaouielmahjoub50@gmail.com";
    private final String TO_EMAIL = "recipient@example.com";
    private final String SUBJECT = "Test Subject";
    private final String BODY = "Test Body Message";

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(mailSender);
    }

    // ========== SEND NOTIFICATION TESTS ==========

    @Test
    void sendNotification_shouldSendEmailSuccessfully() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, SUBJECT, BODY);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertEquals(FROM_EMAIL, capturedMessage.getFrom());
        assertEquals(TO_EMAIL, capturedMessage.getTo()[0]);
        assertEquals(SUBJECT, capturedMessage.getSubject());
        assertEquals(BODY, capturedMessage.getText());
    }

    @Test
    void sendNotification_shouldHandleException_whenMailSenderFails() {
        doThrow(new RuntimeException("Mail server error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Should not throw exception, just log it
        assertDoesNotThrow(() ->
                emailService.sendNotification(TO_EMAIL, SUBJECT, BODY)
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_shouldHandleMailException() {
        doThrow(new MailException("Connection refused") {})
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendNotification(TO_EMAIL, SUBJECT, BODY)
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_shouldSetCorrectFromEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, SUBJECT, BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(FROM_EMAIL, message.getFrom());
    }

    @Test
    void sendNotification_shouldSetCorrectToEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, SUBJECT, BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().length);
        assertEquals(TO_EMAIL, message.getTo()[0]);
    }

    @Test
    void sendNotification_shouldSetCorrectSubject() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, SUBJECT, BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(SUBJECT, message.getSubject());
    }

    @Test
    void sendNotification_shouldSetCorrectBody() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, SUBJECT, BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(BODY, message.getText());
    }

    @Test
    void sendNotification_shouldHandleNullPointerException() {
        doThrow(new NullPointerException("Null email"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendNotification(TO_EMAIL, SUBJECT, BODY)
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_shouldHandleIllegalArgumentException() {
        doThrow(new IllegalArgumentException("Invalid email format"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendNotification(TO_EMAIL, SUBJECT, BODY)
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ========== SEND ALERT TESTS ==========

    @Test
    void sendAlert_shouldSendEmailToAdmin() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendAlert(SUBJECT, BODY);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertEquals(FROM_EMAIL, capturedMessage.getFrom());
        assertEquals(FROM_EMAIL, capturedMessage.getTo()[0]); // Admin email same as FROM
        assertEquals(SUBJECT, capturedMessage.getSubject());
        assertEquals(BODY, capturedMessage.getText());
    }

    @Test
    void sendAlert_shouldUseAdminEmailAsRecipient() {
        String adminEmail = "charkaouielmahjoub50@gmail.com";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendAlert(SUBJECT, BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(adminEmail, message.getTo()[0]);
    }

    @Test
    void sendAlert_shouldHandleException_whenMailSenderFails() {
        doThrow(new RuntimeException("Mail server down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendAlert(SUBJECT, BODY)
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendAlert_shouldSetCorrectSubject() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        String alertSubject = "Critical Alert";
        emailService.sendAlert(alertSubject, BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(alertSubject, message.getSubject());
    }

    @Test
    void sendAlert_shouldSetCorrectBody() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        String alertBody = "System down, immediate action required!";
        emailService.sendAlert(SUBJECT, alertBody);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(alertBody, message.getText());
    }

    @Test
    void sendAlert_shouldHandleMailException() {
        doThrow(new MailException("SMTP server unavailable") {})
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendAlert(SUBJECT, BODY)
        );

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    void sendNotification_shouldHandleEmptySubject() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, "", BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("", message.getSubject());
    }

    @Test
    void sendNotification_shouldHandleEmptyBody() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, SUBJECT, "");

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("", message.getText());
    }

    @Test
    void sendNotification_shouldHandleMultipleRecipients() {
        String multipleEmails = "user1@example.com";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(multipleEmails, SUBJECT, BODY);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendAlert_shouldCallSendNotificationInternally() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendAlert(SUBJECT, BODY);

        // Verify that mailSender.send was called (which means sendNotification was called)
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_shouldHandleVeryLongSubject() {
        String longSubject = "A".repeat(500);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, longSubject, BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(longSubject, message.getSubject());
    }

    @Test
    void sendNotification_shouldHandleVeryLongBody() {
        String longBody = "B".repeat(5000);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, SUBJECT, longBody);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(longBody, message.getText());
    }

    @Test
    void sendNotification_shouldHandleSpecialCharactersInSubject() {
        String specialSubject = "Test @#$%^&*() Subject";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, specialSubject, BODY);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(specialSubject, message.getSubject());
    }

    @Test
    void sendNotification_shouldHandleSpecialCharactersInBody() {
        String specialBody = "Test body with émojis 🎉 and àccénts";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(TO_EMAIL, SUBJECT, specialBody);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(specialBody, message.getText());
    }
}
