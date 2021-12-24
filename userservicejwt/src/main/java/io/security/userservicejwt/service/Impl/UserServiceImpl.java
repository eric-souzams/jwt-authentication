package io.security.userservicejwt.service.Impl;

import io.security.userservicejwt.domain.Role;
import io.security.userservicejwt.domain.User;
import io.security.userservicejwt.repository.RoleRepository;
import io.security.userservicejwt.repository.UserRepository;
import io.security.userservicejwt.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override @Transactional
    public User saveUser(User user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override @Transactional
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override @Transactional
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        Optional<User> user = userRepository.findByUsername(username);
        Optional<Role> role = roleRepository.findByName(roleName);

        if (user.isEmpty() || role.isEmpty()) {
            return;
        }

        user.get().getRoles().add(role.get());
    }

    @Override @Transactional(readOnly = true)
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    @Override @Transactional(readOnly = true)
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }
}
