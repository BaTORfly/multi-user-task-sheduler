package io.github.batorfly.task_tracker_backend.security.filter;

import io.github.batorfly.task_tracker_backend.Utils;
import io.github.batorfly.task_tracker_backend.service.jwt.JwtService;
import io.github.batorfly.task_tracker_backend.service.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor @Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            Utils.getTokenFromAuthHeader(request.getHeader("Authorization"))
                    .flatMap(jwtService::getSubjectFromToken)
                    .ifPresent(
                            email -> {
                                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                    userService
                                            .findByEmail(email)
                                            .ifPresent(user -> {
                                                UsernamePasswordAuthenticationToken authorization =
                                                        new UsernamePasswordAuthenticationToken(
                                                                user,
                                                                null,
                                                                user.getAuthorities()
                                                        );

                                                authorization.setDetails(
                                                        new WebAuthenticationDetailsSource().buildDetails(request)
                                                );

                                                SecurityContextHolder.getContext().setAuthentication(authorization);
                                                log.debug("User {} authenticated successfully", user.getEmail() +
                                                        user.getAuthorities());
                                            });
                                }
                            }
                    );
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException ex) {
            log.error("Exception {} occurred. Message: {}", ex.getClass().getName(), ex.getMessage());
            handleJwtException(response, ex);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/v1/auth");
    }

    private void handleJwtException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");

        Map<String, Object> body = Map.of(
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
        );

        String jsonResponse = objectMapper.writeValueAsString(body);

        response.getWriter().write(jsonResponse);

        log.error(
                "Exception {} handled. Response {} with status {} sent.",
                e.getClass().getName(),
                jsonResponse,
                HttpStatus.UNAUTHORIZED.value()
        );
    }
}
