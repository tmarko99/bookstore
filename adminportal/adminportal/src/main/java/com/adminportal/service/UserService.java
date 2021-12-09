package com.adminportal.service;

import com.adminportal.entity.User;
import com.adminportal.entity.security.UserRole;
import com.adminportal.repository.RoleRepository;
import com.adminportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User createUser(User user, Set<UserRole> userRoles) throws Exception {
        User byUsername = userRepository.findByUsername(user.getUsername());

        if(byUsername != null){
            //throw new Exception("User already exists. Nothing will be done");
        }
        else{
            for(UserRole ur : userRoles){
                roleRepository.save(ur.getRole());
            }
            user.getUserRoles().addAll(userRoles);
            byUsername = userRepository.save(user);
        }
        return byUsername;
    }

    public User save(User user){
        return userRepository.save(user);
    }

}
