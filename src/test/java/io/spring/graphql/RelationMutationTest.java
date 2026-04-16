package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
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

@SpringBootTest(
    classes = {
      DgsAutoConfiguration.class,
      RelationMutation.class,
      ProfileDatafetcher.class,
      ArticleDatafetcher.class,
      CommentDatafetcher.class,
      MeDatafetcher.class
    })
public class RelationMutationTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private UserRepository userRepository;
  @MockBean private ProfileQueryService profileQueryService;
  @MockBean private io.spring.application.ArticleQueryService articleQueryService;
  @MockBean private io.spring.application.CommentQueryService commentQueryService;
  @MockBean private io.spring.application.UserQueryService userQueryService;
  @MockBean private io.spring.core.service.JwtService jwtService;

  private User user;
  private User target;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    target = new User("target@test.com", "targetuser", "password", "bio2", "image2");

    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                user, null, java.util.Collections.emptyList()));
  }

  @Test
  void testFollowUser() {
    when(userRepository.findByUsername(eq("targetuser"))).thenReturn(Optional.of(target));
    ProfileData profileData =
        new ProfileData(target.getId(), "targetuser", "bio2", "image2", true);
    when(profileQueryService.findByUsername(eq("targetuser"), any()))
        .thenReturn(Optional.of(profileData));

    String mutation =
        "mutation { followUser(username: \"targetuser\") { profile { username following } } }";

    String username =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.followUser.profile.username");
    org.assertj.core.api.Assertions.assertThat(username).isEqualTo("targetuser");
  }

  @Test
  void testUnfollowUser() {
    when(userRepository.findByUsername(eq("targetuser"))).thenReturn(Optional.of(target));
    FollowRelation relation = new FollowRelation(user.getId(), target.getId());
    when(userRepository.findRelation(eq(user.getId()), eq(target.getId())))
        .thenReturn(Optional.of(relation));
    ProfileData profileData =
        new ProfileData(target.getId(), "targetuser", "bio2", "image2", false);
    when(profileQueryService.findByUsername(eq("targetuser"), any()))
        .thenReturn(Optional.of(profileData));

    String mutation =
        "mutation { unfollowUser(username: \"targetuser\") { profile { username following } } }";

    String username =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.unfollowUser.profile.username");
    org.assertj.core.api.Assertions.assertThat(username).isEqualTo("targetuser");
  }
}
