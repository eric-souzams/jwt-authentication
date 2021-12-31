package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.model.User;
import io.security.FullRegistry.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Configuration
@Slf4j @RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User with email {} not found in the database", email);
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User with email {} found in the database", email);
        }

        return user.get();
    }
}
