package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.TestHelper;
import io.spring.application.ArticleQueryService;
import io.spring.application.CommentQueryService;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.joda.time.DateTime;
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
      CommentDatafetcher.class,
      ProfileDatafetcher.class,
      MeDatafetcher.class
    })
public class CommentDatafetcherTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private ArticleQueryService articleQueryService;
  @MockBean private CommentQueryService commentQueryService;
  @MockBean private UserRepository userRepository;
  @MockBean private io.spring.application.ProfileQueryService profileQueryService;
  @MockBean private io.spring.application.UserQueryService userQueryService;
  @MockBean private io.spring.core.service.JwtService jwtService;

  private User user;
  private ArticleData articleData;
  private CommentData commentData;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    articleData = TestHelper.articleDataFixture("1", user);
    DateTime now = new DateTime();
    commentData =
        new CommentData(
            "comment1",
            "test comment body",
            articleData.getId(),
            now,
            now,
            new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false));
    SecurityContextHolder.getContext()
        .setAuthentication(
            new AnonymousAuthenticationToken(
                "key",
                "anonymous",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
  }

  @Test
  void testArticleComments() {
    when(articleQueryService.findBySlug(eq("title-1"), any()))
        .thenReturn(Optional.of(articleData));

    CursorPager<CommentData> commentPager =
        new CursorPager<>(Arrays.asList(commentData), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(any(), any(), any()))
        .thenReturn(commentPager);

    ProfileData profileData =
        new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false);
    when(profileQueryService.findByUsername(eq(user.getUsername()), any()))
        .thenReturn(Optional.of(profileData));

    String query =
        "{ article(slug: \"title-1\") { comments(first: 10) { edges { node { id body } } } } }";

    java.util.List<String> bodies =
        dgsQueryExecutor.executeAndExtractJsonPath(
            query, "data.article.comments.edges[*].node.body");
    org.assertj.core.api.Assertions.assertThat(bodies).contains("test comment body");
  }

  @Test
  void testArticleCommentsEmpty() {
    when(articleQueryService.findBySlug(eq("title-1"), any()))
        .thenReturn(Optional.of(articleData));

    CursorPager<CommentData> commentPager =
        new CursorPager<>(Collections.emptyList(), Direction.NEXT, false);
    when(commentQueryService.findByArticleIdWithCursor(any(), any(), any()))
        .thenReturn(commentPager);

    ProfileData profileData =
        new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false);
    when(profileQueryService.findByUsername(eq(user.getUsername()), any()))
        .thenReturn(Optional.of(profileData));

    String query =
        "{ article(slug: \"title-1\") { comments(first: 10) { edges { node { id } } } } }";

    java.util.List<String> ids =
        dgsQueryExecutor.executeAndExtractJsonPath(
            query, "data.article.comments.edges[*].node.id");
    org.assertj.core.api.Assertions.assertThat(ids).isEmpty();
  }
}
