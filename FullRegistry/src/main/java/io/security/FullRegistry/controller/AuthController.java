package io.security.FullRegistry.controller;

import io.security.FullRegistry.dto.LoginRequest;
import io.security.FullRegistry.dto.RegistrationUserRequest;
import io.security.FullRegistry.dto.TokenResponse;
import io.security.FullRegistry.exception.CustomAuthException;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.service.JwtService;
import io.security.FullRegistry.service.RegistrationService;
import io.security.FullRegistry.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping(value = "/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegistrationUserRequest request) {
        registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        try {
            registrationService.confirmToken(token);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
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

}
