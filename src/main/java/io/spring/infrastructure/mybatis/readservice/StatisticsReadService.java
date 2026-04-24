package io.spring.infrastructure.mybatis.readservice;

import io.spring.application.data.ArticleStatisticsData;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StatisticsReadService {
  List<ArticleStatisticsData> getArticleStatistics();

  int getArticleViewCount(@Param("articleId") String articleId);
}
