package io.spring.api.security;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtTokenFilterTest {

  @InjectMocks private JwtTokenFilter jwtTokenFilter;

  @Mock private JwtService jwtService;
  @Mock private UserRepository userRepository;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  private User user;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    user = new User("test@test.com", "testuser", "password", "bio", "image");
  }

  @Test
  void testValidTokenSetsSecurityContext() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Token valid-token");
    when(jwtService.getSubFromToken(eq("valid-token"))).thenReturn(Optional.of(user.getId()));
    when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    org.assertj.core.api.Assertions.assertThat(
            SecurityContextHolder.getContext().getAuthentication())
        .isNotNull();
    org.assertj.core.api.Assertions.assertThat(
            SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .isEqualTo(user);
  }

  @Test
  void testMissingAuthorizationHeader() throws Exception {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    org.assertj.core.api.Assertions.assertThat(
            SecurityContextHolder.getContext().getAuthentication())
        .isNull();
  }

  @Test
  void testMalformedToken() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    org.assertj.core.api.Assertions.assertThat(
            SecurityContextHolder.getContext().getAuthentication())
        .isNull();
  }

  @Test
  void testTokenFormatExtraction() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Token my-jwt-token");
    when(jwtService.getSubFromToken(eq("my-jwt-token"))).thenReturn(Optional.of(user.getId()));
    when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    org.assertj.core.api.Assertions.assertThat(
            SecurityContextHolder.getContext().getAuthentication())
        .isNotNull();
  }
}
