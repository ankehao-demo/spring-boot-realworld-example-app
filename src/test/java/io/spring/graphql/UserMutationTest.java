package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.application.user.UserService;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(
    classes = {
      DgsAutoConfiguration.class,
      UserMutation.class,
      MeDatafetcher.class,
      ProfileDatafetcher.class,
      ArticleDatafetcher.class,
      CommentDatafetcher.class
    })
public class UserMutationTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private UserRepository userRepository;
  @MockBean private PasswordEncoder passwordEncoder;
  @MockBean private UserService userService;
  @MockBean private JwtService jwtService;
  @MockBean private io.spring.application.ArticleQueryService articleQueryService;
  @MockBean private io.spring.application.CommentQueryService commentQueryService;
  @MockBean private io.spring.application.ProfileQueryService profileQueryService;
  @MockBean private io.spring.application.UserQueryService userQueryService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
  }

  @Test
  void testCreateUser() {
    when(userService.createUser(any())).thenReturn(user);
    when(jwtService.toToken(any())).thenReturn("test-token");

    String mutation =
        "mutation { createUser(input: {email: \"test@test.com\", username: \"testuser\", password: \"password\"}) { ... on UserPayload { user { email username token } } } }";

    String email =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.createUser.user.email");
    org.assertj.core.api.Assertions.assertThat(email).isEqualTo("test@test.com");
  }

  @Test
  void testLogin() {
    when(userRepository.findByEmail(eq("test@test.com"))).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(eq("password"), eq(user.getPassword()))).thenReturn(true);
    when(jwtService.toToken(any())).thenReturn("test-token");

    String mutation =
        "mutation { login(email: \"test@test.com\", password: \"password\") { user { email username token } } }";

    String email =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.login.user.email");
    org.assertj.core.api.Assertions.assertThat(email).isEqualTo("test@test.com");
  }

  @Test
  void testUpdateUser() {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                user, null, java.util.Collections.emptyList()));
    when(jwtService.toToken(any())).thenReturn("test-token");

    String mutation =
        "mutation { updateUser(changes: {email: \"new@test.com\", bio: \"new bio\"}) { user { email username token } } }";

    String email =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.updateUser.user.email");
    org.assertj.core.api.Assertions.assertThat(email).isNotNull();
  }
}
