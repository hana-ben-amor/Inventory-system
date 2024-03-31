package com.example.userservice.service;


import com.example.userservice.auth.AuthenticationRequest;
import com.example.userservice.auth.AuthenticationResponse;
import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.repository.RoleCustomRepo;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Value("${application.security.jwt.Secret_key}")
    private String secretKey;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleCustomRepo roleCustomRepo;
    private final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            User user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(() -> new NoSuchElementException("User not found"));

            // Authenticate the user
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));

            List<Role> roles = roleCustomRepo.getRole(user);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            Set<Role> set = new HashSet<Role>();

            roles.forEach(c -> {
                set.add(new Role(c.getRole_id(), c.getName(), c.getDescription()));
                authorities.add(new SimpleGrantedAuthority(c.getName()));
            });
            user.setRoles(set);
            String jwtAccessToken = jwtService.generateToken(user, authorities);
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .access_token(jwtAccessToken)
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .roles(user.getRoles())
                    .build();
            return ResponseEntity.ok(response);
        }  catch (NoSuchElementException e) {
            // Return the response with a NOT_FOUND status
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (BadCredentialsException e) {
            // Return the response with a BAD_REQUEST status
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Return the response with an INTERNAL_SERVER_ERROR status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
 /*
    public ResponseEntity<?> register(RegisterRequest request) {
        try {
            User savedUser=userService.saveUser(new User(request.getUsername(),request.getFirstname(),request.getLastname(),request.getEmail(),request.getPassword(),new HashSet<>()));
            userService.addToUser(request.getEmail(),"ROLE_USER");
            User user=userRepository.findByEmail(request.getEmail()).orElseThrow();

            List<Role> role=null;
            if(savedUser!=null){
                role =roleCustomRepo.getRole(savedUser);
            }
            Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
            Set<Role> set=new HashSet<>();
            role.stream().forEach(c->{
                set.add(new Role(c.getRole_id(),c.getName(),c.getDescription()));
                authorities.add(new SimpleGrantedAuthority(c.getName()));
            });
            assert savedUser != null;
            savedUser.setRoles(set);
            String jwtAccessToken = jwtService.generateToken(user, authorities);
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .access_token(jwtAccessToken)
                    .firstname(savedUser.getFirstname())
                    .lastname(savedUser.getLastname())
                    .email(savedUser.getEmail())
                    .roles(savedUser.getRoles())
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }*/


}
