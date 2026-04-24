package io.spring.infrastructure.repository;

import io.spring.core.article.ArticleView;
import io.spring.core.article.ArticleViewRepository;
import io.spring.infrastructure.mybatis.mapper.ArticleViewMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MyBatisArticleViewRepository implements ArticleViewRepository {
  private ArticleViewMapper articleViewMapper;

  @Override
  public void save(ArticleView articleView) {
    articleViewMapper.insert(articleView);
  }
}
