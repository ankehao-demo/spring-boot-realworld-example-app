package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.graphql.types.UserPayload;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class MeDatafetcherTest {

  @Mock private UserQueryService userQueryService;
  @Mock private JwtService jwtService;
  @Mock private DataFetchingEnvironment dataFetchingEnvironment;

  private MeDatafetcher meDatafetcher;

  @BeforeEach
  void setUp() {
    meDatafetcher = new MeDatafetcher(userQueryService, jwtService);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void should_return_current_user_when_authenticated() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserData userData = new UserData(user.getId(), "test@example.com", "testuser", "bio", "image");
    when(userQueryService.findById(user.getId())).thenReturn(Optional.of(userData));

    DataFetcherResult<io.spring.graphql.types.User> result =
        meDatafetcher.getMe("Token test-token", dataFetchingEnvironment);

    assertNotNull(result);
    assertEquals("test@example.com", result.getData().getEmail());
    assertEquals("testuser", result.getData().getUsername());
    assertEquals("test-token", result.getData().getToken());
    assertEquals(user, result.getLocalContext());
  }

  @Test
  void should_return_null_when_anonymous() {
    AnonymousAuthenticationToken anonymous =
        new AnonymousAuthenticationToken(
            "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    SecurityContextHolder.getContext().setAuthentication(anonymous);

    DataFetcherResult<io.spring.graphql.types.User> result =
        meDatafetcher.getMe("Token test-token", dataFetchingEnvironment);

    assertNull(result);
  }

  @Test
  void should_return_null_when_principal_is_null() {
    TestingAuthenticationToken authentication = new TestingAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    DataFetcherResult<io.spring.graphql.types.User> result =
        meDatafetcher.getMe("Token test-token", dataFetchingEnvironment);

    assertNull(result);
  }

  @Test
  void should_return_user_payload_user() {
    User user = new User("test@example.com", "testuser", "password", "bio", "image");
    when(dataFetchingEnvironment.getLocalContext()).thenReturn(user);
    when(jwtService.toToken(user)).thenReturn("jwt-token");

    DataFetcherResult<io.spring.graphql.types.User> result =
        meDatafetcher.getUserPayloadUser(dataFetchingEnvironment);

    assertNotNull(result);
    assertEquals("test@example.com", result.getData().getEmail());
    assertEquals("testuser", result.getData().getUsername());
    assertEquals("jwt-token", result.getData().getToken());
    assertEquals(user, result.getLocalContext());
  }
}
