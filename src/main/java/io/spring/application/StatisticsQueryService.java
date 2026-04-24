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
  private StatisticsReadService statisticsReadService;
  private ArticleViewRepository articleViewRepository;

  public List<ArticleStatisticsData> getArticleStatistics() {
    return statisticsReadService.getArticleStatistics();
  }

  public void recordView(String articleId, User user) {
    String userId = user != null ? user.getId() : null;
    ArticleView articleView = new ArticleView(articleId, userId);
    articleViewRepository.save(articleView);
  }
}
