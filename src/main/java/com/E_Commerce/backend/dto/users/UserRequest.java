package com.E_Commerce.backend.dto.users;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String username;
    private String password;
    private String role;
}



