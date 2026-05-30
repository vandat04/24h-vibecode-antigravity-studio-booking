package com.studio.service.impl;

import com.studio.entity.Role;
import com.studio.entity.User;
import com.studio.repository.RoleRepository;
import com.studio.repository.UserRepository;
import com.studio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public User register(User user, Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + roleId));
        user.setRole(role);
        // Note: In production, password hashing should be applied here (e.g., BCryptPasswordEncoder)
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    @Override
    public List<User> getStaffByRole(String roleName) {
        return userRepository.findByRoleRoleName(roleName);
    }

    @Override
    @Transactional
    public User toggleActiveStatus(Long id) {
        User user = findById(id);
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }
}
