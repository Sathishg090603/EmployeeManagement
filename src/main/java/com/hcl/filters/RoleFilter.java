package com.hcl.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String role = (String) httpRequest.getAttribute("role");
        String path = httpRequest.getRequestURI();
        System.out.println(role);

        if (path.contains("/admin") && !"Admin".equalsIgnoreCase(role)) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only");
            return;
        }

        chain.doFilter(request, response);
    }

}
