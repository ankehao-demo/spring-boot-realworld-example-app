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
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
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

@WebMvcTest(ProfileApi.class)
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ProfileFeedApiTest extends TestWithCurrentUser {

  @Autowired private MockMvc mvc;

  @MockBean private ProfileQueryService profileQueryService;
  @MockBean private ArticleQueryService articleQueryService;

  private User targetUser;
  private List<ArticleData> articleDataList;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);

    targetUser = new User("target@test.com", "targetuser", "123", "", "");
    when(userRepository.findByUsername(eq("targetuser"))).thenReturn(Optional.of(targetUser));

    articleDataList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
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
                  "authorId", "someauthor", "bio", "image", false)));
    }
  }

  @Test
  public void should_get_profile_feed_with_cursor() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(articleDataList, CursorPager.Direction.NEXT, true);

    when(articleQueryService.findUserFeedWithCursor(eq(targetUser), any()))
        .thenReturn(cursorPager);

    given()
        .header("Authorization", "Token " + token)
        .param("first", 5)
        .when()
        .get("/profiles/{username}/feed", "targetuser")
        .prettyPeek()
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(3))
        .body("pageInfo.hasNextPage", equalTo(true))
        .body("pageInfo.hasPreviousPage", equalTo(false));
  }

  @Test
  public void should_get_profile_feed_with_offset() throws Exception {
    when(articleQueryService.findUserFeed(eq(targetUser), any()))
        .thenReturn(new ArticleDataList(articleDataList, 3));

    given()
        .header("Authorization", "Token " + token)
        .param("offset", 0)
        .param("limit", 20)
        .when()
        .get("/profiles/{username}/feed", "targetuser")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(3))
        .body("articlesCount", equalTo(3));
  }

  @Test
  public void should_get_401_without_auth() throws Exception {
    given()
        .param("first", 5)
        .when()
        .get("/profiles/{username}/feed", "targetuser")
        .then()
        .statusCode(401);
  }

  @Test
  public void should_get_404_for_nonexistent_username() throws Exception {
    when(userRepository.findByUsername(eq("nonexistent"))).thenReturn(Optional.empty());

    given()
        .header("Authorization", "Token " + token)
        .param("first", 5)
        .when()
        .get("/profiles/{username}/feed", "nonexistent")
        .then()
        .statusCode(404);
  }

  @Test
  public void should_get_empty_feed_when_target_follows_nobody() throws Exception {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(new ArrayList<>(), CursorPager.Direction.NEXT, false);

    when(articleQueryService.findUserFeedWithCursor(eq(targetUser), any()))
        .thenReturn(cursorPager);

    given()
        .header("Authorization", "Token " + token)
        .param("first", 5)
        .when()
        .get("/profiles/{username}/feed", "targetuser")
        .then()
        .statusCode(200)
        .body("articles.size()", equalTo(0))
        .body("pageInfo.hasNextPage", equalTo(false))
        .body("pageInfo.hasPreviousPage", equalTo(false));
  }
}
