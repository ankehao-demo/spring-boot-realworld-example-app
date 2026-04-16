package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.application.ArticleQueryService;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Arrays;
import java.util.Optional;
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
      ArticleMutation.class,
      ArticleDatafetcher.class,
      ProfileDatafetcher.class,
      CommentDatafetcher.class,
      MeDatafetcher.class
    })
public class ArticleMutationTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private ArticleCommandService articleCommandService;
  @MockBean private ArticleFavoriteRepository articleFavoriteRepository;
  @MockBean private ArticleRepository articleRepository;
  @MockBean private ArticleQueryService articleQueryService;
  @MockBean private UserRepository userRepository;
  @MockBean private io.spring.application.ProfileQueryService profileQueryService;
  @MockBean private io.spring.application.CommentQueryService commentQueryService;
  @MockBean private io.spring.application.UserQueryService userQueryService;
  @MockBean private io.spring.core.service.JwtService jwtService;

  private User user;
  private Article article;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    article = new Article("Test Title", "test description", "test body", Arrays.asList("java"), user.getId());

    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(user, null, java.util.Collections.emptyList()));
  }

  @Test
  void testCreateArticle() {
    when(articleCommandService.createArticle(any(), eq(user))).thenReturn(article);
    ArticleData articleData =
        new ArticleData(
            article.getId(),
            article.getSlug(),
            article.getTitle(),
            article.getDescription(),
            article.getBody(),
            false,
            0,
            article.getCreatedAt(),
            article.getUpdatedAt(),
            Arrays.asList("java"),
            new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false));
    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    String mutation =
        "mutation { createArticle(input: {title: \"Test Title\", description: \"test description\", body: \"test body\", tagList: [\"java\"]}) { article { title } } }";

    String title =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.createArticle.article.title");
    org.assertj.core.api.Assertions.assertThat(title).isEqualTo("Test Title");
  }

  @Test
  void testDeleteArticle() {
    when(articleRepository.findBySlug(eq("test-title"))).thenReturn(Optional.of(article));

    String mutation = "mutation { deleteArticle(slug: \"test-title\") { success } }";

    Boolean success =
        dgsQueryExecutor.executeAndExtractJsonPath(mutation, "data.deleteArticle.success");
    org.assertj.core.api.Assertions.assertThat(success).isTrue();
  }

  @Test
  void testFavoriteArticle() {
    when(articleRepository.findBySlug(eq("test-title"))).thenReturn(Optional.of(article));
    ArticleData articleData =
        new ArticleData(
            article.getId(),
            article.getSlug(),
            article.getTitle(),
            article.getDescription(),
            article.getBody(),
            true,
            1,
            article.getCreatedAt(),
            article.getUpdatedAt(),
            Arrays.asList("java"),
            new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false));
    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    String mutation =
        "mutation { favoriteArticle(slug: \"test-title\") { article { title favorited } } }";

    String title =
        dgsQueryExecutor.executeAndExtractJsonPath(
            mutation, "data.favoriteArticle.article.title");
    org.assertj.core.api.Assertions.assertThat(title).isEqualTo("Test Title");
  }

  @Test
  void testUnfavoriteArticle() {
    when(articleRepository.findBySlug(eq("test-title"))).thenReturn(Optional.of(article));
    ArticleFavorite fav = new ArticleFavorite(article.getId(), user.getId());
    when(articleFavoriteRepository.find(article.getId(), user.getId()))
        .thenReturn(Optional.of(fav));
    ArticleData articleData =
        new ArticleData(
            article.getId(),
            article.getSlug(),
            article.getTitle(),
            article.getDescription(),
            article.getBody(),
            false,
            0,
            article.getCreatedAt(),
            article.getUpdatedAt(),
            Arrays.asList("java"),
            new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false));
    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    String mutation =
        "mutation { unfavoriteArticle(slug: \"test-title\") { article { title favorited } } }";

    String title =
        dgsQueryExecutor.executeAndExtractJsonPath(
            mutation, "data.unfavoriteArticle.article.title");
    org.assertj.core.api.Assertions.assertThat(title).isEqualTo("Test Title");
  }
}
