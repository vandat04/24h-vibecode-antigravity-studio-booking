package com.studio.service;

import com.studio.entity.User;
import java.util.List;

public interface UserService {
    User register(User user, Integer roleId);
    User findByUsername(String username);
    User findById(Long id);
    List<User> getStaffByRole(String roleName);
    User toggleActiveStatus(Long id);
}
