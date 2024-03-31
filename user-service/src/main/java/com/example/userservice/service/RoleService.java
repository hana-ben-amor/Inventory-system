package com.example.userservice.service;

import com.example.userservice.entity.Role;
import com.example.userservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long roleId) {
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        return optionalRole.orElse(null);
    }

    public Role createRole(Role role) {
        // Additional validation or business logic can be added here
        Role newRole= new Role(role.getName(),role.getDescription());
        newRole.setCreated_At(new Date(System.currentTimeMillis()));
        return roleRepository.save(newRole);
    }

    public Role updateRole(Long roleId, Role updatedRole) {
        Optional<Role> optionalExistingRole = roleRepository.findById(roleId);
        if (optionalExistingRole.isPresent()) {
            Role existingRole = optionalExistingRole.get();
            // Update properties of existingRole with updatedRole
            existingRole.setName(updatedRole.getName());
            existingRole.setDescription(updatedRole.getDescription());
            // Update other properties as needed

            return roleRepository.save(existingRole);
        } else {
            // Handle case where the role with roleId is not found
            return null;
        }
    }

    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    public Role findById(Long roleId) {
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        return optionalRole.orElse(null);
    }

    public void save(Role role) {
        roleRepository.save(role);
    }
}
