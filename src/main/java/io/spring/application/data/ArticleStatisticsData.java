package io.spring.application.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleStatisticsData {
  private String id;
  private int favoritesCount;
  private int commentCount;
}
