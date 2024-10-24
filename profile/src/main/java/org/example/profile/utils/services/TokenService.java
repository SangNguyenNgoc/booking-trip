package org.example.profile.utils.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final StringRedisTemplate redisTemplate;

    public void blacklistToken(String token) {
        // Lưu access token vào Redis với thời gian hết hạn
        redisTemplate.opsForValue().set(token, "blacklisted");
    }
    public Boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
