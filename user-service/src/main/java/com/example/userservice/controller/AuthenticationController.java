package com.example.userservice.controller;

import com.example.userservice.auth.AuthenticationRequest;
import com.example.userservice.repository.RoleCustomRepo;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthenticationService;
import com.example.userservice.service.JwtService;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthenticationController {
    @Value("${application.security.jwt.Secret_key}")
    private String secretKey;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final RoleCustomRepo roleCustomRepo;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final AuthenticationService authenticationService;
    private String newAccessToken;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        // Extract the JWT token from the Authorization header
        String jwtToken = extractJwtToken(token);

        // Validate the JWT token
        boolean isValid = jwtService.validateToken(jwtToken);

        // Return response based on token validation result
        if (isValid) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
        }
    }

    // Helper method to extract the JWT token from the Authorization header
    private String extractJwtToken(String header) {
        // Header format: "Bearer <token>"
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    }



