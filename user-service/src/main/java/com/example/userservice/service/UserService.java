package com.example.userservice.service;


import com.example.userservice.entity.Role;
import com.example.userservice.entity.User;

public interface UserService {
    User saveUser(User user);
    com.example.userservice.entity.Role saveRole(Role role);

    void addToUser(String email,String roleName);
    User getCurrentUser();

}
