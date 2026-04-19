package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.ArticleQueryService;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.data.ArticleStatisticsData;
import io.spring.core.article.ArticleRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ArticleApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ArticleStatisticsApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private ArticleQueryService articleQueryService;

  @MockBean private ArticleRepository articleRepository;

  @MockBean private ArticleCommandService articleCommandService;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_get_article_statistics_success() throws Exception {
    String slug = "test-article";
    ArticleStatisticsData statisticsData = new ArticleStatisticsData("article-id-1", 5, 3);

    when(articleQueryService.getArticleStatistics(eq(slug)))
        .thenReturn(Optional.of(statisticsData));

    RestAssuredMockMvc.when()
        .get("/articles/{slug}/stats", slug)
        .then()
        .statusCode(200)
        .body("statistics.id", equalTo("article-id-1"))
        .body("statistics.favoritesCount", equalTo(5))
        .body("statistics.commentCount", equalTo(3));
  }

  @Test
  public void should_get_404_if_article_not_found() throws Exception {
    when(articleQueryService.getArticleStatistics(anyString())).thenReturn(Optional.empty());

    RestAssuredMockMvc.when()
        .get("/articles/{slug}/stats", "non-existent-slug")
        .then()
        .statusCode(404);
  }
}
