package io.security.FullRegistry.service.impl;

import io.jsonwebtoken.*;
import io.security.FullRegistry.model.User;
import io.security.FullRegistry.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.expiration-refresh}")
    private String expirationRefresh;

    @Value("${jwt.key-signature}")
    private String keySignature;

    @Override
    public String generateToken(User user) {
        long expirationTime = Long.parseLong(expiration);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(expirationTime);
        Instant instant = expirationDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date expirationDate = Date.from(instant);

        Instant issuedInstant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        Date issuedDate = Date.from(instant);

        return Jwts.builder()
                .setExpiration(expirationDate)
                .setIssuedAt(issuedDate)
                .setSubject(user.getEmail())
                .claim("userName", user.getFirstName())
                .claim("userId", user.getId().toString())
                .claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS512, keySignature)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        long expirationTime = Long.parseLong(expirationRefresh);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(expirationTime);
        Instant instant = expirationDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date expirationDate = Date.from(instant);

        Instant issuedInstant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        Date issuedDate = Date.from(instant);

        return Jwts.builder()
                .setExpiration(expirationDate)
                .setIssuedAt(issuedDate)
                .setSubject(user.getEmail())
                .claim("userName", user.getFirstName())
                .claim("userId", user.getId().toString())
                .claim("refresh", true)
                .signWith(SignatureAlgorithm.HS512, keySignature)
                .compact();
    }

    @Override
    public Claims getClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(keySignature)
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            Claims claims = getClaims(token);
            Date expirationDate = claims.getExpiration();
            LocalDateTime expirationDateTime = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            boolean isAfter = LocalDateTime.now().isAfter(expirationDateTime);

            return !isAfter;
        } catch (ExpiredJwtException exception) {
            return false;
        }
    }

    @Override
    public String getUserLogin(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }
}
