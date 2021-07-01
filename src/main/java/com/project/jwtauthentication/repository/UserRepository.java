package com.project.jwtauthentication.repository;

import com.project.jwtauthentication.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByLogin(String login);

}
