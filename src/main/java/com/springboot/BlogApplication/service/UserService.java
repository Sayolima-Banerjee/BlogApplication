package com.springboot.BlogApplication.service;

import com.springboot.BlogApplication.entity.Role;
import com.springboot.BlogApplication.entity.User;
import com.springboot.BlogApplication.web.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);

    User findByUsername(String username);

    Role getRole(String name);
}
