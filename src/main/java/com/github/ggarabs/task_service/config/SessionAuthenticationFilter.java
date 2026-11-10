package com.github.ggarabs.task_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private static final Set<String> ROLES = Set.of(
            "CANDIDATE", "CONTRACTOR", "INTERMEDIARY", "ADMINISTRATOR");

    private final RestClient userService;

    public SessionAuthenticationFilter(
            RestClient.Builder restClientBuilder,
            @Value("${services.user.url:http://localhost:8080}") String userServiceUrl) {
        this.userService = restClientBuilder.baseUrl(userServiceUrl).build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) return false;
        String path = request.getRequestURI();
        return path.equals("/tasks") || path.startsWith("/tasks/") || path.startsWith("/duvidas/task/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String sessionId = findSessionId(request.getCookies());
        if (sessionId != null) {
            try {
                SessionUser user = userService.get()
                        .uri("/api/auth/me")
                        .cookie("JSESSIONID", sessionId)
                        .retrieve()
                        .body(SessionUser.class);
                if (user != null && user.id() != null && user.email() != null && ROLES.contains(user.role())) {
                    var authentication = UsernamePasswordAuthenticationToken.authenticated(
                            user.id(), null, Set.of(new SimpleGrantedAuthority("ROLE_" + user.role())));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (RestClientException ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String findSessionId(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("JSESSIONID".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    public record SessionUser(String id, String email, String role, String message) { }
}
