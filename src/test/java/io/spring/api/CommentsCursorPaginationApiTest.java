package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.CommentQueryService;
import io.spring.application.CursorPager;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.CommentRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentsApi.class)
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class CommentsCursorPaginationApiTest extends TestWithCurrentUser {

  @MockBean private ArticleRepository articleRepository;
  @MockBean private CommentRepository commentRepository;
  @MockBean private CommentQueryService commentQueryService;

  @Autowired private MockMvc mvc;

  private Article article;
  private List<CommentData> commentDataList;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);

    article = new Article("title", "desc", "body", Arrays.asList("test", "java"), user.getId());
    when(articleRepository.findBySlug(eq(article.getSlug()))).thenReturn(Optional.of(article));

    commentDataList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      DateTime now = DateTime.now().minusMinutes(i);
      commentDataList.add(
          new CommentData(
              "commentId" + i,
              "comment body " + i,
              article.getId(),
              now,
              now,
              new ProfileData(
                  user.getId(), user.getUsername(), user.getBio(), user.getImage(), false)));
    }
  }

  @Test
  public void should_get_cursor_comments_forward() throws Exception {
    CursorPager<CommentData> cursorPager =
        new CursorPager<>(commentDataList, CursorPager.Direction.NEXT, true);

    when(commentQueryService.findByArticleIdWithCursor(eq(article.getId()), eq(null), any()))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .when()
        .get("/articles/{slug}/comments", article.getSlug())
        .prettyPeek()
        .then()
        .statusCode(200)
        .body("comments.size()", equalTo(5))
        .body("pageInfo.hasNextPage", equalTo(true))
        .body("pageInfo.hasPreviousPage", equalTo(false));
  }

  @Test
  public void should_get_cursor_comments_with_after() throws Exception {
    CursorPager<CommentData> cursorPager =
        new CursorPager<>(
            commentDataList.subList(2, 5), CursorPager.Direction.NEXT, false);

    when(commentQueryService.findByArticleIdWithCursor(eq(article.getId()), eq(null), any()))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .param("after", String.valueOf(DateTime.now().getMillis()))
        .when()
        .get("/articles/{slug}/comments", article.getSlug())
        .then()
        .statusCode(200)
        .body("comments.size()", equalTo(3))
        .body("pageInfo.hasNextPage", equalTo(false));
  }

  @Test
  public void should_get_cursor_comments_backward() throws Exception {
    CursorPager<CommentData> cursorPager =
        new CursorPager<>(
            commentDataList.subList(0, 2), CursorPager.Direction.PREV, true);

    when(commentQueryService.findByArticleIdWithCursor(eq(article.getId()), eq(null), any()))
        .thenReturn(cursorPager);

    given()
        .param("last", 5)
        .param("before", String.valueOf(DateTime.now().getMillis()))
        .when()
        .get("/articles/{slug}/comments", article.getSlug())
        .then()
        .statusCode(200)
        .body("comments.size()", equalTo(2))
        .body("pageInfo.hasPreviousPage", equalTo(true))
        .body("pageInfo.hasNextPage", equalTo(false));
  }

  @Test
  public void should_get_comments_without_cursor_params() throws Exception {
    when(commentQueryService.findByArticleId(anyString(), eq(null)))
        .thenReturn(commentDataList);

    given()
        .when()
        .get("/articles/{slug}/comments", article.getSlug())
        .then()
        .statusCode(200)
        .body("comments.size()", equalTo(5))
        .body("comments[0].id", equalTo(commentDataList.get(0).getId()));
  }

  @Test
  public void should_get_404_for_nonexistent_slug() throws Exception {
    when(articleRepository.findBySlug(eq("nonexistent"))).thenReturn(Optional.empty());

    given()
        .param("first", 5)
        .when()
        .get("/articles/{slug}/comments", "nonexistent")
        .then()
        .statusCode(404);
  }

  @Test
  public void should_get_empty_comments_with_cursor() throws Exception {
    CursorPager<CommentData> cursorPager =
        new CursorPager<>(new ArrayList<>(), CursorPager.Direction.NEXT, false);

    when(commentQueryService.findByArticleIdWithCursor(eq(article.getId()), eq(null), any()))
        .thenReturn(cursorPager);

    given()
        .param("first", 5)
        .when()
        .get("/articles/{slug}/comments", article.getSlug())
        .then()
        .statusCode(200)
        .body("comments.size()", equalTo(0))
        .body("pageInfo.hasNextPage", equalTo(false))
        .body("pageInfo.hasPreviousPage", equalTo(false));
  }

  @Test
  public void should_get_comments_with_following_status_authenticated() throws Exception {
    CursorPager<CommentData> cursorPager =
        new CursorPager<>(commentDataList, CursorPager.Direction.NEXT, false);

    when(commentQueryService.findByArticleIdWithCursor(eq(article.getId()), eq(user), any()))
        .thenReturn(cursorPager);

    given()
        .header("Authorization", "Token " + token)
        .param("first", 5)
        .when()
        .get("/articles/{slug}/comments", article.getSlug())
        .then()
        .statusCode(200)
        .body("comments.size()", equalTo(5));
  }

  @Test
  public void should_return_400_for_invalid_cursor() throws Exception {
    given()
        .param("first", 5)
        .param("after", "not-a-number")
        .when()
        .get("/articles/{slug}/comments", article.getSlug())
        .then()
        .statusCode(400);
  }
}
