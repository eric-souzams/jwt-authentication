package io.security.FullRegistry.repository;

import io.security.FullRegistry.model.ConfirmationAccountToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationAccountToken, UUID> {

    Optional<ConfirmationAccountToken> findByToken(String token);

}
