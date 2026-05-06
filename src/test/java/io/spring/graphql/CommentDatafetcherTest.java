package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.exceptions.QueryException;
import io.spring.TestHelper;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CommentDatafetcherTest extends GraphQLTestBase {

  private User defaultUser() {
    return new User("user@email.com", "username", "password", "bio", "image");
  }

  @Test
  public void should_get_article_comments() {
    User user = defaultUser();
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    when(articleQueryService.findBySlug(eq("title-1"), any())).thenReturn(Optional.of(articleData));

    ProfileData profileData =
        new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false);
    DateTime now = new DateTime();
    CommentData commentData =
        new CommentData("comment-id", "comment body", articleData.getId(), now, now, profileData);
    CursorPager<CommentData> commentPager =
        new CursorPager<>(Arrays.asList(commentData), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(eq(articleData.getId()), any(), any()))
        .thenReturn(commentPager);

    String query =
        "{ article(slug: \"title-1\") { comments(first: 10) { edges { node { id body } } } } }";

    List<LinkedHashMap<String, Object>> edges =
        dgsQueryExecutor.executeAndExtractJsonPath(
            query, "data.article.comments.edges");

    assertNotNull(edges);
    assertEquals(1, edges.size());
    LinkedHashMap<String, Object> node = (LinkedHashMap<String, Object>) edges.get(0).get("node");
    assertEquals("comment-id", node.get("id"));
    assertEquals("comment body", node.get("body"));
  }

  @Test
  public void should_throw_when_neither_first_nor_last_for_comments() {
    User user = defaultUser();
    ArticleData articleData = TestHelper.articleDataFixture("1", user);
    when(articleQueryService.findBySlug(eq("title-1"), any())).thenReturn(Optional.of(articleData));

    String query =
        "{ article(slug: \"title-1\") { comments { edges { node { id } } } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.article.comments"));
  }
}
