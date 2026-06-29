package com.llbeauty.security;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class NxlApiKeyFilter implements Filter {
    @Value("${nxl.api.key}")
    private String configuredKey;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (request.getRequestURI().startsWith("/api/nxl")) {
            String apiKey = request.getHeader("x-api-key");
            if (apiKey == null || !apiKey.equals(configuredKey)) {
                HttpServletResponse response = (HttpServletResponse) res;
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"error\":\"Invalid or missing x-api-key\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}