package io.security.FullRegistry.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.security.FullRegistry.model.User;

public interface JwtService {

    String generateToken(User user);

    String generateRefreshToken(User user);

    Claims getClaims(String token) throws ExpiredJwtException;

    boolean isValidToken(String token);

    String getUserLogin(String token);

}
