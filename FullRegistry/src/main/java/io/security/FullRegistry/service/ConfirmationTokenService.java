package io.security.FullRegistry.service;

import io.security.FullRegistry.model.ConfirmationToken;

public interface ConfirmationTokenService {

    void saveConfirmationToken(ConfirmationToken token);

    ConfirmationToken getToken(String token);

    void setConfirmedAt(String token);

}
