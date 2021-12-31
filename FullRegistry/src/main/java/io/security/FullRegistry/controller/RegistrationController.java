package io.security.FullRegistry.controller;

import io.security.FullRegistry.dto.RegistrationUserRequest;
import io.security.FullRegistry.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/auth")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(value = "/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegistrationUserRequest request) {
        registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/confirm")
    public ResponseEntity<Void> confirm(@RequestParam("token") String token) {
        registrationService.confirmToken(token);
        return ResponseEntity.ok().build();
    }

}
