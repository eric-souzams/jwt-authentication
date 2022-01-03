package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.dto.RegistrationUserRequest;
import io.security.FullRegistry.exception.CustomBusinessException;
import io.security.FullRegistry.model.ConfirmationAccountToken;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.repository.ConfirmationTokenRepository;
import io.security.FullRegistry.service.EmailSenderService;
import io.security.FullRegistry.service.RegistrationService;
import io.security.FullRegistry.service.UserService;
import io.security.FullRegistry.utils.BuildEmails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final EmailSenderService emailSenderService;
    private final ConfirmationTokenRepository repository;

    @Override
    @Transactional
    public void register(RegistrationUserRequest request) {
        User user = userService.signUpUser(new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword()));

        String token = this.generateConfirmationToken(user);

        String confirmLink = "http://http://localhost:8080/api/auth/confirm?token=" + token;
        emailSenderService.send(
                user.getEmail(),
                BuildEmails.activeAccount(user.getFirstName(), confirmLink),
                "Active your account");
    }

    @Override
    @Transactional
    public void confirmToken(String token) {
        ConfirmationAccountToken confirmationAccountToken = this.getToken(token);

        if (confirmationAccountToken.getConfirmedAt() != null) {
            log.error("User {} already confirmed and enabled account", confirmationAccountToken.getUser().getEmail());
            throw new CustomBusinessException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationAccountToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            log.error("Token {} expired", confirmationAccountToken.getToken());
            throw new CustomBusinessException("Token expired");
        }

        this.setConfirmedAt(token);
        userService.enableAppUser(confirmationAccountToken.getUser().getEmail());
    }

    @Override
    @Transactional
    public String generateConfirmationToken(User user) {
        String token = generateSafeToken();

        ConfirmationAccountToken confirmationAccountToken = new ConfirmationAccountToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);

        this.saveConfirmationToken(confirmationAccountToken);

        log.info("Create new Token {} to user {}", token, user.getEmail());

        return token;
    }

    @Override
    @Transactional
    public void saveConfirmationToken(ConfirmationAccountToken token) {
        log.info("Save new token {} on database", token.getToken());
        repository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfirmationAccountToken getToken(String token) {
        Optional<ConfirmationAccountToken> result = repository.findByToken(token);
        if (result.isEmpty()) {
            log.error("Token {} not found", token);
            throw new CustomBusinessException("Token not found");
        }

        log.info("Found token {} on database", token);
        return result.get();
    }

    @Override
    @Transactional
    public void setConfirmedAt(String token) {
        log.info("Setting confirmation on token {}", token);
        ConfirmationAccountToken result = getToken(token);
        result.setConfirmedAt(LocalDateTime.now());
    }

    private String generateSafeToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[100];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}
