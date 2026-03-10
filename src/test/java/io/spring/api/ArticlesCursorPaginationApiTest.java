package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPager;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ArticlesApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ArticlesCursorPaginationApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private ArticleQueryService articleQueryService;
  @MockBean private ArticleCommandService articleCommandService;

  private List<ArticleData> articleDataList;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);

    articleDataList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      DateTime now = DateTime.now().minusHours(i);
      articleDataList.add(
          new ArticleData(
              "id" + i,
              "slug-" + i,
              "title-" + i,
              "desc",
              "body",
              false,
              0,
              now,
              now,
              Arrays.asList("tag1"),
              new ProfileData(
                  "authorId", user.getUsername(), user.getBio(), user.getImage(), false)));
    }
  }

  @Test
  public void should_get_cursor_articles_forward() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(articleDataList, CursorPager.Direction.NEXT, true);

    when(articleQueryService.findRecentArticlesWithCursor(
            eq(null), eq(null), eq(null), any(), eq(null)))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .when()
        .get("/articles")
        .prettyPeek()
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(5))
        .body("pageInfo.hasNextPage", equalTo(true))
        .body("pageInfo.hasPreviousPage", equalTo(false));
  }

  @Test
  public void should_get_cursor_articles_forward_with_after() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(
            articleDataList.subList(2, 5), CursorPager.Direction.NEXT, false);

    when(articleQueryService.findRecentArticlesWithCursor(
            eq(null), eq(null), eq(null), any(), eq(null)))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .param("after", String.valueOf(DateTime.now().getMillis()))
        .when()
        .get("/articles")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(3))
        .body("pageInfo.hasNextPage", equalTo(false));
  }

  @Test
  public void should_get_cursor_articles_backward() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(
            articleDataList.subList(0, 3), CursorPager.Direction.PREV, true);

    when(articleQueryService.findRecentArticlesWithCursor(
            eq(null), eq(null), eq(null), any(), eq(null)))
        .thenReturn(cursorPager);

    given()
        .param("last", 5)
        .param("before", String.valueOf(DateTime.now().getMillis()))
        .when()
        .get("/articles")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(3))
        .body("pageInfo.hasPreviousPage", equalTo(true))
        .body("pageInfo.hasNextPage", equalTo(false));
  }

  @Test
  public void should_get_cursor_articles_with_tag_filter() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(articleDataList, CursorPager.Direction.NEXT, false);

    when(articleQueryService.findRecentArticlesWithCursor(
            eq("tag1"), eq(null), eq(null), any(), eq(null)))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .param("tag", "tag1")
        .when()
        .get("/articles")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(5));
  }

  @Test
  public void should_get_cursor_articles_with_author_filter() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(articleDataList, CursorPager.Direction.NEXT, false);

    when(articleQueryService.findRecentArticlesWithCursor(
            eq(null), eq("author1"), eq(null), any(), eq(null)))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .param("author", "author1")
        .when()
        .get("/articles")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(5));
  }

  @Test
  public void should_get_cursor_articles_with_favorited_filter() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(articleDataList, CursorPager.Direction.NEXT, false);

    when(articleQueryService.findRecentArticlesWithCursor(
            eq(null), eq(null), eq("user1"), any(), eq(null)))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .param("favorited", "user1")
        .when()
        .get("/articles")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(5));
  }

  @Test
  public void should_get_cursor_feed_forward_authenticated() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(articleDataList, CursorPager.Direction.NEXT, true);

    when(articleQueryService.findUserFeedWithCursor(eq(user), any())).thenReturn(cursorPager);

    given()
        .header("Authorization", "Token " + token)
        .param("first", 5)
        .when()
        .get("/articles/feed")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(5))
        .body("pageInfo.hasNextPage", equalTo(true))
        .body("pageInfo.hasPreviousPage", equalTo(false));
  }

  @Test
  public void should_get_cursor_feed_backward_authenticated() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(
            articleDataList.subList(0, 2), CursorPager.Direction.PREV, true);

    when(articleQueryService.findUserFeedWithCursor(eq(user), any())).thenReturn(cursorPager);

    given()
        .header("Authorization", "Token " + token)
        .param("last", 5)
        .param("before", String.valueOf(DateTime.now().getMillis()))
        .when()
        .get("/articles/feed")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(2))
        .body("pageInfo.hasPreviousPage", equalTo(true));
  }

  @Test
  public void should_get_401_for_feed_without_auth() throws Exception {
    given()
        .param("first", 5)
        .when()
        .get("/articles/feed")
        .then()
        .statusCode(401);
  }

  @Test
  public void should_cap_max_limit() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(articleDataList, CursorPager.Direction.NEXT, false);

    when(articleQueryService.findRecentArticlesWithCursor(
            eq(null), eq(null), eq(null), any(), eq(null)))
        .thenReturn(cursorPager);

    given()
        .param("first", 1001)
        .when()
        .get("/articles")
        .then()
        .statusCode(200);
  }

  @Test
  public void should_get_empty_result_with_cursor() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(new ArrayList<>(), CursorPager.Direction.NEXT, false);

    when(articleQueryService.findRecentArticlesWithCursor(
            eq(null), eq(null), eq(null), any(), eq(null)))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .when()
        .get("/articles")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(0))
        .body("pageInfo.hasNextPage", equalTo(false))
        .body("pageInfo.hasPreviousPage", equalTo(false));
  }

  @Test
  public void should_return_400_for_invalid_cursor() throws Exception {
    given()
        .param("first", 5)
        .param("after", "not-a-number")
        .when()
        .get("/articles")
        .then()
        .statusCode(400);
  }

  @Test
  public void should_return_400_when_both_first_and_last_specified() throws Exception {
    given()
        .param("first", 5)
        .param("last", 5)
        .when()
        .get("/articles")
        .then()
        .statusCode(400);
  }

  @Test
  public void should_still_work_with_offset_mode() throws Exception {
    when(articleQueryService.findRecentArticles(
            eq(null), eq(null), eq(null), any(), eq(null)))
        .thenReturn(
            new io.spring.application.data.ArticleDataList(articleDataList, 5));

    given()
        .param("offset", 0)
        .param("limit", 20)
        .when()
        .get("/articles")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(5))
        .body("articlesCount", equalTo(5));
  }
}
