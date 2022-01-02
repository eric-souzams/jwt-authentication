package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.dto.RegistrationUserRequest;
import io.security.FullRegistry.model.ConfirmationToken;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.service.ConfirmationTokenService;
import io.security.FullRegistry.service.EmailSenderService;
import io.security.FullRegistry.service.RegistrationService;
import io.security.FullRegistry.service.UserService;
import io.security.FullRegistry.utils.BuildEmails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSenderService emailSenderService;

    @Override
    @Transactional
    public void register(RegistrationUserRequest request) {
        User user = userService.signUpUser(new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword()));

        String token = userService.generateConfirmationToken(user);

        String confirmLink = "http://http://localhost:8080/api/auth/confirm?token=" + token;
        emailSenderService.send(
                user.getEmail(),
                BuildEmails.activeAccount(user.getFirstName(), confirmLink),
                "Active your account");
    }

    @Override
    @Transactional
    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            log.error("User {} already confirmed and enabled account", confirmationToken.getUser().getEmail());
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            log.error("Token {} expired", confirmationToken.getToken());
            throw new IllegalStateException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(confirmationToken.getUser().getEmail());
    }
}
