package Duoc.Huerto.security;

import Duoc.Huerto.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

public class BearerTokenFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    public BearerTokenFilter(TokenService tokenService) { this.tokenService = tokenService; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7).trim();
            tokenService.validar(token).ifPresent(usuario -> {
                var auth = new UsernamePasswordAuthenticationToken(
                    usuario.getCorreo(), null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getTipoCuenta().name()))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }
        chain.doFilter(request, response);
    }
}
