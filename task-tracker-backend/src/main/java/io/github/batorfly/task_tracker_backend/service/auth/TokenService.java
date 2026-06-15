package io.github.batorfly.task_tracker_backend.service.auth;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.exception.auth.CookiesNotFoundException;
import io.github.batorfly.task_tracker_backend.exception.auth.RefreshTokenNotFoundException;
import io.github.batorfly.task_tracker_backend.service.jwt.JwtService;
import io.github.batorfly.task_tracker_backend.web.dto.auth.TokenPair;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor @Slf4j
public class TokenService {
    private final JwtService jwtService;

    public TokenPair createTokenPair(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new TokenPair(accessToken, refreshToken);
    }

    public void createRefreshToken(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        Date expiration = jwtService.getExpirationFromToken(refreshToken);

        long maxAgeSeconds =
                (expiration.getTime() - System.currentTimeMillis()) / 1000;

        cookie.setMaxAge((int) Math.max(maxAgeSeconds, 0));
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    public void deleteRefreshToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies())
                .orElseThrow(() -> {
                    log.error("Cookies not found.");
                    return new CookiesNotFoundException("Cookies not found");
                });

        return Arrays.stream(cookies)
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RefreshTokenNotFoundException("No refresh token"));
    }
}
