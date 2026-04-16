package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Arrays;
import java.util.Optional;
import org.joda.time.DateTime;
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
      CommentMutation.class,
      CommentDatafetcher.class,
      ArticleDatafetcher.class,
      ProfileDatafetcher.class,
      MeDatafetcher.class
    })
public class CommentMutationTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private ArticleRepository articleRepository;
  @MockBean private CommentRepository commentRepository;
  @MockBean private CommentQueryService commentQueryService;
  @MockBean private UserRepository userRepository;
  @MockBean private io.spring.application.ArticleQueryService articleQueryService;
  @MockBean private io.spring.application.ProfileQueryService profileQueryService;
  @MockBean private io.spring.application.UserQueryService userQueryService;
  @MockBean private io.spring.core.service.JwtService jwtService;

  private User user;
  private Article article;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    article =
        new Article("Test Title", "test description", "test body", Arrays.asList("java"), user.getId());

    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                user, null, java.util.Collections.emptyList()));
  }

  @Test
  void testAddComment() {
    when(articleRepository.findBySlug(eq("test-title"))).thenReturn(Optional.of(article));
    DateTime now = new DateTime();
    CommentData commentData =
        new CommentData(
            "comment-id",
            "Great article!",
            article.getId(),
            now,
            now,
            new ProfileData(
                user.getId(), user.getUsername(), user.getBio(), user.getImage(), false));
    when(commentQueryService.findById(any(), any())).thenReturn(Optional.of(commentData));

    ProfileData profileData =
        new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false);
    when(profileQueryService.findByUsername(eq(user.getUsername()), any()))
        .thenReturn(Optional.of(profileData));

    String mutation =
        "mutation { addComment(slug: \"test-title\", body: \"Great article!\") { comment { id body } } }";

    String body =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.addComment.comment.body");
    org.assertj.core.api.Assertions.assertThat(body).isEqualTo("Great article!");
  }

  @Test
  void testDeleteComment() {
    when(articleRepository.findBySlug(eq("test-title"))).thenReturn(Optional.of(article));
    io.spring.core.comment.Comment comment =
        new io.spring.core.comment.Comment("Great article!", user.getId(), article.getId());
    when(commentRepository.findById(eq(article.getId()), any())).thenReturn(Optional.of(comment));

    String mutation =
        "mutation { deleteComment(slug: \"test-title\", id: \"" + comment.getId() + "\") { success } }";

    Boolean success =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.deleteComment.success");
    org.assertj.core.api.Assertions.assertThat(success).isTrue();
  }
}
