package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.service.EmailSenderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor @Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String emailBody, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(emailBody, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("eric@email.com");

            log.info("Send email to {}", to);
            //mailSender.send(mimeMessage);
            //disable

        } catch (MessagingException exception) {
            log.error("Failed to send email", exception);
            throw new IllegalStateException("Failed to send email");
        }
    }
}
