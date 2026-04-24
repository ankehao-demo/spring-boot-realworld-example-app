package io.spring.core.article;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Getter
@NoArgsConstructor
public class ArticleView {
  private String id;
  private String articleId;
  private String userId;
  private DateTime createdAt;

  public ArticleView(String articleId, String userId) {
    this.id = UUID.randomUUID().toString();
    this.articleId = articleId;
    this.userId = userId;
    this.createdAt = new DateTime();
  }
}
