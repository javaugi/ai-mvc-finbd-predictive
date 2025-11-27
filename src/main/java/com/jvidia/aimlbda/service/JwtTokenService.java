package com.jvidia.aimlbda.service;

import com.jvidia.aimlbda.utils.LogUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@Component
public class JwtTokenService {

    //node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
    @Value("${security.jwt.token.secret-key:035cb319f1550747b8fc393302c52069dcfd45a1b081e4d1f0eaa850a2a0173a}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:86400000}")
    private long jwtExpiration;

    public long getJwtExpiration() {
        return jwtExpiration;
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> roles) {
        log.debug("generateToken username {} roles {} ", username, roles);

        Map<String,Object> claims = new HashMap<>();
        claims.put("roles", roles);
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(validity)
                .signWith(getSignKey())
                .compact();
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.debug("generateToken username {} authorities {} ", username, authorities);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                //.signWith(getSignKey(), SignatureAlgorithm.HS384) // Explicitly specify HS384
                .compact();
    }

    // For HS384, you need a longer secret key (at least 384 bits = 48 characters)
    private Key getSignKey() {
        // Ensure the secret key is long enough for HS384
        String key = secretKey;
        if (key.length() < 48) {
            // Pad the key to meet minimum requirements
            key = String.format("%-48s", key).replace(' ', '0');
        }
        byte[] keyBytes = key.getBytes(); // Or use Decoders.BASE64 if your secret is base64 encoded
        //byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(HttpServletRequest req) {
        LogUtils.logRequest("JwtToenService.resolveToken", req);
        // 1. Check Authorization header first
        String bearerToken = req.getHeader("Authorization");
        log.debug("resolveToken bearerToken {} ", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 2. Check URL parameter (for OAuth2 redirect)
        String tokenParam = req.getParameter("token");
        log.debug("resolveToken tokenParam {} ", tokenParam);
        if (tokenParam != null && validateToken(tokenParam)) {
            return tokenParam;
        }

        // 3. Check session for OAuth2 authentication
        var session = req.getSession(false);
        log.debug("resolveToken :Check session for OAuth2 authentication session {} ", session);
        if (session != null) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.debug("resolveToken authentication {} ", authentication);
            if (authentication != null && authentication.isAuthenticated()) {
                return "SESSION_AUTH"; // Special marker for session auth
            }
        }

        return null;
    }

    public boolean validateToken(String token) {
        try {
            // For signed JWT (JWS), use parseSignedClaims, not parseEncryptedClaims
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith((SecretKey) getSignKey()) // Use verifyWith for signed tokens
                    .build()
                    .parseSignedClaims(token);

            log.debug("Token validated successfully for user: {}", jws.getPayload().getSubject());
            return true;
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            log.error("Invalid JWT signature for token: {}", token, ex);
            throw new SignatureException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", token, ex);
            throw new MalformedJwtException("Invalid JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", token, ex);
            throw new UnsupportedJwtException("Unsupported JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("JWT token is expired: {}", token, ex);
            throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), "The Token Provided is Expired");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", token, ex);
            throw new IllegalArgumentException("JWT claims string is empty");
        } catch (JwtException ex) {
            log.error("JWT validation failed: {}", token, ex);
            throw new JwtException("JWT validation failed");
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith((SecretKey) getSignKey())
                    .build()
                    .parseSignedClaims(token);
            return jws.getPayload().getSubject();
        } catch (JwtException ex) {
            log.error("Error extracting username from token: {}", token, ex);
            throw new JwtException("Failed to extract username from token");
        }
    }

    //ClassCastException: class java.util.LinkedHashMap cannot be cast to class java.lang.String (java.util.LinkedHashMap
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith((SecretKey) getSignKey())
                    .build()
                    .parseSignedClaims(token);

            @SuppressWarnings("unchecked")
            List<String> roles = jws.getPayload().get("roles", List.class);

            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (JwtException ex) {
            log.error("Error extracting authorities from token: {}", token, ex);
            throw new JwtException("Failed to extract authorities from token");
        }
    }

    public String getUsernameOld(String token) {
        //Jwts.builder().signWith(getSignKey()).claims(extractAllClaims(token));
        //Jwts.parser().decryptWith(getSecretKey()).build().parseEncryptedClaims(token).getPayload().getSubject();
        //Jwts.parser().decryptWith(getSecretKey()).build().parseEncryptedClaims(token).getPayload().getSubject();
        //return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        return extractUsername(token);
    }

    public SecretKey getSecretKey() {
        // Option 1: Using the Jwts.SIG builder (modern approach for v0.12.x+)
        //SecretKey key = Jwts.SIG.HS384.key().build();
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Helper method to extract all claims (if needed)
    public Claims extractAllClaims(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith((SecretKey) getSignKey())
                    .build()
                    .parseSignedClaims(token);

            return jws.getPayload();
        } catch (JwtException ex) {
            log.error("Error extracting claims from token: {}", token, ex);
            throw new JwtException("Failed to extract claims from token");
        }
    }

    // If you need to check token expiration
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException ex) {
            log.error("Error checking token expiration: {}", token, ex);
            return true; // Consider expired if we can't parse
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

}
