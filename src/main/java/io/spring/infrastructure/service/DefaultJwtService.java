package io.spring.infrastructure.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DefaultJwtService implements JwtService {
  private final SecretKey signingKey;
  private int sessionTime;

  @Autowired
  public DefaultJwtService(
      @Value("${jwt.secret}") String secret, @Value("${jwt.sessionTime}") int sessionTime) {
    this.sessionTime = sessionTime;
    byte[] keyBytes = Base64.getDecoder().decode(secret);
    this.signingKey = new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA512");
  }

  @Override
  public String toToken(User user) {
    return Jwts.builder()
        .subject(user.getId())
        .issuedAt(new Date())
        .issuer("realworld-app")
        .audience().add("realworld-api").and()
        .expiration(expireTimeFromNow())
        .signWith(signingKey)
        .compact();
  }

  @Override
  public Optional<String> getSubFromToken(String token) {
    try {
      Jws<Claims> claimsJws =
          Jwts.parser()
              .verifyWith(signingKey)
              .requireIssuer("realworld-app")
              .requireAudience("realworld-api")
              .build()
              .parseSignedClaims(token);
      return Optional.ofNullable(claimsJws.getPayload().getSubject());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Long> getIssuedAtFromToken(String token) {
    try {
      Jws<Claims> claimsJws =
          Jwts.parser()
              .verifyWith(signingKey)
              .requireIssuer("realworld-app")
              .requireAudience("realworld-api")
              .build()
              .parseSignedClaims(token);
      Date issuedAt = claimsJws.getPayload().getIssuedAt();
      return Optional.ofNullable(issuedAt).map(Date::getTime);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Date expireTimeFromNow() {
    return new Date(System.currentTimeMillis() + sessionTime * 1000L);
  }
}
