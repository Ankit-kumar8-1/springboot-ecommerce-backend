package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);
}
