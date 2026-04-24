package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.StatisticsQueryService;
import io.spring.application.data.ArticleStatisticsData;
import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({StatisticsApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class StatisticsApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private StatisticsQueryService statisticsQueryService;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_get_article_statistics() throws Exception {
    DateTime now = new DateTime();
    ArticleStatisticsData stat =
        new ArticleStatisticsData(
            "article-id-1",
            "test-article",
            "Test Article",
            "johnjacob",
            10,
            5,
            3,
            now,
            500,
            3.0,
            80.0,
            25.0);

    when(statisticsQueryService.getArticleStatistics()).thenReturn(Arrays.asList(stat));

    given()
        .when()
        .get("/statistics/articles")
        .then()
        .statusCode(200)
        .body("statistics", hasSize(1))
        .body("statistics[0].articleId", equalTo("article-id-1"))
        .body("statistics[0].slug", equalTo("test-article"))
        .body("statistics[0].title", equalTo("Test Article"))
        .body("statistics[0].authorUsername", equalTo("johnjacob"))
        .body("statistics[0].viewCount", equalTo(10))
        .body("statistics[0].favoritesCount", equalTo(5))
        .body("statistics[0].commentsCount", equalTo(3))
        .body("statistics[0].wordCount", equalTo(500))
        .body("statistics[0].readTimeMinutes", equalTo(3.0f))
        .body("statistics[0].engagementRate", equalTo(80.0f))
        .body("statistics[0].trendingScore", equalTo(25.0f));
  }

  @Test
  public void should_get_empty_statistics() throws Exception {
    when(statisticsQueryService.getArticleStatistics()).thenReturn(Collections.emptyList());

    given()
        .when()
        .get("/statistics/articles")
        .then()
        .statusCode(200)
        .body("statistics", hasSize(0));
  }

  @Test
  public void should_access_statistics_without_authentication() throws Exception {
    when(statisticsQueryService.getArticleStatistics()).thenReturn(Collections.emptyList());

    RestAssuredMockMvc.when()
        .get("/statistics/articles")
        .then()
        .statusCode(200);
  }
}
