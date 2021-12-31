package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.dto.RoleRequest;
import io.security.FullRegistry.model.Role;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.repository.RoleRepository;
import io.security.FullRegistry.repository.UserRepository;
import io.security.FullRegistry.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

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
    public Integer enableAppUser(String email) {
        return null;
    }

    @Override
    @Transactional
    public Role saveRole(RoleRequest role) {
        Role newRole = new Role(null, role.getRoleName());
        log.info("Saving new role {} to the database", role.getRoleName());
        return roleRepository.save(newRole);
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
