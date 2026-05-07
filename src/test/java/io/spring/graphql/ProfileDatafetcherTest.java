package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.Comment;
import io.spring.graphql.types.Profile;
import io.spring.graphql.types.ProfilePayload;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ProfileDatafetcherTest {

  @Mock private ProfileQueryService profileQueryService;
  @Mock private DataFetchingEnvironment dataFetchingEnvironment;

  private ProfileDatafetcher profileDatafetcher;

  @BeforeEach
  void setUp() {
    profileDatafetcher = new ProfileDatafetcher(profileQueryService);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void should_get_user_profile() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    setAuthenticated(user);
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(user);

    ProfileData profileData = new ProfileData(user.getId(), "testuser", "bio", "image", false);
    when(profileQueryService.findByUsername("testuser", user)).thenReturn(Optional.of(profileData));

    Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    assertEquals("bio", result.getBio());
    assertEquals("image", result.getImage());
    assertFalse(result.getFollowing());
  }

  @Test
  void should_get_user_profile_with_authenticated_user() {
    User currentUser = new User("current@example.com", "current", "password", "", "");
    TestingAuthenticationToken authentication =
        new TestingAuthenticationToken(currentUser, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User targetUser = new User("target@example.com", "target", "password", "bio", "image");
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(targetUser);

    setAuthenticated(currentUser);
    ProfileData profileData =
        new ProfileData(targetUser.getId(), "target", "bio", "image", true);
    when(profileQueryService.findByUsername("target", currentUser))
        .thenReturn(Optional.of(profileData));

    Profile result = profileDatafetcher.getUserProfile(dataFetchingEnvironment);

    assertNotNull(result);
    assertEquals("target", result.getUsername());
    assertTrue(result.getFollowing());
  }

  @Test
  void should_get_article_author_profile() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    setAuthenticated(user);
    ArticleData articleData =
        new ArticleData(
            "id1", "test-slug", "title", "desc", "body", false, 0,
            new DateTime(), new DateTime(), null,
            new ProfileData("uid", "author", "bio", "image", false));

    Map<String, ArticleData> map = new HashMap<>();
    map.put("test-slug", articleData);

    Article article = Article.newBuilder().slug("test-slug").build();

    when(dataFetchingEnvironment.getLocalContext()).thenReturn(map);
    when(dataFetchingEnvironment.getSource()).thenReturn(article);

    ProfileData profileData = new ProfileData("uid", "author", "bio", "image", false);
    when(profileQueryService.findByUsername("author", user))
        .thenReturn(Optional.of(profileData));

    Profile result = profileDatafetcher.getAuthor(dataFetchingEnvironment);

    assertNotNull(result);
    assertEquals("author", result.getUsername());
  }

  @Test
  void should_get_comment_author_profile() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    setAuthenticated(user);
    ProfileData authorProfile = new ProfileData("uid", "commentauthor", "bio", "image", false);
    CommentData commentData =
        new CommentData("c1", "body", "a1", new DateTime(), new DateTime(), authorProfile);

    Map<String, CommentData> map = new HashMap<>();
    map.put("c1", commentData);

    Comment comment = Comment.newBuilder().id("c1").build();

    when(dataFetchingEnvironment.getSource()).thenReturn(comment);
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(map);

    ProfileData profileData =
        new ProfileData("uid", "commentauthor", "bio", "image", false);
    when(profileQueryService.findByUsername("commentauthor", user))
        .thenReturn(Optional.of(profileData));

    Profile result = profileDatafetcher.getCommentAuthor(dataFetchingEnvironment);

    assertNotNull(result);
    assertEquals("commentauthor", result.getUsername());
  }

  @Test
  void should_query_profile_by_username() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    setAuthenticated(user);
    ProfileData profileData = new ProfileData("uid", "testuser", "bio", "image", false);
    when(profileQueryService.findByUsername("testuser", user))
        .thenReturn(Optional.of(profileData));
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("testuser");

    ProfilePayload result =
        profileDatafetcher.queryProfile("testuser", dataFetchingEnvironment);

    assertNotNull(result);
    assertNotNull(result.getProfile());
    assertEquals("testuser", result.getProfile().getUsername());
  }

  @Test
  void should_throw_not_found_when_profile_does_not_exist() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    setAuthenticated(user);
    when(profileQueryService.findByUsername("nonexistent", user)).thenReturn(Optional.empty());
    when(dataFetchingEnvironment.getArgument("username")).thenReturn("nonexistent");

    assertThrows(
        ResourceNotFoundException.class,
        () -> profileDatafetcher.queryProfile("nonexistent", dataFetchingEnvironment));
  }

  private void setAuthenticated(User user) {
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
