package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import graphql.ExecutionResult;
import io.spring.application.data.UserData;
import io.spring.core.user.User;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class MeDatafetcherTest extends GraphQLTestBase {

  private User defaultUser() {
    return new User("user@email.com", "username", "password", "bio", "image");
  }

  @Test
  public void should_get_current_user() {
    User user = defaultUser();
    setAuthenticatedUser(user);

    UserData userData =
        new UserData(user.getId(), user.getEmail(), user.getUsername(), user.getBio(), user.getImage());
    when(userQueryService.findById(eq(user.getId()))).thenReturn(Optional.of(userData));
    when(jwtService.toToken(any())).thenReturn("test-token");
    when(jwtService.getSubFromToken(eq("test-token"))).thenReturn(Optional.of(user.getId()));

    String query = "{ me { email username token } }";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token test-token");

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.me", Collections.emptyMap(), LinkedHashMap.class, headers);

    assertNotNull(result);
    assertEquals("user@email.com", result.get("email"));
    assertEquals("username", result.get("username"));
  }

  @Test
  public void should_return_null_when_not_authenticated() {
    String query = "{ me { email username } }";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token invalid");

    ExecutionResult result =
        dgsQueryExecutor.execute(query, Collections.emptyMap(), Collections.emptyMap(), headers);

    Object meData = result.getData();
    assertNotNull(meData);
    LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) meData;
    assertNull(data.get("me"));
  }
}
