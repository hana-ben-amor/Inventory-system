package com.example.userservice.service.impl;


import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>());
        //userRegistrationsCounter.increment(); // Increment user registration counter
        return userRepository.save(user);
    }




    public Optional<User> findUserById(Long id){
        return userRepository.findById(id);
    }
    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void updateUser(User updatedUser) {
        userRepository.save(updatedUser);
    }
    @Override
    public void addToUser(String email, String roleName) {
        if(!userRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("User with this email: "+email+" doesn't exist");
        }
        Role role=roleRepository.findByName(roleName);
        if(role==null){
            throw new IllegalArgumentException("Role with name: "+roleName+" doesn't exist");
        }

        User user= (User) userRepository.findByEmail(email).get();
        user.getRoles().add(role);
    }
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public Long getUserIdByEmail(String email) {
        Optional<User> user=userRepository.findByEmail(email);

        // Check if the user is not found
        if (!user.isPresent()) {
            // Handle the situation where the user is not found
            // You might throw an exception or return a default value depending on your requirements
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        return user.get().getUser_id();
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
    public Long getTotalUsers() {
        return userRepository.count();
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addUser(User newUser) {
        userRepository.save(newUser);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public Optional<User> getUserById(Long headOfDepartmentId) {
        return userRepository.findById(headOfDepartmentId);
    }


    public Optional<User> getUserByUser_Id(Long userId) {
        return userRepository.findById(userId);
    }


    public void addUserToGroup(Long userId, Long groupId) {
        System.out.println("user added to a group");
    }
}
