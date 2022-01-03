package io.security.FullRegistry.service.impl;

import io.security.FullRegistry.dto.ForgotPasswordRequest;
import io.security.FullRegistry.dto.ResetPasswordRequest;
import io.security.FullRegistry.dto.RoleRequest;
import io.security.FullRegistry.exception.CustomAuthException;
import io.security.FullRegistry.exception.CustomBusinessException;
import io.security.FullRegistry.model.ResetPasswordToken;
import io.security.FullRegistry.model.Role;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.repository.ResetPasswordTokenRepository;
import io.security.FullRegistry.repository.RoleRepository;
import io.security.FullRegistry.repository.UserRepository;
import io.security.FullRegistry.service.EmailSenderService;
import io.security.FullRegistry.service.UserService;
import io.security.FullRegistry.utils.BuildEmails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResetPasswordTokenRepository passwordTokenRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public User signUpUser(User user) {
        boolean userExist = userRepository.findByEmail(user.getEmail()).isPresent();
        if (userExist) {
            log.error("User with email {} already exist", user.getEmail());
            throw new CustomBusinessException("Already exist a User using this email");
        }

        log.info("Saving new user {} to the database", user.getEmail());
        user.setPassword(encoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);

        addRoleToUser(user.getEmail(), "ROLE_USER");

        return createdUser;
    }


    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Optional<User> user = userRepository.findByEmail(forgotPasswordRequest.getEmail());
        if (user.isEmpty()) {
            log.error("User {} not found", forgotPasswordRequest.getEmail());
            throw new CustomBusinessException("User not found");
        }

        String token = this.generateResetPasswordToken(user.get());

        String confirmLink = "http://http://localhost:8080/api/auth/reset-password?token=" + token;
        emailSenderService.send(
                user.get().getEmail(),
                BuildEmails.activeAccount(user.get().getFirstName(), confirmLink),
                "Forgot password");
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest, String token) {
        ResetPasswordToken resetToken = this.getToken(token);
        if (resetToken.getConfirmedAt() != null) {
            log.error("User {} already change a password", resetToken.getUser().getEmail());
            throw new CustomBusinessException("Token expired");
        }

        LocalDateTime expiredAt = resetToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            log.error("Token {} expired", resetToken.getToken());
            throw new CustomBusinessException("Token expired");
        }

        if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            log.error("The passwords entered are not the same");
            throw new CustomBusinessException("The passwords entered are not the same");
        }

        resetToken.getUser().setPassword(encoder.encode(resetPasswordRequest.getPassword()));
        log.error("Update password from {}", resetToken.getUser().getEmail());

        this.setConfirmedResetAt(token);
    }

    @Override
    @Transactional
    public String generateResetPasswordToken(User user) {
        String token = generateSafeToken();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);

        this.saveResetPasswordToken(resetPasswordToken);

        log.info("Create new reset password token {} to user {}", token, user.getEmail());

        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public ResetPasswordToken getToken(String token) {
        Optional<ResetPasswordToken> result = passwordTokenRepository.findByToken(token);
        if (result.isEmpty()) {
            log.error("Token {} not found", token);
            throw new CustomBusinessException("Token not found");
        }

        log.info("Found token {} on database", token);
        return result.get();
    }

    @Override
    @Transactional
    public void setConfirmedResetAt(String token) {
        ResetPasswordToken result = getToken(token);
        log.info("Setting confirmation on token {}", token);
        result.setConfirmedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void saveResetPasswordToken(ResetPasswordToken token) {
        log.info("Save new token {} on database", token.getToken());
        passwordTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void enableAppUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User with email {} not found", email);
            throw new CustomBusinessException("User not found");
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

    private String generateSafeToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[100];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}
