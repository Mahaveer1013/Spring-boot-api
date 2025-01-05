package com.E_Commerce.backend.dto.users;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String role;
}
