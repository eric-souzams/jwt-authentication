package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.model.ConfirmationToken;
import io.security.FullRegistry.repository.ConfirmationTokenRepository;
import io.security.FullRegistry.service.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository repository;

    @Override
    @Transactional
    public void saveConfirmationToken(ConfirmationToken token) {
        log.info("Save new token {} on database", token.getToken());
        repository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public ConfirmationToken getToken(String token) {
        Optional<ConfirmationToken> result = repository.findByToken(token);
        if (result.isEmpty()) {
            log.error("Token {} not found", token);
            throw new IllegalStateException("Token not found");
        }

        log.info("Found token {} on database", token);
        return result.get();
    }

    @Override
    @Transactional
    public void setConfirmedAt(String token) {
        log.info("Setting confirmation on token {}", token);
        ConfirmationToken result = getToken(token);
        result.setConfirmedAt(LocalDateTime.now());
    }
}
