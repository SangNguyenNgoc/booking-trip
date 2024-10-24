package org.example.booking.utils.services;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AppUtils {
    public static String getRandomNumber(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder uid = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            uid.append(digit);
        }
        return uid.toString();
    }
}
