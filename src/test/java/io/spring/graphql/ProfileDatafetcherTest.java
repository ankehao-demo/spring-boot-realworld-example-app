package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(
    classes = {
      DgsAutoConfiguration.class,
      ProfileDatafetcher.class,
      ArticleDatafetcher.class,
      CommentDatafetcher.class,
      MeDatafetcher.class
    })
public class ProfileDatafetcherTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private ProfileQueryService profileQueryService;
  @MockBean private UserRepository userRepository;
  @MockBean private io.spring.application.ArticleQueryService articleQueryService;
  @MockBean private io.spring.application.CommentQueryService commentQueryService;
  @MockBean private io.spring.application.UserQueryService userQueryService;
  @MockBean private io.spring.core.service.JwtService jwtService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    SecurityContextHolder.getContext()
        .setAuthentication(
            new AnonymousAuthenticationToken(
                "key",
                "anonymous",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
  }

  @Test
  void testQueryProfile() {
    ProfileData profileData =
        new ProfileData(user.getId(), "testuser", "bio", "image", false);
    when(profileQueryService.findByUsername(eq("testuser"), any()))
        .thenReturn(Optional.of(profileData));

    String query = "{ profile(username: \"testuser\") { profile { username bio image following } } }";

    String username =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.profile.profile.username");
    org.assertj.core.api.Assertions.assertThat(username).isEqualTo("testuser");
  }

  @Test
  void testQueryProfileFollowing() {
    ProfileData profileData =
        new ProfileData(user.getId(), "testuser", "bio", "image", true);
    when(profileQueryService.findByUsername(eq("testuser"), any()))
        .thenReturn(Optional.of(profileData));

    String query = "{ profile(username: \"testuser\") { profile { username following } } }";

    Boolean following =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.profile.profile.following");
    org.assertj.core.api.Assertions.assertThat(following).isTrue();
  }
}
