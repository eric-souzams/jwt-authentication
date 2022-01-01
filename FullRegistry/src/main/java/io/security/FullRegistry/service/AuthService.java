package io.security.FullRegistry.service;

import io.security.FullRegistry.dto.RegistrationUserRequest;

public interface AuthService {

    void register(RegistrationUserRequest request);

    void confirmToken(String token);

}
