package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.article.ArticleView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleViewMapper {
  void insert(@Param("articleView") ArticleView articleView);
}
