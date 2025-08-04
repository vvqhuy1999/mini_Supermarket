package com.example.mini_supermarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MiniSupermarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniSupermarketApplication.class, args);
    }

}
