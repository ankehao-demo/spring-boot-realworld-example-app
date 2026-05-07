package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.CommentPayload;
import io.spring.graphql.types.DeletionStatus;
import java.util.Collections;
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
class CommentMutationTest {

  @Mock private ArticleRepository articleRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private CommentQueryService commentQueryService;

  private CommentMutation commentMutation;
  private User user;

  @BeforeEach
  void setUp() {
    commentMutation = new CommentMutation(articleRepository, commentRepository, commentQueryService);
    user = new User("test@example.com", "testuser", "password", "bio", "image");
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void should_create_comment_successfully() {
    setAuthenticated(user);
    Article article =
        new Article("Title", "Desc", "Body", Collections.emptyList(), user.getId());
    when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));

    DateTime now = new DateTime();
    ProfileData profileData = new ProfileData(user.getId(), "testuser", "bio", "image", false);
    CommentData commentData =
        new CommentData("c1", "comment body", article.getId(), now, now, profileData);
    when(commentQueryService.findById(anyString(), eq(user)))
        .thenReturn(Optional.of(commentData));

    DataFetcherResult<CommentPayload> result =
        commentMutation.createComment(article.getSlug(), "comment body");

    assertNotNull(result);
    assertNotNull(result.getLocalContext());
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  void should_throw_authentication_exception_when_creating_comment_unauthenticated() {
    TestingAuthenticationToken auth = new TestingAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertThrows(
        AuthenticationException.class,
        () -> commentMutation.createComment("slug", "body"));
  }

  @Test
  void should_throw_when_article_not_found_on_create_comment() {
    setAuthenticated(user);
    when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> commentMutation.createComment("nonexistent", "body"));
  }

  @Test
  void should_delete_comment_successfully() {
    setAuthenticated(user);
    Article article =
        new Article("Title", "Desc", "Body", Collections.emptyList(), user.getId());
    when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));

    Comment comment = new Comment("comment body", user.getId(), article.getId());
    when(commentRepository.findById(article.getId(), comment.getId()))
        .thenReturn(Optional.of(comment));

    DeletionStatus result =
        commentMutation.removeComment(article.getSlug(), comment.getId());

    assertTrue(result.getSuccess());
    verify(commentRepository).remove(comment);
  }

  @Test
  void should_throw_authentication_exception_when_deleting_comment_unauthenticated() {
    TestingAuthenticationToken auth = new TestingAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertThrows(
        AuthenticationException.class,
        () -> commentMutation.removeComment("slug", "commentId"));
  }

  @Test
  void should_throw_when_article_not_found_on_delete_comment() {
    setAuthenticated(user);
    when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> commentMutation.removeComment("nonexistent", "commentId"));
  }

  @Test
  void should_throw_when_comment_not_found_on_delete() {
    setAuthenticated(user);
    Article article =
        new Article("Title", "Desc", "Body", Collections.emptyList(), user.getId());
    when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));
    when(commentRepository.findById(article.getId(), "nonexistent"))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> commentMutation.removeComment(article.getSlug(), "nonexistent"));
  }

  @Test
  void should_throw_no_authorization_when_not_author_on_delete_comment() {
    setAuthenticated(user);
    User otherUser = new User("other@example.com", "other", "password", "", "");
    Article article =
        new Article("Title", "Desc", "Body", Collections.emptyList(), otherUser.getId());
    when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));

    Comment comment = new Comment("comment body", otherUser.getId(), article.getId());
    when(commentRepository.findById(article.getId(), comment.getId()))
        .thenReturn(Optional.of(comment));

    assertThrows(
        NoAuthorizationException.class,
        () -> commentMutation.removeComment(article.getSlug(), comment.getId()));
  }

  private void setAuthenticated(User user) {
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
