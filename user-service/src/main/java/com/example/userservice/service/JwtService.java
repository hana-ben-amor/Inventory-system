package com.example.userservice.service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.userservice.entity.User;
import com.example.userservice.repository.RoleCustomRepo;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${application.security.jwt.Secret_key}")
    private String secretKey;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleCustomRepo roleCustomRepo;

    public String generateToken(User user, Collection<SimpleGrantedAuthority> authorities) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());

        return JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 7*24*60*60*1000))
                //.withClaim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim("roles", authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList()))

                .sign(algorithm);
    }
    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
            JWT.require(algorithm).build().verify(token);
            return true; // Token is valid
        } catch (JWTVerificationException exception) {
            // Invalid signature/claims
            return false;
        }
    }



}
