package com.snaplink.api.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String BASE62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890";
    private static final int BASE = BASE62_CHARACTERS.length();

    public String encode(Long number){
        StringBuilder sb = new StringBuilder();

        while(number > 0){
            int remainder = (int)(number % BASE);
            sb.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE;
        }

        return sb.reverse().toString();
    }
}
