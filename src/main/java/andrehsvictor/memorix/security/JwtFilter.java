package andrehsvictor.memorix.security;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import andrehsvictor.memorix.token.TokenBlacklistService;
import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenBlacklistService tokenBlacklistService;
    private final JwtService jwtService;
    private final UserService userService;

    private final static String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(BEARER_PREFIX.length());
            Jwt jwt = jwtService.decode(token);
            if (tokenBlacklistService.exists(jwt.getId()) || !jwt.getClaim("type").equals("access")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().close();
                return;
            }
            setAuthentication(jwt);
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(Jwt jwt) {
        User user = userService.findById(UUID.fromString(jwt.getSubject()));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
                Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
