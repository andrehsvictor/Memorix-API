package andrehsvictor.memorix.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.Message.RecipientType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final MailSender mailSender;

    public void send(String to, String subject, String body) {
        ((JavaMailSender) mailSender).send(mimeMessage -> {
            mimeMessage.setRecipients(RecipientType.TO, to);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(body);
        });
    }
}
