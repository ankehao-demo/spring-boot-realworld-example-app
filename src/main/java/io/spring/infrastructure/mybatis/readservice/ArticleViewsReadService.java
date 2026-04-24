package io.spring.infrastructure.mybatis.readservice;

import io.spring.application.data.ArticleViewCount;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleViewsReadService {
  int articleViewCount(@Param("articleId") String articleId);

  List<ArticleViewCount> articlesViewCount(@Param("ids") List<String> ids);
}
