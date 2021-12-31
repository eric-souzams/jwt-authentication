package io.security.FullRegistry.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    private String access_token;

    private String refresh_token;

}
