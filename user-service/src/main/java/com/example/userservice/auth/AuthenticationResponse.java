package com.example.userservice.auth;
import com.example.userservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String firstname;
    private String lastname;
    private String access_token;
    private String email;
    private Set<Role> roles;


}
