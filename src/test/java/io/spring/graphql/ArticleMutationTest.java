package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import io.spring.application.ArticleQueryService;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ArticleMutationTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private ArticleCommandService articleCommandService;

  @MockBean private ArticleQueryService articleQueryService;

  @MockBean private ArticleRepository articleRepository;

  @MockBean private ArticleFavoriteRepository articleFavoriteRepository;

  @MockBean private UserRepository userRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("test@example.com", "testuser", "password", "", "");
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(user, null, java.util.Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @AfterEach
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void should_create_article() {
    Article article =
        new Article("Test Article", "desc", "body", Arrays.asList("java"), user.getId());
    when(articleCommandService.createArticle(any(), any())).thenReturn(article);

    Instant now = Instant.now();
    ProfileData profile = new ProfileData(user.getId(), "testuser", "", "", false);
    ArticleData articleData =
        new ArticleData(
            article.getId(),
            article.getSlug(),
            "Test Article",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("java"),
            profile);
    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    String slug =
        dgsQueryExecutor.executeAndExtractJsonPath(
            "mutation { createArticle(input: {title: \"Test Article\", description: \"desc\", body: \"body\", tagList: [\"java\"]}) { article { slug } } }",
            "data.createArticle.article.slug");
    Assertions.assertNotNull(slug);
  }

  @Test
  public void should_delete_article() {
    Article article =
        new Article("Test Article", "desc", "body", Arrays.asList("java"), user.getId());
    when(articleRepository.findBySlug(eq("test-article"))).thenReturn(Optional.of(article));

    Boolean success =
        dgsQueryExecutor.executeAndExtractJsonPath(
            "mutation { deleteArticle(slug: \"test-article\") { success } }",
            "data.deleteArticle.success");
    Assertions.assertTrue(success);
  }
}
