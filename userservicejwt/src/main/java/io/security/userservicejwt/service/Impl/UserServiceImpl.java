package io.security.userservicejwt.service.Impl;

import io.security.userservicejwt.domain.Role;
import io.security.userservicejwt.domain.User;
import io.security.userservicejwt.repository.RoleRepository;
import io.security.userservicejwt.repository.UserRepository;
import io.security.userservicejwt.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User saveUser(User user) {
        return null;
    }

    @Override
    public Role saveRole(Role role) {
        return null;
    }

    @Override
    public void addRoleToUser(String username, String roleName) {

    }

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return null;
    }
}
