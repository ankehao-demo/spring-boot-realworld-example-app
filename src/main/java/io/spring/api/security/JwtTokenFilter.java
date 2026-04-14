package io.spring.api.security;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class JwtTokenFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

  @Autowired private UserRepository userRepository;
  @Autowired private JwtService jwtService;
  private final String header = "Authorization";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Optional<String> tokenOpt = getTokenString(request.getHeader(header));
    if (tokenOpt.isPresent()) {
      String token = tokenOpt.get();
      Optional<String> subOpt = jwtService.getSubFromToken(token);
      if (subOpt.isPresent()) {
        String id = subOpt.get();
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
          userRepository
              .findById(id)
              .ifPresent(
                  user -> {
                    if (isTokenRevokedByPasswordChange(token, user)) {
                      log.warn("Token rejected: issued before password change for user={}", user.getId());
                      return;
                    }
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                  });
        }
      } else {
        log.warn("Failed token validation from IP={}", request.getRemoteAddr());
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean isTokenRevokedByPasswordChange(String token, User user) {
    if (user.getPasswordChangedAt() == 0) {
      return false;
    }
    Optional<Long> issuedAtOpt = jwtService.getIssuedAtFromToken(token);
    if (issuedAtOpt.isPresent()) {
      long issuedAt = issuedAtOpt.get();
      return issuedAt < user.getPasswordChangedAt();
    }
    return false;
  }

  private Optional<String> getTokenString(String header) {
    if (header == null) {
      return Optional.empty();
    } else {
      String[] split = header.split(" ");
      if (split.length < 2) {
        return Optional.empty();
      } else {
        return Optional.ofNullable(split[1]);
      }
    }
  }
}
