package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import io.spring.application.ArticleQueryService;
import io.spring.application.CommentQueryService;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
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
public class CommentDatafetcherTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private ArticleQueryService articleQueryService;

  @MockBean private CommentQueryService commentQueryService;

  @MockBean private UserRepository userRepository;

  @Test
  public void should_get_comments_for_article() {
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
    when(articleQueryService.findBySlug(eq("test-article"), any()))
        .thenReturn(Optional.of(articleData));

    CommentData commentData = new CommentData();
    commentData.setId("comment1");
    commentData.setBody("This is a comment");
    commentData.setArticleId("id1");
    commentData.setCreatedAt(now);
    commentData.setUpdatedAt(now);
    commentData.setProfileData(profile);
    // cursor is derived from createdAt via getCursor()

    CursorPager<CommentData> pager =
        new CursorPager<>(Collections.singletonList(commentData), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(any(), any(), any())).thenReturn(pager);

    String body =
        dgsQueryExecutor.executeAndExtractJsonPath(
            "{ article(slug: \"test-article\") { comments(first: 10) { edges { node { body } } } } }",
            "data.article.comments.edges[0].node.body");
    Assertions.assertEquals("This is a comment", body);
  }
}
