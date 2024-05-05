package org.kerix.api.utils;


import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Token {
    private final String id;

    public Token(String Type) {
        this.id = new TokenBuilder(Base64.getEncoder().encodeToString(Type.getBytes())).generateId();
    }

    public String getId() {
        return id;
    }

    public String decode() {
        byte[] firstBytes = Base64.getDecoder().decode(id);
        String decodedString = new String(firstBytes);
        String[] parts = decodedString.split("=", 2);
        java.lang.StringBuilder firstPart = new java.lang.StringBuilder(parts[0]);
        java.lang.StringBuilder secondPart = new java.lang.StringBuilder(parts[1]);
        firstPart.append("=");
        for (int i = 0; i < 1; i++) {
            if (secondPart.charAt(0) == '=') {
                secondPart.deleteCharAt(0);
                firstPart.append("=");
            }
        }
        return new String(Base64.getDecoder().decode(firstPart.toString()));
    }
}

class TokenBuilder {
    private final String type;

    public TokenBuilder(String type) {
        this.type = type;
    }

    public String generateId() {
        long t = System.currentTimeMillis();
        double st = Math.sqrt(t);
        Base64.Encoder e = Base64.getEncoder();

        SecureRandom r = new SecureRandom();
        r.setSeed(t + r.nextLong());

        int ri = r.nextInt(10);
        double srt = Math.pow(ri, st / 4) * 3;
        double n = Character.getNumericValue(String.valueOf(t).charAt(ri)) / (ri / 2.3) * srt;

        String snt = Base64.getEncoder().encodeToString(String.valueOf(Math.floor(st * Math.pow(10, n))).getBytes());

        String randomString = e.encodeToString(generateRandomString().getBytes());

        return e.encodeToString((type + snt + randomString).getBytes());
    }

    private String generateRandomString() {
        int length = 10;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder(length);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            stringBuilder.append(characters.charAt(randomIndex));
        }
        return Base64.getEncoder().encodeToString((stringBuilder.toString()).getBytes());
    }
}

