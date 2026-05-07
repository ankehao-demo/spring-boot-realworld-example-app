package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.graphql.types.Article;
import io.spring.graphql.types.Comment;
import io.spring.graphql.types.CommentsConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
class CommentDatafetcherTest {

  @Mock private CommentQueryService commentQueryService;

  private CommentDatafetcher commentDatafetcher;
  private User user;

  @BeforeEach
  void setUp() {
    commentDatafetcher = new CommentDatafetcher(commentQueryService);
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
  void should_get_comment_from_payload() {
    DateTime now = new DateTime();
    ProfileData profileData = new ProfileData("uid", "author", "bio", "img", false);
    CommentData commentData = new CommentData("c1", "comment body", "a1", now, now, profileData);

    DataFetchingEnvironment mockDfe = mock(DataFetchingEnvironment.class);
    when(mockDfe.getLocalContext()).thenReturn(commentData);

    DataFetcherResult<Comment> result = commentDatafetcher.getComment(dfe(mockDfe));

    assertNotNull(result);
    assertEquals("c1", result.getData().getId());
    assertEquals("comment body", result.getData().getBody());
  }

  @Test
  void should_get_article_comments_with_first() {
    setAuthenticated(user);

    DateTime now = new DateTime();
    ProfileData profileData = new ProfileData("uid", "author", "bio", "img", false);
    CommentData commentData = new CommentData("c1", "body", "a1", now, now, profileData);

    ArticleData articleData =
        new ArticleData(
            "a1", "slug", "title", "desc", "body", false, 0, now, now, null, profileData);
    Map<String, ArticleData> map = new HashMap<>();
    map.put("slug", articleData);

    Article article = Article.newBuilder().slug("slug").build();

    DataFetchingEnvironment mockDfe = mock(DataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(article);
    when(mockDfe.getLocalContext()).thenReturn(map);

    CursorPager<CommentData> cursorPager =
        new CursorPager<>(Arrays.asList(commentData), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(
            eq("a1"), eq(user), any(CursorPageParameter.class)))
        .thenReturn(cursorPager);

    DataFetcherResult<CommentsConnection> result =
        commentDatafetcher.articleComments(10, null, null, null, dfe(mockDfe));

    assertNotNull(result);
    assertEquals(1, result.getData().getEdges().size());
    assertEquals("c1", result.getData().getEdges().get(0).getNode().getId());
  }

  @Test
  void should_get_article_comments_with_last() {
    setAuthenticated(user);

    DateTime now = new DateTime();
    ProfileData profileData = new ProfileData("uid", "author", "bio", "img", false);
    CommentData commentData = new CommentData("c1", "body", "a1", now, now, profileData);

    ArticleData articleData =
        new ArticleData(
            "a1", "slug", "title", "desc", "body", false, 0, now, now, null, profileData);
    Map<String, ArticleData> map = new HashMap<>();
    map.put("slug", articleData);

    Article article = Article.newBuilder().slug("slug").build();

    DataFetchingEnvironment mockDfe = mock(DataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(article);
    when(mockDfe.getLocalContext()).thenReturn(map);

    CursorPager<CommentData> cursorPager =
        new CursorPager<>(Arrays.asList(commentData), Direction.PREV, false);
    when(commentQueryService.findByArticleIdWithCursor(
            eq("a1"), eq(user), any(CursorPageParameter.class)))
        .thenReturn(cursorPager);

    DataFetcherResult<CommentsConnection> result =
        commentDatafetcher.articleComments(null, null, 5, null, dfe(mockDfe));

    assertNotNull(result);
    assertEquals(1, result.getData().getEdges().size());
  }

  @Test
  void should_throw_when_both_first_and_last_null_for_comments() {
    assertThrows(
        IllegalArgumentException.class,
        () -> commentDatafetcher.articleComments(null, null, null, null, dfe()));
  }

  @Test
  void should_get_empty_comments() {
    setAuthenticated(user);

    DateTime now = new DateTime();
    ProfileData profileData = new ProfileData("uid", "author", "bio", "img", false);
    ArticleData articleData =
        new ArticleData(
            "a1", "slug", "title", "desc", "body", false, 0, now, now, null, profileData);
    Map<String, ArticleData> map = new HashMap<>();
    map.put("slug", articleData);

    Article article = Article.newBuilder().slug("slug").build();

    DataFetchingEnvironment mockDfe = mock(DataFetchingEnvironment.class);
    when(mockDfe.getSource()).thenReturn(article);
    when(mockDfe.getLocalContext()).thenReturn(map);

    CursorPager<CommentData> cursorPager =
        new CursorPager<>(Collections.emptyList(), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(
            eq("a1"), eq(user), any(CursorPageParameter.class)))
        .thenReturn(cursorPager);

    DataFetcherResult<CommentsConnection> result =
        commentDatafetcher.articleComments(10, null, null, null, dfe(mockDfe));

    assertNotNull(result);
    assertTrue(result.getData().getEdges().isEmpty());
  }

  private void setAuthenticated(User user) {
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
