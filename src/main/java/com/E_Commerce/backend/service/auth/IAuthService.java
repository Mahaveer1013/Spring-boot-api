package com.E_Commerce.backend.service.auth;

import com.E_Commerce.backend.dto.auth.AuthRequest;
import com.E_Commerce.backend.dto.users.UserResponse;

public interface IAuthService {

    UserResponse getCurrentUser();

    String verify(AuthRequest user);
}
