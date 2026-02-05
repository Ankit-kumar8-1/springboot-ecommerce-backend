package com.ankitsaahariya.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {


    private static final long JWT_TOKEN_VALIDITY= 30L * 24 * 60 * 60 * 1000;
    @Value("${jwt.secret}")
    private String secretKey;

}
