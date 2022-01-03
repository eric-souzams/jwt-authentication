package io.security.FullRegistry.service;

import io.security.FullRegistry.dto.ForgotPasswordRequest;
import io.security.FullRegistry.dto.ResetPasswordRequest;
import io.security.FullRegistry.dto.RoleRequest;
import io.security.FullRegistry.model.ResetPasswordToken;
import io.security.FullRegistry.model.Role;
import io.security.FullRegistry.model.User;

public interface UserService {

    User signUpUser(User user);

    User signInUser(String email, String password);

    User getAuthenticatedUser(String email);

    void enableAppUser(String email);

    Role saveRole(RoleRequest role);

    void addRoleToUser(String email, String roleName);

    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest, String token);

    String generateResetPasswordToken(User user);

    ResetPasswordToken getToken(String token);

    void setConfirmedResetAt(String token);

    void saveResetPasswordToken(ResetPasswordToken token);

}
