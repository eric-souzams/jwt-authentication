package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.dto.RegistrationUserRequest;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.service.RegistrationService;
import io.security.FullRegistry.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;

    @Override
    public void register(RegistrationUserRequest request) {
        User user = userService.signUpUser(new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword()));

    }
}
