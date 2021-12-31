package io.security.FullRegistry.service;

import io.security.FullRegistry.dto.RegistrationUserRequest;

public interface RegistrationService {

    void register(RegistrationUserRequest request);

}
