package com.E_Commerce.backend.service.auth;

import com.E_Commerce.backend.dto.auth.AuthRequest;
import com.E_Commerce.backend.dto.users.UserMapper;
import com.E_Commerce.backend.dto.users.UserResponse;
import com.E_Commerce.backend.lib.exception.AuthenticationException;
import com.E_Commerce.backend.model.Users;
import com.E_Commerce.backend.repository.UserRepository;
import com.E_Commerce.backend.service.utils.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    public AuthService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return userMapper.toResponse((Users) authentication.getPrincipal());
            } else {
                throw new AuthenticationException("No authenticated user found");
            }
        } catch (Exception e) {
            // Log exception here
            throw new AuthenticationException("Error getting the current user: " + e.getMessage());
        }
    }

    @Override
    public String verify(AuthRequest user) {
        try {
            // Attempt to authenticate the user
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            // If authenticated successfully, generate JWT token
            if (authentication.isAuthenticated()) {
                Users userData = userRepository.findByUsername(user.getUsername());
                if (userData == null) {
                    throw new AuthenticationException("User not found with the given username");
                }
                return jwtService.generateToken(userData.getId(), userData.getUsername(), userData.getEmail(), userData.getRole().toString());
            } else {
                throw new AuthenticationException("Invalid username or password");
            }
        } catch (BadCredentialsException e) {
            // Catch authentication failure, such as invalid credentials
            throw new AuthenticationException("Invalid username or password");
        } catch (Exception e) {
            // Log exception and return a generic error message
            throw new AuthenticationException("Error during authentication: " + e.getMessage());
        }
    }
}
