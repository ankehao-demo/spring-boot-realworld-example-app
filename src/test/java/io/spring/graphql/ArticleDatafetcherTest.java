package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.TestHelper;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.ArticlesConnection;
import io.spring.graphql.types.Profile;
import java.util.Arrays;
import java.util.Collections;
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
class ArticleDatafetcherTest {

  @Mock private ArticleQueryService articleQueryService;
  @Mock private UserRepository userRepository;

  private ArticleDatafetcher articleDatafetcher;
  private User user;

  @BeforeEach
  void setUp() {
    articleDatafetcher = new ArticleDatafetcher(articleQueryService, userRepository);
    user = new User("test@example.com", "testuser", "password", "bio", "image");
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private DgsDataFetchingEnvironment dfe() {
    return new DgsDataFetchingEnvironment(mock(DataFetchingEnvironment.class));
  }

  private DgsDataFetchingEnvironment dfe(DataFetchingEnvironment delegate) {
    return new DgsDataFetchingEnvironment(delegate);
  }

  @Test
  void should_get_feed_with_first_parameter() {
    setAuthenticated(user);
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findUserFeedWithCursor(eq(user), any(CursorPageParameter.class)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.getFeed(10, null, null, null, dfe());

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(1, result.getData().getEdges().size());
  }

  @Test
  void should_get_feed_with_last_parameter() {
    setAuthenticated(user);
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.PREV, false);
    when(articleQueryService.findUserFeedWithCursor(eq(user), any(CursorPageParameter.class)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.getFeed(null, null, 10, null, dfe());

    assertNotNull(result);
    assertEquals(1, result.getData().getEdges().size());
  }

  @Test
  void should_throw_when_both_first_and_last_null_for_feed() {
    assertThrows(
        IllegalArgumentException.class,
        () -> articleDatafetcher.getFeed(null, null, null, null, dfe()));
  }

  @Test
  void should_get_feed_with_empty_results() {
    setAuthenticated(user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Collections.emptyList(), Direction.NEXT, false);
    when(articleQueryService.findUserFeedWithCursor(eq(user), any(CursorPageParameter.class)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.getFeed(10, null, null, null, dfe());

    assertNotNull(result);
    assertTrue(result.getData().getEdges().isEmpty());
  }

  @Test
  void should_get_user_feed_from_profile() {
    DataFetchingEnvironment mockDfe = mock(DataFetchingEnvironment.class);
    Profile profile = Profile.newBuilder().username("testuser").build();
    when(mockDfe.getSource()).thenReturn(profile);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findUserFeedWithCursor(eq(user), any(CursorPageParameter.class)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.userFeed(10, null, null, null, dfe(mockDfe));

    assertNotNull(result);
    assertEquals(1, result.getData().getEdges().size());
  }

  @Test
  void should_throw_when_user_not_found_for_user_feed() {
    DataFetchingEnvironment mockDfe = mock(DataFetchingEnvironment.class);
    Profile profile = Profile.newBuilder().username("nonexistent").build();
    when(mockDfe.getSource()).thenReturn(profile);
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> articleDatafetcher.userFeed(10, null, null, null, dfe(mockDfe)));
  }

  @Test
  void should_throw_when_both_first_and_last_null_for_user_feed() {
    assertThrows(
        IllegalArgumentException.class,
        () -> articleDatafetcher.userFeed(null, null, null, null, dfe()));
  }

  @Test
  void should_get_user_favorites() {
    setAuthenticated(user);
    DataFetchingEnvironment mockDfe = mock(DataFetchingEnvironment.class);
    Profile profile = Profile.newBuilder().username("testuser").build();
    when(mockDfe.getSource()).thenReturn(profile);

    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(
            isNull(), isNull(), eq("testuser"), any(CursorPageParameter.class), eq(user)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.userFavorites(10, null, null, null, dfe(mockDfe));

    assertNotNull(result);
    assertEquals(1, result.getData().getEdges().size());
  }

  @Test
  void should_throw_when_both_first_and_last_null_for_user_favorites() {
    assertThrows(
        IllegalArgumentException.class,
        () -> articleDatafetcher.userFavorites(null, null, null, null, dfe()));
  }

  @Test
  void should_get_user_articles() {
    setAuthenticated(user);
    DataFetchingEnvironment mockDfe = mock(DataFetchingEnvironment.class);
    Profile profile = Profile.newBuilder().username("testuser").build();
    when(mockDfe.getSource()).thenReturn(profile);

    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(
            isNull(), eq("testuser"), isNull(), any(CursorPageParameter.class), eq(user)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.userArticles(10, null, null, null, dfe(mockDfe));

    assertNotNull(result);
    assertEquals(1, result.getData().getEdges().size());
  }

  @Test
  void should_throw_when_both_first_and_last_null_for_user_articles() {
    assertThrows(
        IllegalArgumentException.class,
        () -> articleDatafetcher.userArticles(null, null, null, null, dfe()));
  }

  @Test
  void should_get_articles_with_filters() {
    setAuthenticated(user);
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(
            eq("java"), eq("testuser"), isNull(), any(CursorPageParameter.class), eq(user)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.getArticles(10, null, null, null, "testuser", null, "java", dfe());

    assertNotNull(result);
    assertEquals(1, result.getData().getEdges().size());
  }

  @Test
  void should_throw_when_both_first_and_last_null_for_articles() {
    assertThrows(
        IllegalArgumentException.class,
        () -> articleDatafetcher.getArticles(null, null, null, null, null, null, null, dfe()));
  }

  @Test
  void should_get_article_from_article_payload() {
    setAuthenticated(user);
    io.spring.core.article.Article article =
        new io.spring.core.article.Article("title", "desc", "body", Collections.emptyList(), user.getId());
    DataFetchingEnvironment rawDfe = mock(DataFetchingEnvironment.class);
    when(rawDfe.getLocalContext()).thenReturn(article);

    ArticleData articleData = TestHelper.getArticleDataFromArticleAndUser(article, user);
    when(articleQueryService.findById(article.getId(), user))
        .thenReturn(Optional.of(articleData));

    DataFetcherResult<Article> result = articleDatafetcher.getArticle(rawDfe);

    assertNotNull(result);
    assertEquals(articleData.getSlug(), result.getData().getSlug());
    assertEquals(articleData.getBody(), result.getData().getBody());
  }

  @Test
  void should_throw_when_article_not_found_in_article_payload() {
    setAuthenticated(user);
    io.spring.core.article.Article article =
        new io.spring.core.article.Article("title", "desc", "body", Collections.emptyList(), user.getId());
    DataFetchingEnvironment rawDfe = mock(DataFetchingEnvironment.class);
    when(rawDfe.getLocalContext()).thenReturn(article);
    when(articleQueryService.findById(article.getId(), user)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> articleDatafetcher.getArticle(rawDfe));
  }

  @Test
  void should_get_comment_article() {
    setAuthenticated(user);
    DateTime now = new DateTime();
    ProfileData profileData = new ProfileData("uid", "author", "bio", "img", false);
    CommentData commentData = new CommentData("c1", "comment body", "a1", now, now, profileData);

    DataFetchingEnvironment rawDfe = mock(DataFetchingEnvironment.class);
    when(rawDfe.getLocalContext()).thenReturn(commentData);

    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    when(articleQueryService.findById("a1", user)).thenReturn(Optional.of(articleData));

    DataFetcherResult<Article> result = articleDatafetcher.getCommentArticle(rawDfe);

    assertNotNull(result);
    assertNotNull(result.getData());
  }

  @Test
  void should_find_article_by_slug() {
    setAuthenticated(user);
    io.spring.core.article.Article article =
        new io.spring.core.article.Article("My Title", "desc", "body", Collections.emptyList(), user.getId());
    ArticleData articleData = TestHelper.getArticleDataFromArticleAndUser(article, user);
    when(articleQueryService.findBySlug(article.getSlug(), user))
        .thenReturn(Optional.of(articleData));

    DataFetcherResult<Article> result =
        articleDatafetcher.findArticleBySlug(article.getSlug());

    assertNotNull(result);
    assertEquals(article.getSlug(), result.getData().getSlug());
    assertEquals(article.getTitle(), result.getData().getTitle());
  }

  @Test
  void should_throw_when_article_not_found_by_slug() {
    setAuthenticated(user);
    when(articleQueryService.findBySlug("nonexistent", user)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> articleDatafetcher.findArticleBySlug("nonexistent"));
  }

  @Test
  void should_get_articles_with_last_parameter() {
    setAuthenticated(user);
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.PREV, false);
    when(articleQueryService.findRecentArticlesWithCursor(
            isNull(), isNull(), isNull(), any(CursorPageParameter.class), eq(user)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.getArticles(null, null, 5, null, null, null, null, dfe());

    assertNotNull(result);
    assertEquals(1, result.getData().getEdges().size());
  }

  @Test
  void should_set_page_info_with_has_next() {
    setAuthenticated(user);
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, true);
    when(articleQueryService.findUserFeedWithCursor(eq(user), any(CursorPageParameter.class)))
        .thenReturn(cursorPager);

    DataFetcherResult<ArticlesConnection> result =
        articleDatafetcher.getFeed(10, null, null, null, dfe());

    assertNotNull(result);
    assertTrue(result.getData().getPageInfo().isHasNextPage());
    assertFalse(result.getData().getPageInfo().isHasPreviousPage());
  }

  private void setAuthenticated(User user) {
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
