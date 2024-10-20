package org.example.profile.utils.services;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AppUtils {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_";
    private static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final SecureRandom random = new SecureRandom();
    public static String generatePassword() {
        StringBuilder password = new StringBuilder(8);

        // Đảm bảo mật khẩu có ít nhất 1 ký tự thường, 1 ký tự in hoa, 1 số và 1 ký tự đặc biệt
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // Sinh các ký tự ngẫu nhiên khác cho đến khi đủ 8 ký tự
        for (int i = 4; i < 8; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        // Trộn các ký tự trong mật khẩu để đảm bảo tính ngẫu nhiên
        return shuffleString(password.toString());
    }

    // Hàm trộn ngẫu nhiên các ký tự trong chuỗi
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            // Hoán đổi vị trí của các ký tự
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
