package com.example.tallybook.security;

import com.example.tallybook.mapper.UserMapper;
import com.example.tallybook.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find user by username
        User user = userMapper.findByUsername(username);
        
        // If not found, try to find by phone number
        if (user == null) {
            user = userMapper.findByPhone(username);
        }
        
        // If still not found, throw exception
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username or phone: " + username);
        }
        
        // If user is disabled
        if (user.getStatus() != 1) {
            throw new UsernameNotFoundException("User is disabled: " + username);
        }
        
        // Return UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 