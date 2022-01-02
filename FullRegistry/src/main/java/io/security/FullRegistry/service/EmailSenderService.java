package io.security.FullRegistry.service;

public interface EmailSenderService {

    void send(String to, String emailBody, String subject);

}
