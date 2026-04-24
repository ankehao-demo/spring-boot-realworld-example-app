package io.spring.infrastructure.repository;

import io.spring.core.article.ArticleView;
import io.spring.core.article.ArticleViewRepository;
import io.spring.infrastructure.mybatis.mapper.ArticleViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisArticleViewRepository implements ArticleViewRepository {
  private ArticleViewMapper mapper;

  @Autowired
  public MyBatisArticleViewRepository(ArticleViewMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void save(ArticleView articleView) {
    mapper.insert(articleView);
  }
}
