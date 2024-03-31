package com.example.userservice.controller;

import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.repository.RoleCustomRepo;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.JwtService;
import com.example.userservice.service.impl.UserServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleCustomRepo roleCustomRepo;
    @Autowired
    private UserRepository userRepository;

    public UserController() {
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        if (users != null && !users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            // Return an appropriate status code if no users are found
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            // Check if the user with the given ID exists
            Optional<User> existingUser = userService.findUserById(id);
            if (existingUser.isPresent()) {
                User userToUpdate = existingUser.get();
                // Update user details with the provided values (excluding roles and password)
                userToUpdate.setFirstname(updatedUser.getFirstname());
                userToUpdate.setLastname(updatedUser.getLastname());
                userToUpdate.setEmail(updatedUser.getEmail());
                // Update the user in the database
                userService.updateUser(userToUpdate);
                System.out.println("Request received for user update. User ID: " + id);

                return ResponseEntity.ok("User updated successfully");
            } else {
                System.out.println("Request received for user update. User ID: " + id);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user: " + e.getMessage());
        }
    }


    @PostMapping("/{email}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addRoleToUser(
            @PathVariable String email,
            @RequestParam String roleName) {
        try {
            userService.addToUser(email, roleName);
            return ResponseEntity.ok("Role added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Long> getUserIdByEmail(@RequestParam String email) {
        // Implement logic to retrieve user ID based on email from your database or user repository
        Long userId = userService.getUserIdByEmail(email);

        if (userId != null) {
            return ResponseEntity.ok(userId);
        } else {
            // Return an appropriate status code if user is not found
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("current")
    public User getCurrentUser(){
        return userService.getCurrentUser();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody User newUser) {
        try {
            // Set an empty password for the new user if not provided
            if (StringUtils.isBlank(newUser.getPassword())) {
                newUser.setPassword(""); // You may want to handle password hashing and security properly
            }

            // Retrieve the existing role by name (assuming "ROLE_USER" is already in the database)
            Role userRole = roleRepository.findByName("ROLE_USER");

            // Assign the existing role to the user if roles are not provided
            if (newUser.getRoles() == null || newUser.getRoles().isEmpty()) {
                newUser.setRoles(Collections.singleton(userRole));
            } else {
                // Check if each role already exists in the database, if not, save it
                Set<Role> existingRoles = new HashSet<>();
                for (Role role : newUser.getRoles()) {
                    Role existingRole = roleRepository.findByName(role.getName());
                    if (existingRole != null) {
                        existingRoles.add(existingRole);
                    } else {
                        throw new RoleNotFoundException("Role not found");
                    }
                }
                newUser.setRoles(existingRoles);
            }
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

            // Add the user to the database
            userService.addUser(newUser);

            // Retrieve the added user (assuming your service provides a method for this)
            Optional<User> user = userService.getUserByEmail(newUser.getEmail());

            if (user.isPresent()) {
                User addedUser = (User) user.get();

                // Retrieve user's authorities (roles)
                List<Role> roles = roleCustomRepo.getRole(addedUser);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

                roles.forEach(role ->
                        authorities.add(new SimpleGrantedAuthority(role.getName())));

                // Generate the access token for the added user
                String jwtAccessToken = jwtService.generateToken(addedUser, authorities);

                // Return the access token as a plain string
                return ResponseEntity.ok(jwtAccessToken);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding user: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User newUser) {
        try {
            // Set an empty password for the new user if not provided
            if (StringUtils.isBlank(newUser.getPassword())) {
                newUser.setPassword(""); // You may want to handle password hashing and security properly
            }

            // Retrieve the existing role by name (assuming "ROLE_USER" is already in the database)
            Role userRole = roleRepository.findByName("ROLE_USER");

            // Assign the existing role to the user if roles are not provided
            if (newUser.getRoles() == null || newUser.getRoles().isEmpty()) {
                newUser.setRoles(Collections.singleton(userRole));
            } else {
                // Check if each role already exists in the database, if not, save it
                Set<Role> existingRoles = new HashSet<>();
                for (Role role : newUser.getRoles()) {
                    Role existingRole = roleRepository.findByName(role.getName());
                    if (existingRole != null) {
                        existingRoles.add(existingRole);
                    } else {
                        throw new RoleNotFoundException("Role not found");
                    }
                }
                newUser.setRoles(existingRoles);
            }
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

            // Add the user to the database
            userService.addUser(newUser);

            // Generate the access token for the added user with default ROLE_USER
            Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            String jwtAccessToken = jwtService.generateToken(newUser, authorities);

            // Return the access token as a plain string
            return ResponseEntity.ok(jwtAccessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding user: " + e.getMessage());
        }
    }


}