package io.spring.graphql;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import io.spring.application.ArticleQueryService;
import io.spring.application.CommentQueryService;
import io.spring.application.ProfileQueryService;
import io.spring.application.TagsQueryService;
import io.spring.application.UserQueryService;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.user.UserService;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.CommentRepository;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class GraphQLTestBase {

  @Autowired protected DgsQueryExecutor dgsQueryExecutor;
  @Autowired protected MockMvc mockMvc;

  @MockBean protected UserRepository userRepository;
  @MockBean protected ArticleRepository articleRepository;
  @MockBean protected CommentRepository commentRepository;
  @MockBean protected ArticleFavoriteRepository articleFavoriteRepository;
  @MockBean protected JwtService jwtService;
  @MockBean protected UserReadService userReadService;
  @MockBean protected ArticleQueryService articleQueryService;
  @MockBean protected CommentQueryService commentQueryService;
  @MockBean protected ProfileQueryService profileQueryService;
  @MockBean protected TagsQueryService tagsQueryService;
  @MockBean protected UserQueryService userQueryService;
  @MockBean protected UserService userService;
  @MockBean protected ArticleCommandService articleCommandService;
  @MockBean protected PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUpAnonymousAuth() {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new AnonymousAuthenticationToken(
                "anonymous",
                "anonymousUser",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
  }

  protected void setAuthenticatedUser(User user) {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @AfterEach
  public void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }
}
