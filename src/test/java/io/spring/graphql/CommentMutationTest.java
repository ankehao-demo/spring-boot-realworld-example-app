package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.exceptions.QueryException;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CommentMutationTest extends GraphQLTestBase {

  private User defaultUser() {
    return new User("user@email.com", "username", "password", "bio", "image");
  }

  @Test
  public void should_add_comment_success() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    ProfileData profileData =
        new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false);
    DateTime now = new DateTime();
    CommentData commentData =
        new CommentData("comment-id", "test comment", article.getId(), now, now, profileData);
    when(commentQueryService.findById(any(), any())).thenReturn(Optional.of(commentData));

    String query =
        String.format(
            "mutation { addComment(slug: \"%s\", body: \"test comment\") { comment { id body } } }",
            article.getSlug());

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.addComment.comment", LinkedHashMap.class);

    assertNotNull(result);
    assertEquals("comment-id", result.get("id"));
    assertEquals("test comment", result.get("body"));
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  public void should_fail_add_comment_when_not_authenticated() {
    String query = "mutation { addComment(slug: \"test\", body: \"comment\") { comment { id } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.addComment"));
  }

  @Test
  public void should_fail_add_comment_when_article_not_found() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    when(articleRepository.findBySlug(eq("not-found"))).thenReturn(Optional.empty());

    String query =
        "mutation { addComment(slug: \"not-found\", body: \"comment\") { comment { id } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.addComment"));
  }

  @Test
  public void should_delete_comment_success() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    Comment comment = new Comment("test comment", user.getId(), article.getId());
    when(commentRepository.findById(eq(article.getId()), eq(comment.getId())))
        .thenReturn(Optional.of(comment));

    String query =
        String.format(
            "mutation { deleteComment(slug: \"%s\", id: \"%s\") { success } }",
            article.getSlug(), comment.getId());

    Boolean success =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.deleteComment.success");

    assertTrue(success);
    verify(commentRepository).remove(comment);
  }

  @Test
  public void should_fail_delete_comment_when_not_authorized() {
    User user = defaultUser();
    User otherUser = new User("other@email.com", "other", "pass", "", "");
    setAuthenticatedUser(otherUser);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    Comment comment = new Comment("test comment", user.getId(), article.getId());
    when(commentRepository.findById(eq(article.getId()), eq(comment.getId())))
        .thenReturn(Optional.of(comment));

    String query =
        String.format(
            "mutation { deleteComment(slug: \"%s\", id: \"%s\") { success } }",
            article.getSlug(), comment.getId());

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.deleteComment"));
  }

  @Test
  public void should_fail_delete_comment_when_not_found() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    Article article = new Article("Title", "desc", "body", Arrays.asList(), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    when(commentRepository.findById(eq(article.getId()), eq("nonexistent")))
        .thenReturn(Optional.empty());

    String query =
        String.format(
            "mutation { deleteComment(slug: \"%s\", id: \"nonexistent\") { success } }",
            article.getSlug());

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.deleteComment"));
  }
}
