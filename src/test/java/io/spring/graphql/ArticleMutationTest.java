package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.exceptions.QueryException;
import io.spring.TestHelper;
import io.spring.application.data.ArticleData;
import io.spring.core.article.Article;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ArticleMutationTest extends GraphQLTestBase {

  private User defaultUser() {
    return new User("user@email.com", "username", "password", "bio", "image");
  }

  @Test
  public void should_create_article_success() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    Article article = new Article("Test Title", "desc", "body", Arrays.asList("java"), user.getId());
    when(articleCommandService.createArticle(any(), any())).thenReturn(article);

    ArticleData articleData = TestHelper.getArticleDataFromArticleAndUser(article, user);
    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    String query =
        "mutation { createArticle(input: {title: \"Test Title\", description: \"desc\", body: \"body\", tagList: [\"java\"]}) { article { title slug body description } } }";

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.createArticle.article", LinkedHashMap.class);

    assertNotNull(result);
    verify(articleCommandService).createArticle(any(), eq(user));
  }

  @Test
  public void should_fail_create_article_when_not_authenticated() {
    String query =
        "mutation { createArticle(input: {title: \"Test\", description: \"desc\", body: \"body\"}) { article { title } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.createArticle"));
  }

  @Test
  public void should_update_article_success() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    Article updatedArticle =
        new Article("New Title", "new desc", "new body", Arrays.asList(), user.getId());
    when(articleCommandService.updateArticle(any(), any())).thenReturn(updatedArticle);

    ArticleData articleData = TestHelper.getArticleDataFromArticleAndUser(updatedArticle, user);
    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    String query =
        String.format(
            "mutation { updateArticle(slug: \"%s\", changes: {title: \"New Title\"}) { article { title } } }",
            article.getSlug());

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.updateArticle.article", LinkedHashMap.class);

    assertNotNull(result);
    verify(articleCommandService).updateArticle(any(), any());
  }

  @Test
  public void should_fail_update_article_when_not_author() {
    User user = defaultUser();
    User otherUser = new User("other@email.com", "other", "pass", "", "");
    setAuthenticatedUser(otherUser);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    String query =
        String.format(
            "mutation { updateArticle(slug: \"%s\", changes: {title: \"New\"}) { article { title } } }",
            article.getSlug());

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.updateArticle"));
  }

  @Test
  public void should_favorite_article_success() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    ArticleData articleData = TestHelper.getArticleDataFromArticleAndUser(article, user);
    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    String query =
        String.format(
            "mutation { favoriteArticle(slug: \"%s\") { article { title } } }",
            article.getSlug());

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.favoriteArticle.article", LinkedHashMap.class);

    assertNotNull(result);
    verify(articleFavoriteRepository).save(any(ArticleFavorite.class));
  }

  @Test
  public void should_unfavorite_article_success() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    ArticleFavorite fav = new ArticleFavorite(article.getId(), user.getId());
    when(articleFavoriteRepository.find(eq(article.getId()), eq(user.getId())))
        .thenReturn(Optional.of(fav));

    ArticleData articleData = TestHelper.getArticleDataFromArticleAndUser(article, user);
    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    String query =
        String.format(
            "mutation { unfavoriteArticle(slug: \"%s\") { article { title } } }",
            article.getSlug());

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.unfavoriteArticle.article", LinkedHashMap.class);

    assertNotNull(result);
    verify(articleFavoriteRepository).remove(any(ArticleFavorite.class));
  }

  @Test
  public void should_delete_article_success() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    String query =
        String.format(
            "mutation { deleteArticle(slug: \"%s\") { success } }", article.getSlug());

    Boolean success =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.deleteArticle.success");

    assertTrue(success);
    verify(articleRepository).remove(article);
  }

  @Test
  public void should_fail_delete_article_when_not_author() {
    User user = defaultUser();
    User otherUser = new User("other@email.com", "other", "pass", "", "");
    setAuthenticatedUser(otherUser);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    String query =
        String.format(
            "mutation { deleteArticle(slug: \"%s\") { success } }", article.getSlug());

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.deleteArticle"));
  }

  @Test
  public void should_fail_delete_article_when_not_found() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    when(articleRepository.findBySlug(eq("not-found"))).thenReturn(Optional.empty());

    String query = "mutation { deleteArticle(slug: \"not-found\") { success } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.deleteArticle"));
  }
}
