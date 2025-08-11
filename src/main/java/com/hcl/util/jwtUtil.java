package com.hcl.util;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class jwtUtil {
 private static final String SECRET_KEY = "my_secret_key";

 public static String generateToken(String username,String role) {
     return Jwts.builder()
         .setSubject(username)
         .claim("role", role)
         .setIssuedAt(new Date())
         .setExpiration(new Date(System.currentTimeMillis() + 600000)) 
         .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
         .compact();
 }

public static String generateRefreshToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30 mins
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

 public static String validateToken(String token) {
     return Jwts.parser()
         .setSigningKey(SECRET_KEY)
         .parseClaimsJws(token)
         .getBody()
         .getSubject();
 }

}
