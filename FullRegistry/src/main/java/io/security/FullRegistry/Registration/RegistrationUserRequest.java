package io.security.FullRegistry.Registration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter @AllArgsConstructor
public class RegistrationUserRequest {

    @NotBlank(message = "First name is not valid")
    private final String firstName;

    @NotBlank(message = "Last name is not valid")
    private final String lastName;

    @Email(message = "This email is not valid")
    private final String email;

    @NotBlank(message = "Password is not valid")
    private final String password;

}
