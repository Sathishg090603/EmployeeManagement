package com.hcl.filters;

//JwtFilter.java
import javax.servlet.*;
import javax.servlet.http.*;

import com.hcl.util.jwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;

public class jwtFilter implements Filter {
	
	private static final String SECRET_KEY = "my_secret_key";
	
 @Override
 public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
         throws IOException, ServletException {
     HttpServletRequest httpRequest = (HttpServletRequest) request;
     String authHeader = httpRequest.getHeader("Authorization");

     if (authHeader != null && authHeader.startsWith("Bearer ")) {
         String token = authHeader.substring(7);
         try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();
                
                String username = jwtUtil.validateToken(token);
                httpRequest.setAttribute("username", username);
                String role = claims.get("role", String.class);
                httpRequest.setAttribute("role", role);
                System.out.println(role);
             // Optionally set user info in request context
             chain.doFilter(request, response);
         } catch (Exception e) {
             ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
         }
     } else {
         ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
     }
 }
}
