package io.security.FullRegistry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class RoleRequest {

    @NotBlank(message = "RoleName is not valid")
    private String roleName;

}
