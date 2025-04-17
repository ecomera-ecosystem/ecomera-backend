package com.moushtario.ecomera.Configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Youssef
 * @version 1.0
 * @created 13/04/2025
 */

@Service
public class JwtService {

    private static final String SECRET_KEY ;

    static {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKey sk = keyGen.generateKey();
        SECRET_KEY = Base64.getEncoder().encodeToString(sk.getEncoded());
    }

    /**
     * Generates a JWT token for the given user.
     * @return the generated JWT token cryptographically signed with the secret key.
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token the signed JWT token (compact JWS string)
     * @return the username (subject) embedded in the token
     */
    public String extractUsername(String token) {
        return extractUsername(token, Claims::getSubject); // Extract the subject (username, email, userId etc.) from the token
    }

    /**
     * Extracts Clamins from the given JWT token.
     * @param token the signed JWT token (compact JWS string)
     * @param claimsResolver a function to extract specific claims from the token
     * @return a generic type T containing the extracted claims
     */
    private <T> T extractUsername(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims (payload) from the given JWT token.
     *
     * @param token the signed JWT token (compact JWS string)
     * @return Claims object containing all the data embedded in the token (subject, expiration, custom claims, etc.)
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser() // 1. Create a new JwtParserBuilder instance
                .verifyWith(getKey()) // 2. Provide the secret key used to verify the token's signature
                .build() // 3. Build the parser with the given key
                .parseSignedClaims(token) // 4. Parse the JWT and validate the signature
                .getPayload(); // 5. Extract the payload (Claims) from the token
    }

    /**
     * Generates a token
     *
     * @param userDetails the user details for which the token is generated. uses getUsername() method to extract the username from the UserDetails object.
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // Map that holds extra custom claims.

        return Jwts.builder()
                .claims(claims) // Set the claims
                .subject(userDetails.getUsername()) // Set the subject
                .issuedAt(new Date(System.currentTimeMillis())) // Set the issued date
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Set the expiration date for 10 hours
                .signWith(getKey()) // Sign the token with the key
                .compact(); // Compact the token: it generates the token as a string
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractUsername(token, Claims::getExpiration);
    }


}
