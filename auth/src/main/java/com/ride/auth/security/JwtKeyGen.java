package com.ride.auth.security;

import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class JwtKeyGen {
    public static void main(String[] args) {
        byte[] key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512).getEncoded();
        System.out.println(Base64.getEncoder().encodeToString(key));
    }
}
