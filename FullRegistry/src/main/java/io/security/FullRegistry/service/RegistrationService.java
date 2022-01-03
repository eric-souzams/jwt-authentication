package io.security.FullRegistry.service;

import io.security.FullRegistry.dto.RegistrationUserRequest;
import io.security.FullRegistry.model.ConfirmationAccountToken;
import io.security.FullRegistry.model.User;

public interface RegistrationService {

    void register(RegistrationUserRequest request);

    void confirmToken(String token);

    void saveConfirmationToken(ConfirmationAccountToken token);

    ConfirmationAccountToken getToken(String token);

    void setConfirmedAt(String token);

    String generateConfirmationToken(User user);
}
