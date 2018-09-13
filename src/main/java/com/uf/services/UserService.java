package com.uf.services;

import java.util.List;
import java.util.Set;

import com.uf.domain.Users;
import com.uf.domain.security.UserRole;

public interface UserService {
	Users findByUsername(String username);

    Users findByEmail(String email);
    
    Users findByuserId(Long id);

    boolean checkUserExists(String username, String email);

    boolean checkUsernameExists(String username);

    boolean checkEmailExists(String email);
    
    void save (Users user);
    
    Users createUser(Users user, Set<UserRole> userRoles);
    
    Users saveUser (Users user); 
    
    List<Users> findUserList();

    void enableUser (String username);

    void disableUser (String username);
}
