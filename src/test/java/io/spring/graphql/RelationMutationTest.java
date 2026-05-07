package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.ProfilePayload;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class RelationMutationTest {

  @Mock private UserRepository userRepository;
  @Mock private ProfileQueryService profileQueryService;

  private RelationMutation relationMutation;
  private User user;

  @BeforeEach
  void setUp() {
    relationMutation = new RelationMutation(userRepository, profileQueryService);
    user = new User("test@example.com", "testuser", "password", "bio", "image");
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void should_follow_user_successfully() {
    setAuthenticated(user);
    User target = new User("target@example.com", "target", "password", "bio", "img");
    when(userRepository.findByUsername("target")).thenReturn(Optional.of(target));

    ProfileData profileData = new ProfileData(target.getId(), "target", "bio", "img", true);
    when(profileQueryService.findByUsername("target", user))
        .thenReturn(Optional.of(profileData));

    ProfilePayload result = relationMutation.follow("target");

    assertNotNull(result);
    assertNotNull(result.getProfile());
    assertEquals("target", result.getProfile().getUsername());
    assertTrue(result.getProfile().getFollowing());
    verify(userRepository).saveRelation(any(FollowRelation.class));
  }

  @Test
  void should_throw_authentication_exception_when_follow_unauthenticated() {
    TestingAuthenticationToken auth = new TestingAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertThrows(AuthenticationException.class, () -> relationMutation.follow("target"));
  }

  @Test
  void should_throw_not_found_when_target_user_not_found_on_follow() {
    setAuthenticated(user);
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> relationMutation.follow("nonexistent"));
  }

  @Test
  void should_unfollow_user_successfully() {
    setAuthenticated(user);
    User target = new User("target@example.com", "target", "password", "bio", "img");
    when(userRepository.findByUsername("target")).thenReturn(Optional.of(target));

    FollowRelation relation = new FollowRelation(user.getId(), target.getId());
    when(userRepository.findRelation(user.getId(), target.getId()))
        .thenReturn(Optional.of(relation));

    ProfileData profileData = new ProfileData(target.getId(), "target", "bio", "img", false);
    when(profileQueryService.findByUsername("target", user))
        .thenReturn(Optional.of(profileData));

    ProfilePayload result = relationMutation.unfollow("target");

    assertNotNull(result);
    assertNotNull(result.getProfile());
    assertEquals("target", result.getProfile().getUsername());
    assertFalse(result.getProfile().getFollowing());
    verify(userRepository).removeRelation(relation);
  }

  @Test
  void should_throw_authentication_exception_when_unfollow_unauthenticated() {
    TestingAuthenticationToken auth = new TestingAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertThrows(AuthenticationException.class, () -> relationMutation.unfollow("target"));
  }

  @Test
  void should_throw_not_found_when_target_user_not_found_on_unfollow() {
    setAuthenticated(user);
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> relationMutation.unfollow("nonexistent"));
  }

  @Test
  void should_throw_not_found_when_no_follow_relation_exists_on_unfollow() {
    setAuthenticated(user);
    User target = new User("target@example.com", "target", "password", "bio", "img");
    when(userRepository.findByUsername("target")).thenReturn(Optional.of(target));
    when(userRepository.findRelation(user.getId(), target.getId()))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> relationMutation.unfollow("target"));
  }

  private void setAuthenticated(User user) {
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
