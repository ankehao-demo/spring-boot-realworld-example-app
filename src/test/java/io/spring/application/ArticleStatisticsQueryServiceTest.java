package io.spring.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleStatisticsData;
import io.spring.application.data.ProfileData;
import io.spring.infrastructure.mybatis.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.mybatis.readservice.ArticleReadService;
import io.spring.infrastructure.mybatis.readservice.CommentReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.ArrayList;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleStatisticsQueryServiceTest {

  @Mock private ArticleReadService articleReadService;

  @Mock private UserRelationshipQueryService userRelationshipQueryService;

  @Mock private ArticleFavoritesReadService articleFavoritesReadService;

  @Mock private CommentReadService commentReadService;

  private ArticleQueryService articleQueryService;

  @BeforeEach
  public void setUp() {
    articleQueryService =
        new ArticleQueryService(
            articleReadService,
            userRelationshipQueryService,
            articleFavoritesReadService,
            commentReadService);
  }

  @Test
  public void should_return_statistics_when_article_exists() {
    String slug = "test-article";
    DateTime now = new DateTime();
    ArticleData articleData =
        new ArticleData(
            "article-id-1",
            slug,
            "Test Article",
            "Description",
            "Body",
            false,
            0,
            now,
            now,
            new ArrayList<>(),
            new ProfileData("user-id-1", "testuser", "bio", "image", false));

    when(articleReadService.findBySlug(eq(slug))).thenReturn(articleData);
    when(articleFavoritesReadService.articleFavoriteCount(eq("article-id-1"))).thenReturn(5);
    when(commentReadService.countByArticleId(eq("article-id-1"))).thenReturn(3);

    Optional<ArticleStatisticsData> result = articleQueryService.getArticleStatistics(slug);

    assertTrue(result.isPresent());
    assertEquals("article-id-1", result.get().getId());
    assertEquals(5, result.get().getFavoritesCount());
    assertEquals(3, result.get().getCommentCount());
  }

  @Test
  public void should_return_empty_when_article_not_found() {
    when(articleReadService.findBySlug(eq("non-existent"))).thenReturn(null);

    Optional<ArticleStatisticsData> result =
        articleQueryService.getArticleStatistics("non-existent");

    assertFalse(result.isPresent());
  }

  @Test
  public void should_return_zero_counts_when_no_favorites_or_comments() {
    String slug = "empty-article";
    DateTime now = new DateTime();
    ArticleData articleData =
        new ArticleData(
            "article-id-2",
            slug,
            "Empty Article",
            "Description",
            "Body",
            false,
            0,
            now,
            now,
            new ArrayList<>(),
            new ProfileData("user-id-1", "testuser", "bio", "image", false));

    when(articleReadService.findBySlug(eq(slug))).thenReturn(articleData);
    when(articleFavoritesReadService.articleFavoriteCount(eq("article-id-2"))).thenReturn(0);
    when(commentReadService.countByArticleId(eq("article-id-2"))).thenReturn(0);

    Optional<ArticleStatisticsData> result = articleQueryService.getArticleStatistics(slug);

    assertTrue(result.isPresent());
    assertEquals("article-id-2", result.get().getId());
    assertEquals(0, result.get().getFavoritesCount());
    assertEquals(0, result.get().getCommentCount());
  }
}
