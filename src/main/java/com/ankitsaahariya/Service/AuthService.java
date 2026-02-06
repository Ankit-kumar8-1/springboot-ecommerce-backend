package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.SignupRequest;
import com.ankitsaahariya.dto.response.MessageResponse;


public interface AuthService {

    MessageResponse signup(SignupRequest  request);

    }
