package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.exceptions.QueryException;
import io.spring.TestHelper;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ArticleDatafetcherTest extends GraphQLTestBase {

  private User defaultUser() {
    return new User("user@email.com", "username", "password", "bio", "image");
  }

  @Test
  public void should_get_article_by_slug() {
    User user = defaultUser();
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    when(articleQueryService.findBySlug(eq("title-1"), any())).thenReturn(Optional.of(articleData));

    String query =
        "{ article(slug: \"title-1\") { title slug body description favorited favoritesCount } }";

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.article", LinkedHashMap.class);

    assertNotNull(result);
    assertEquals("title 1", result.get("title"));
    assertEquals("title-1", result.get("slug"));
    assertEquals("body 1", result.get("body"));
    assertEquals("desc 1", result.get("description"));
  }

  @Test
  public void should_throw_when_article_not_found_by_slug() {
    when(articleQueryService.findBySlug(eq("not-found"), any())).thenReturn(Optional.empty());

    String query = "{ article(slug: \"not-found\") { title } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.article"));
  }

  @Test
  public void should_get_articles_with_first_param() {
    User user = defaultUser();
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(any(), any(), any(), any(), any()))
        .thenReturn(cursorPager);

    String query =
        "{ articles(first: 10) { edges { cursor node { title slug } } pageInfo { hasNextPage hasPreviousPage } } }";

    List<LinkedHashMap<String, Object>> edges =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles.edges");

    assertNotNull(edges);
    assertEquals(1, edges.size());
  }

  @Test
  public void should_get_articles_filtered_by_tag() {
    User user = defaultUser();
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(
            eq("java"), any(), any(), any(), any()))
        .thenReturn(cursorPager);

    String query =
        "{ articles(first: 10, withTag: \"java\") { edges { node { title } } } }";

    List<LinkedHashMap<String, Object>> edges =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles.edges");

    assertNotNull(edges);
    assertEquals(1, edges.size());
    verify(articleQueryService)
        .findRecentArticlesWithCursor(eq("java"), any(), any(), any(), any());
  }

  @Test
  public void should_get_articles_filtered_by_author() {
    User user = defaultUser();
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(
            any(), eq("username"), any(), any(), any()))
        .thenReturn(cursorPager);

    String query =
        "{ articles(first: 10, authoredBy: \"username\") { edges { node { title } } } }";

    List<LinkedHashMap<String, Object>> edges =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles.edges");

    assertNotNull(edges);
    assertEquals(1, edges.size());
    verify(articleQueryService)
        .findRecentArticlesWithCursor(any(), eq("username"), any(), any(), any());
  }

  @Test
  public void should_get_articles_filtered_by_favorited() {
    User user = defaultUser();
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(
            any(), any(), eq("username"), any(), any()))
        .thenReturn(cursorPager);

    String query =
        "{ articles(first: 10, favoritedBy: \"username\") { edges { node { title } } } }";

    List<LinkedHashMap<String, Object>> edges =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles.edges");

    assertNotNull(edges);
    assertEquals(1, edges.size());
    verify(articleQueryService)
        .findRecentArticlesWithCursor(any(), any(), eq("username"), any(), any());
  }

  @Test
  public void should_throw_when_neither_first_nor_last_provided() {
    String query = "{ articles { edges { node { title } } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles"));
  }

  @Test
  public void should_get_feed() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findUserFeedWithCursor(any(), any())).thenReturn(cursorPager);

    String query =
        "{ feed(first: 10) { edges { cursor node { title slug } } pageInfo { hasNextPage hasPreviousPage } } }";

    List<LinkedHashMap<String, Object>> edges =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.feed.edges");

    assertNotNull(edges);
    assertEquals(1, edges.size());
  }

  @Test
  public void should_get_feed_with_last_param() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Collections.emptyList(), Direction.PREV, false);
    when(articleQueryService.findUserFeedWithCursor(any(), any())).thenReturn(cursorPager);

    String query =
        "{ feed(last: 10, before: \"1000000000000\") { edges { node { title } } pageInfo { hasNextPage hasPreviousPage } } }";

    List<LinkedHashMap<String, Object>> edges =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.feed.edges");

    assertNotNull(edges);
    assertEquals(0, edges.size());
  }
}
