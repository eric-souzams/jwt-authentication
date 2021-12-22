package io.security.userservicejwt.dto;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Builder @Getter @Setter
public class RoleToUserDTO {

    private String username;
    private String roleName;

}
