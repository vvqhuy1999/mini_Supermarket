package com.example.mini_supermarket;

import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
@Slf4j
class MiniSupermarketApplicationTests {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void hash() throws NoSuchAlgorithmException {
        String password = "123456";

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte[] digest = md.digest();
        // E10ADC3949BA59ABBE56E057F20F883E
        String md5Hash = DatatypeConverter.printHexBinary(digest);

        log.info("MD5 round 1: {} ", md5Hash);

        md.update(password.getBytes());
        digest = md.digest();
        md5Hash = DatatypeConverter.printHexBinary(digest);

        log.info("MD5 round 2: {} ", md5Hash);

        // Sử dụng PasswordEncoder được inject từ TestConfig
        String encodedPassword = passwordEncoder.encode(password);
        log.info("BCrypt encoded password: {}", encodedPassword);

        boolean matches = passwordEncoder.matches(password, encodedPassword);
        log.info("Password matches: {}", matches);
    }


}
