package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.dto.RegistrationUserRequest;
import io.security.FullRegistry.model.ConfirmationToken;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.service.ConfirmationTokenService;
import io.security.FullRegistry.service.RegistrationService;
import io.security.FullRegistry.service.UserService;
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

    @Override
    @Transactional
    public void register(RegistrationUserRequest request) {
        User user = userService.signUpUser(new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword()));

        String token = userService.generateConfirmationToken(user);
    }

    @Override
    @Transactional
    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(confirmationToken.getUser().getEmail());
    }
}
