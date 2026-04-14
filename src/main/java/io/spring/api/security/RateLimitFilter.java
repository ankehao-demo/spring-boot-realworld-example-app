package io.spring.api.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class RateLimitFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    if ("/users/login".equals(path) && "POST".equalsIgnoreCase(request.getMethod())) {
      String ip = request.getRemoteAddr();
      Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());
      if (bucket.tryConsume(1)) {
        filterChain.doFilter(request, response);
      } else {
        log.warn("Rate limit exceeded for IP={} on {}", ip, path);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write("{\"errors\":{\"body\":[\"Too many requests. Try again later.\"]}}");
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private Bucket createBucket() {
    Bandwidth limit = Bandwidth.builder()
        .capacity(10)
        .refillGreedy(10, Duration.ofMinutes(1))
        .build();
    return Bucket.builder().addLimit(limit).build();
  }
}
