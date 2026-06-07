package com.vu.api.config;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

// Register this filter as a Spring bean so it can be injected into the security chain.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT signing key loaded from application configuration.
    @Value("${jwt.signerKey}")
    private String signerKey;

    // Executed once for each incoming request.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {
        // Read the Authorization header from the request.
        String authorizationHeader = request.getHeader("Authorization");

        // Only process Bearer tokens.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Remove the "Bearer " prefix and keep the raw JWT.
            String token = authorizationHeader.substring(7);
            try {
                // Parse the JWT string into a structured object.
                SignedJWT signedJWT = SignedJWT.parse(token);
                // Verify the signature before trusting any claims.
                if (verify(signedJWT)) {
                    // Read the payload claims from the token.
                    JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                    // Use the JWT subject as the authenticated user id.
                    String userId = claimsSet.getSubject();

                    // Read the roles claim if present.
                    List<String> roles = claimsSet.getStringListClaim("roles");
                    // Convert raw role strings into Spring Security authorities.
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (roles != null) {
                        // Map each role string to a granted authority.
                        for (String role : roles) {
                            authorities.add(new SimpleGrantedAuthority(role));
                        }
                    }

                    // Build a UserDetails principal for Spring Security.
                    UserDetails principal = User.withUsername(userId)
                            .password("")
                            .authorities(authorities)
                            .build();

                    // Create an authenticated token and attach it to the security context.
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    // Store the authentication so downstream code can access the current user.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ParseException | JOSEException | IllegalArgumentException e) {
                // If token parsing or verification fails, clear any existing authentication.
                SecurityContextHolder.clearContext();
            }
        }

        // Continue processing the remaining filters and eventually the controller.
        filterChain.doFilter(request, response);
    }

    // Verify the JWT signature using the configured HMAC signing key.
    private boolean verify(SignedJWT signedJWT) throws JOSEException {
        // A missing or blank key means verification cannot be performed.
        if (signerKey == null || signerKey.isBlank()) {
            return false;
        }

        // Hold the raw signing key bytes.
        byte[] keyBytes;
        try {
            // Try decoding the key as Base64 first.
            keyBytes = Base64.getDecoder().decode(signerKey);
        } catch (IllegalArgumentException ex) {
            // Fall back to UTF-8 bytes if the key is not Base64 encoded.
            keyBytes = signerKey.getBytes(StandardCharsets.UTF_8);
        }

        // Build a verifier that matches the HS512 signing algorithm.
        JWSVerifier verifier = new MACVerifier(keyBytes);
        // Return true only when the signature is valid.
        return signedJWT.verify(verifier);
    }
}
