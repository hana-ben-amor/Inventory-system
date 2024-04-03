package com.example.userservice.controller;

import com.example.userservice.auth.AuthenticationRequest;
import com.example.userservice.repository.RoleCustomRepo;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.AuthenticationService;
import com.example.userservice.service.JwtService;
import com.example.userservice.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
    private Counter userLoginsCounter;

    @Autowired
        public void setUserLoginsCounter(MeterRegistry meterRegistry) {
        this.userLoginsCounter = Counter.builder("user_logins_total")
                .description("Count of user logins")
                .register(meterRegistry);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){

        ResponseEntity<?> responseEntity = authenticationService.authenticate(authenticationRequest);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            userLoginsCounter.increment();
        }
        return responseEntity;
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



