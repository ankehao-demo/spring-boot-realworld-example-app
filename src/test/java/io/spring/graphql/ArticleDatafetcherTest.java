package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.TestHelper;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(
    classes = {
      DgsAutoConfiguration.class,
      ArticleDatafetcher.class,
      ProfileDatafetcher.class,
      CommentDatafetcher.class,
      MeDatafetcher.class
    })
public class ArticleDatafetcherTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private ArticleQueryService articleQueryService;
  @MockBean private UserRepository userRepository;
  @MockBean private io.spring.application.ProfileQueryService profileQueryService;
  @MockBean private io.spring.application.CommentQueryService commentQueryService;
  @MockBean private io.spring.application.UserQueryService userQueryService;
  @MockBean private io.spring.core.service.JwtService jwtService;

  private User user;
  private ArticleData articleData;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    articleData = TestHelper.articleDataFixture("1", user);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new AnonymousAuthenticationToken(
                "key",
                "anonymous",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
  }

  @Test
  void testGetArticles() {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(any(), any(), any(), any(), any()))
        .thenReturn(cursorPager);

    String query =
        "{ articles(first: 10) { edges { cursor node { title slug description body } } pageInfo { hasNextPage hasPreviousPage } } }";

    List<String> titles =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles.edges[*].node.title");
    org.assertj.core.api.Assertions.assertThat(titles).contains("title 1");
  }

  @Test
  void testFindArticleBySlug() {
    when(articleQueryService.findBySlug(eq("test-slug"), any()))
        .thenReturn(java.util.Optional.of(articleData));

    String query = "{ article(slug: \"test-slug\") { title slug body description } }";

    String title = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.article.title");
    org.assertj.core.api.Assertions.assertThat(title).isEqualTo("title 1");
  }

  @Test
  void testGetArticlesWithTag() {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(
            eq("java"), any(), any(), any(), any()))
        .thenReturn(cursorPager);

    String query = "{ articles(first: 10, withTag: \"java\") { edges { node { title } } } }";

    List<String> titles =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles.edges[*].node.title");
    org.assertj.core.api.Assertions.assertThat(titles).hasSize(1);
  }

  @Test
  void testGetArticlesEmpty() {
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Collections.emptyList(), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(any(), any(), any(), any(), any()))
        .thenReturn(cursorPager);

    String query =
        "{ articles(first: 10) { edges { node { title } } pageInfo { hasNextPage hasPreviousPage } } }";

    List<String> titles =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles.edges[*].node.title");
    org.assertj.core.api.Assertions.assertThat(titles).isEmpty();
  }

  @Test
  void testGetArticlesWithPagination() {
    ArticleData articleData2 = TestHelper.articleDataFixture("2", user);
    CursorPager<ArticleData> cursorPager =
        new CursorPager<>(Arrays.asList(articleData, articleData2), Direction.NEXT, true);
    when(articleQueryService.findRecentArticlesWithCursor(any(), any(), any(), any(), any()))
        .thenReturn(cursorPager);

    String query =
        "{ articles(first: 2) { edges { cursor node { title } } pageInfo { hasNextPage hasPreviousPage } } }";

    Boolean hasNext =
        dgsQueryExecutor.executeAndExtractJsonPath(query, "data.articles.pageInfo.hasNextPage");
    org.assertj.core.api.Assertions.assertThat(hasNext).isTrue();
  }
}
