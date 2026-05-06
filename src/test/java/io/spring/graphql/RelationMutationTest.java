package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.exceptions.QueryException;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class RelationMutationTest extends GraphQLTestBase {

  private User defaultUser() {
    return new User("user@email.com", "username", "password", "bio", "image");
  }

  @Test
  public void should_follow_user_success() {
    User user = defaultUser();
    User target = new User("target@email.com", "target", "pass", "target bio", "target image");
    setAuthenticatedUser(user);

    when(userRepository.findByUsername(eq("target"))).thenReturn(Optional.of(target));

    ProfileData profileData =
        new ProfileData(target.getId(), "target", "target bio", "target image", true);
    when(profileQueryService.findByUsername(eq("target"), eq(user)))
        .thenReturn(Optional.of(profileData));

    String query = "mutation { followUser(username: \"target\") { profile { username following } } }";

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.followUser.profile", LinkedHashMap.class);

    assertNotNull(result);
    assertEquals("target", result.get("username"));
    assertEquals(true, result.get("following"));
    verify(userRepository).saveRelation(any(FollowRelation.class));
  }

  @Test
  public void should_fail_follow_when_not_authenticated() {
    String query = "mutation { followUser(username: \"target\") { profile { username } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.followUser"));
  }

  @Test
  public void should_fail_follow_when_user_not_found() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    when(userRepository.findByUsername(eq("nonexistent"))).thenReturn(Optional.empty());

    String query =
        "mutation { followUser(username: \"nonexistent\") { profile { username } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.followUser"));
  }

  @Test
  public void should_unfollow_user_success() {
    User user = defaultUser();
    User target = new User("target@email.com", "target", "pass", "target bio", "target image");
    setAuthenticatedUser(user);

    when(userRepository.findByUsername(eq("target"))).thenReturn(Optional.of(target));

    FollowRelation relation = new FollowRelation(user.getId(), target.getId());
    when(userRepository.findRelation(eq(user.getId()), eq(target.getId())))
        .thenReturn(Optional.of(relation));

    ProfileData profileData =
        new ProfileData(target.getId(), "target", "target bio", "target image", false);
    when(profileQueryService.findByUsername(eq("target"), eq(user)))
        .thenReturn(Optional.of(profileData));

    String query =
        "mutation { unfollowUser(username: \"target\") { profile { username following } } }";

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.unfollowUser.profile", LinkedHashMap.class);

    assertNotNull(result);
    assertEquals("target", result.get("username"));
    assertEquals(false, result.get("following"));
    verify(userRepository).removeRelation(relation);
  }

  @Test
  public void should_fail_unfollow_when_not_authenticated() {
    String query = "mutation { unfollowUser(username: \"target\") { profile { username } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.unfollowUser"));
  }

  @Test
  public void should_fail_unfollow_when_relation_not_found() {
    User user = defaultUser();
    User target = new User("target@email.com", "target", "pass", "", "");
    setAuthenticatedUser(user);

    when(userRepository.findByUsername(eq("target"))).thenReturn(Optional.of(target));
    when(userRepository.findRelation(eq(user.getId()), eq(target.getId())))
        .thenReturn(Optional.empty());

    String query =
        "mutation { unfollowUser(username: \"target\") { profile { username } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.unfollowUser"));
  }
}
