package io.security.userservicejwt.service;

import io.security.userservicejwt.domain.Role;
import io.security.userservicejwt.domain.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();

}
