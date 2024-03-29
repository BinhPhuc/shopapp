package com.project.shopapp.components;

import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor

// JwtTokenService

public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private long expiration; // save to an enviroment variable

    @Value("${jwt.secret}")
    private String secretKey;


    public String generateToken (User user) throws InvalidParamException {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("phoneNumber", user.getPhoneNumber());
//            this.generateSecretKey();
            String token = Jwts
                    .builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParamException("cannot create jwt token: " + e.getMessage());
        }
    }

    private String generateSecretKey() {
        try {
            // Create a KeyGenerator instance for HMAC-SHA256 algorithm
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");

            // Generate a secret key
            SecretKey secretKey = keyGen.generateKey();

            // Convert the secret key to Base64 encoded string
            String base64EncodedKey = java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());
            return base64EncodedKey;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorithm not supported: " + e.getMessage());
            return null;
        }
    }


    private Key getSignInKey () {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        //P9nQesIs4Zw+fzcoO2NMKZh1B9pRFFiHCQOIybcs4Bk=
        //Decoders.BASE64.decode(P9nQesIs4Zw+fzcoO2NMKZh1B9pRFFiHCQOIybcs4Bk=);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims (String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim (String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired (String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public boolean isTokenValidated(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return !isTokenExpired(token) && phoneNumber.equals(userDetails.getUsername());
    }

}
