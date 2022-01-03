package io.security.FullRegistry.controller;

import io.jsonwebtoken.Claims;
import io.security.FullRegistry.dto.*;
import io.security.FullRegistry.exception.CustomAuthException;
import io.security.FullRegistry.exception.CustomBusinessException;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.service.JwtService;
import io.security.FullRegistry.service.RegistrationService;
import io.security.FullRegistry.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationUserRequest request) {
        try {
            registrationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (CustomBusinessException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        try {
            registrationService.confirmToken(token);
            return ResponseEntity.ok().build();
        } catch (CustomBusinessException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        try {
            User user = userService.signInUser(request.getEmail(), request.getPassword());

            TokenResponse tokenResponse = TokenResponse.builder()
                    .access_token(jwtService.generateToken(user))
                    .refresh_token(jwtService.generateRefreshToken(user))
                    .build();

            return ResponseEntity.ok(tokenResponse);
        } catch (CustomAuthException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.split(" ")[1];
            boolean isValidToken = jwtService.isValidToken(token);
            boolean isRefreshToken = false;

            if (isValidToken) {
                Claims claims = jwtService.getClaims(token);
                Boolean refresh = claims.get("refresh", Boolean.class);
                if (refresh != null) {
                    isRefreshToken = refresh;
                }
            }

            if (isValidToken && isRefreshToken) {
                String userEmail = jwtService.getUserLogin(token);
                User authenticatedUser = userService.getAuthenticatedUser(userEmail);

                TokenResponse tokenResponse = TokenResponse.builder()
                        .access_token(jwtService.generateToken(authenticatedUser))
                        .refresh_token(jwtService.generateRefreshToken(authenticatedUser))
                        .build();

                return ResponseEntity.ok(tokenResponse);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
    }

    @PostMapping(value = "/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        try {
            userService.forgotPassword(request);
            return ResponseEntity.ok().build();
        } catch (CustomBusinessException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @PutMapping(value = "/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestBody @Valid ResetPasswordRequest request) {
        try {
            userService.resetPassword(request, token);
            return ResponseEntity.ok().build();
        } catch (CustomBusinessException | CustomAuthException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

}
