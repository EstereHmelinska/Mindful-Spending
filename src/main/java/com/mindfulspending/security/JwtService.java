package com.mindfulspending.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT for an authenticated user.
     */
    public String generateToken(UserDetails userDetails) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expirationMs);
//during login the token is generated
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey()) //creates signature
                .compact(); //converts builder into string
    }

    /**
     * Extracts the user's email from the token subject.
     */
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Checks that the token belongs to the supplied user
     * and the token has not expired.
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(  //return type depends on the caller
                                 String token,
                                 Function<Claims, T> claimsResolver
    ) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        //during every protected request
        return Jwts.parser()
                .verifyWith(getSigningKey()) //same secret key
                .build()
                .parseSignedClaims(token)  //recreates and compares signatures
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor( //HMAC-SHA secret key user for HS256 JWT signing
                secret.getBytes(StandardCharsets.UTF_8)//UTF-8 because we convert string into bytes
        );
    }

}
