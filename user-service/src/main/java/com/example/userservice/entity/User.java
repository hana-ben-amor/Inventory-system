package com.example.userservice.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name = "Users")
public class User implements UserDetails {
    @PrePersist
    protected void onPersist(){
        if(username ==null){
            username="DefaultUser";
        }
        Date now=new Date();
        created_At=now;
        updated_At=now;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;
    private String username;

    public User(String username, String firstname, String lastname, String email, String password, Set<Role> roles) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User(String firstname, String lastname, String email, Date created_At, Date updated_At, Long age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.created_At = created_At;
        this.updated_At = updated_At;
    }

    @NotBlank(message = "Firstname is required")
    private String firstname;
    @NotBlank(message = "Lastname is required")
    private String lastname;
    @Column(unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    private String password;
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "User_id"),
            inverseJoinColumns = @JoinColumn(name = "Role_id")
    )
    private Set<com.example.userservice.entity.Role> roles = new HashSet<>();
    private Date created_At;
    private Date updated_At;


    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
        roles.stream().forEach(i-> authorities.add(new SimpleGrantedAuthority(i.getName()))
        );
        return List.of(new SimpleGrantedAuthority(authorities.toString()));
    }

    @Override
    public String getUsername(){
        return email;
    }

    @Override
    public String getPassword(){
        return password;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
