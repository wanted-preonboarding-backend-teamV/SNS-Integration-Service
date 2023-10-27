package com.wanted.teamV.service.impl;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class AuthenticationCodeGenerator {
    private static final int LENGTH = 6;

    public String generate() {
        StringBuilder sb = new StringBuilder();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < LENGTH; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }
}
