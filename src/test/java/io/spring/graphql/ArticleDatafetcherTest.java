package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.UserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ArticleDatafetcherTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private ArticleQueryService articleQueryService;

  @MockBean private UserRepository userRepository;

  @Test
  public void should_get_article_by_slug() {
    Instant now = Instant.now();
    ProfileData profile = new ProfileData("userid", "testuser", "bio", "image", false);
    ArticleData articleData =
        new ArticleData(
            "id1",
            "test-article",
            "Test Article",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("java", "spring"),
            profile);
    when(articleQueryService.findBySlug(eq("test-article"), any()))
        .thenReturn(Optional.of(articleData));

    String slug =
        dgsQueryExecutor.executeAndExtractJsonPath(
            "{ article(slug: \"test-article\") { slug title } }", "data.article.slug");
    Assertions.assertEquals("test-article", slug);
  }

  @Test
  public void should_get_articles_with_cursor_pagination() {
    Instant now = Instant.now();
    ProfileData profile = new ProfileData("userid", "testuser", "bio", "image", false);
    ArticleData articleData =
        new ArticleData(
            "id1",
            "test-article",
            "Test Article",
            "desc",
            "body",
            false,
            0,
            now,
            now,
            Arrays.asList("java"),
            profile);
    CursorPager<ArticleData> pager =
        new CursorPager<>(Collections.singletonList(articleData), Direction.NEXT, false);
    when(articleQueryService.findRecentArticlesWithCursor(any(), any(), any(), any(), any()))
        .thenReturn(pager);

    Integer count =
        dgsQueryExecutor.executeAndExtractJsonPath(
            "{ articles(first: 10) { edges { node { slug } } } }", "data.articles.edges.length()");
    Assertions.assertEquals(1, count);
  }
}
