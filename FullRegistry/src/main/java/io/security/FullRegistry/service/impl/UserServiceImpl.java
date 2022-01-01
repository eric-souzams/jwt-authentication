package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.dto.RoleRequest;
import io.security.FullRegistry.exception.CustomAuthException;
import io.security.FullRegistry.exception.CustomBusinessException;
import io.security.FullRegistry.model.ConfirmationToken;
import io.security.FullRegistry.model.Role;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.repository.RoleRepository;
import io.security.FullRegistry.repository.UserRepository;
import io.security.FullRegistry.service.ConfirmationTokenService;
import io.security.FullRegistry.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    @Transactional
    public User signUpUser(User user) {
        boolean userExist = userRepository.findByEmail(user.getEmail()).isPresent();
        if (userExist) {
            log.error("User with email {} already exist", user.getEmail());
            throw new IllegalStateException("Already exist a User using this email");
        }

        log.info("Saving new user {} to the database", user.getEmail());
        user.setPassword(encoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);

        addRoleToUser(user.getEmail(), "ROLE_USER");

        return createdUser;
    }

    @Override
    @Transactional
    public String generateConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        log.info("Create new Token {} to user {}", token, user.getEmail());

        return token;
    }

    @Override
    public User signInUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User {} not found", email);
            throw new CustomAuthException("User not found");
        }

        if (!user.get().getEnabled()) {
            log.error("User {} not are enable", email);
            throw new CustomAuthException("User not are enable");
        }

        boolean isMatcher = encoder.matches(password, user.get().getPassword());
        if (!isMatcher) {
            log.error("Email or password is not valid");
            throw new CustomAuthException("Email or password is not valid");
        }

        return user.get();
    }

    @Override
    public User getAuthenticatedUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User {} not found", email);
            throw new CustomBusinessException("User not found");
        }

        return user.get();
    }

    @Override
    @Transactional
    public void enableAppUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User with email {} not found", email);
            throw new IllegalStateException("User not found");
        }

        log.info("User {} now are enable", email);
        user.get().setEnabled(true);
    }

    @Override
    @Transactional
    public Role saveRole(RoleRequest role) {
        log.info("Saving new role {} to the database", role.getRoleName());
        return roleRepository.save(new Role(null, role.getRoleName()));
    }

    @Override
    @Transactional
    public void addRoleToUser(String email, String roleName) {
        log.info("Adding role {} to user {}", roleName, email);
        Optional<User> user = userRepository.findByEmail(email);
        Optional<Role> role = roleRepository.findByName(roleName);

        if (user.isEmpty() || role.isEmpty()) {
            return;
        }

        user.get().getRoles().add(role.get());
    }
}
