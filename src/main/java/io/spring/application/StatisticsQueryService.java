package io.spring.application;

import io.spring.application.data.ArticleStatisticsData;
import io.spring.core.article.ArticleView;
import io.spring.core.article.ArticleViewRepository;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.StatisticsReadService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StatisticsQueryService {
  private static final double WORDS_PER_MINUTE = 200.0;

  private StatisticsReadService statisticsReadService;
  private ArticleViewRepository articleViewRepository;

  public List<ArticleStatisticsData> getArticleStatistics() {
    List<ArticleStatisticsData> statistics = statisticsReadService.getArticleStatistics();
    statistics.forEach(this::computeDerivedMetrics);
    return statistics;
  }

  public void recordView(String articleId, User user) {
    String userId = user != null ? user.getId() : null;
    ArticleView articleView = new ArticleView(articleId, userId);
    articleViewRepository.save(articleView);
  }

  private void computeDerivedMetrics(ArticleStatisticsData stat) {
    stat.setReadTimeMinutes(Math.max(1, Math.ceil(stat.getWordCount() / WORDS_PER_MINUTE)));
    if (stat.getViewCount() > 0) {
      stat.setEngagementRate(
          Math.round(
                  (stat.getFavoritesCount() + stat.getCommentsCount())
                      * 10000.0
                      / stat.getViewCount())
              / 100.0);
    } else {
      stat.setEngagementRate(0.0);
    }
  }
}
