package io.spring.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import io.spring.application.data.ArticleStatisticsData;
import io.spring.core.article.ArticleViewRepository;
import io.spring.infrastructure.mybatis.readservice.StatisticsReadService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StatisticsQueryServiceTest {
  @Mock private StatisticsReadService statisticsReadService;
  @Mock private ArticleViewRepository articleViewRepository;
  @InjectMocks private StatisticsQueryService statisticsQueryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void should_calculate_read_time_from_word_count() {
    ArticleStatisticsData stat = createStat(1000, 10, 5, 3, 0);
    when(statisticsReadService.getArticleStatistics()).thenReturn(Arrays.asList(stat));

    List<ArticleStatisticsData> result = statisticsQueryService.getArticleStatistics();

    assertEquals(5.0, result.get(0).getReadTimeMinutes());
  }

  @Test
  public void should_return_minimum_one_minute_read_time() {
    ArticleStatisticsData stat = createStat(50, 10, 0, 0, 0);
    when(statisticsReadService.getArticleStatistics()).thenReturn(Arrays.asList(stat));

    List<ArticleStatisticsData> result = statisticsQueryService.getArticleStatistics();

    assertEquals(1.0, result.get(0).getReadTimeMinutes());
  }

  @Test
  public void should_calculate_engagement_rate() {
    ArticleStatisticsData stat = createStat(500, 100, 5, 3, 0);
    when(statisticsReadService.getArticleStatistics()).thenReturn(Arrays.asList(stat));

    List<ArticleStatisticsData> result = statisticsQueryService.getArticleStatistics();

    assertEquals(8.0, result.get(0).getEngagementRate());
  }

  @Test
  public void should_return_zero_engagement_rate_when_no_views() {
    ArticleStatisticsData stat = createStat(500, 0, 5, 3, 0);
    when(statisticsReadService.getArticleStatistics()).thenReturn(Arrays.asList(stat));

    List<ArticleStatisticsData> result = statisticsQueryService.getArticleStatistics();

    assertEquals(0.0, result.get(0).getEngagementRate());
  }

  @Test
  public void should_preserve_trending_score_from_database() {
    ArticleStatisticsData stat = createStat(500, 10, 5, 3, 42.0);
    when(statisticsReadService.getArticleStatistics()).thenReturn(Arrays.asList(stat));

    List<ArticleStatisticsData> result = statisticsQueryService.getArticleStatistics();

    assertEquals(42.0, result.get(0).getTrendingScore());
  }

  @Test
  public void should_handle_empty_statistics() {
    when(statisticsReadService.getArticleStatistics()).thenReturn(Collections.emptyList());

    List<ArticleStatisticsData> result = statisticsQueryService.getArticleStatistics();

    assertEquals(0, result.size());
  }

  @Test
  public void should_calculate_read_time_with_ceiling() {
    ArticleStatisticsData stat = createStat(450, 10, 0, 0, 0);
    when(statisticsReadService.getArticleStatistics()).thenReturn(Arrays.asList(stat));

    List<ArticleStatisticsData> result = statisticsQueryService.getArticleStatistics();

    assertEquals(3.0, result.get(0).getReadTimeMinutes());
  }

  private ArticleStatisticsData createStat(
      int wordCount, int viewCount, int favoritesCount, int commentsCount, double trendingScore) {
    ArticleStatisticsData stat = new ArticleStatisticsData();
    stat.setArticleId("article-1");
    stat.setSlug("test-article");
    stat.setTitle("Test Article");
    stat.setAuthorUsername("testuser");
    stat.setWordCount(wordCount);
    stat.setViewCount(viewCount);
    stat.setFavoritesCount(favoritesCount);
    stat.setCommentsCount(commentsCount);
    stat.setCreatedAt(new DateTime());
    stat.setTrendingScore(trendingScore);
    return stat;
  }
}
