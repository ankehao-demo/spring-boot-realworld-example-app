package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(
    classes = {
      DgsAutoConfiguration.class,
      MeDatafetcher.class,
      ProfileDatafetcher.class,
      ArticleDatafetcher.class,
      CommentDatafetcher.class
    })
public class MeDatafetcherTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private UserQueryService userQueryService;
  @MockBean private JwtService jwtService;
  @MockBean private UserRepository userRepository;
  @MockBean private io.spring.application.ArticleQueryService articleQueryService;
  @MockBean private io.spring.application.CommentQueryService commentQueryService;
  @MockBean private io.spring.application.ProfileQueryService profileQueryService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                user, null, java.util.Collections.emptyList()));
  }

  @Test
  void testMe() {
    UserData userData =
        new UserData(user.getId(), "test@test.com", "testuser", "bio", "image");
    when(userQueryService.findById(eq(user.getId()))).thenReturn(Optional.of(userData));

    String query = "{ me { email username } }";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Token test-token");

    String email =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.me.email", headers);
    org.assertj.core.api.Assertions.assertThat(email).isEqualTo("test@test.com");
  }
}
