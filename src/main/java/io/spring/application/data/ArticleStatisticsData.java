package io.spring.application.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleStatisticsData {
  private String articleId;
  private String slug;
  private String title;
  private String authorUsername;
  private int viewCount;
  private int favoritesCount;
  private int commentsCount;
  private DateTime createdAt;
}
