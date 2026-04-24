package io.spring.api;

import io.spring.application.StatisticsQueryService;
import io.spring.application.data.ArticleStatisticsData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/statistics")
@AllArgsConstructor
public class StatisticsApi {
  private StatisticsQueryService statisticsQueryService;

  @GetMapping("/articles")
  public ResponseEntity<?> getArticleStatistics() {
    List<ArticleStatisticsData> statistics = statisticsQueryService.getArticleStatistics();
    return ResponseEntity.ok(statisticsResponse(statistics));
  }

  private Map<String, Object> statisticsResponse(List<ArticleStatisticsData> statistics) {
    return new HashMap<String, Object>() {
      {
        put("statistics", statistics);
      }
    };
  }
}
