package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.netflix.graphql.dgs.exceptions.QueryException;
import io.spring.application.data.ProfileData;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ProfileDatafetcherTest extends GraphQLTestBase {

  @Test
  public void should_get_profile_by_username() {
    ProfileData profileData = new ProfileData("user-id", "username", "bio", "image", false);
    when(profileQueryService.findByUsername(eq("username"), any()))
        .thenReturn(Optional.of(profileData));

    String query =
        "{ profile(username: \"username\") { profile { username bio image following } } }";

    LinkedHashMap<String, Object> result =
        dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            query, "data.profile.profile", LinkedHashMap.class);

    assertNotNull(result);
    assertEquals("username", result.get("username"));
    assertEquals("bio", result.get("bio"));
    assertEquals("image", result.get("image"));
    assertEquals(false, result.get("following"));
  }

  @Test
  public void should_throw_when_profile_not_found() {
    when(profileQueryService.findByUsername(eq("nonexistent"), any()))
        .thenReturn(Optional.empty());

    String query = "{ profile(username: \"nonexistent\") { profile { username } } }";

    assertThrows(
        QueryException.class,
        () -> dgsQueryExecutor.executeAndExtractJsonPath(query, "data.profile"));
  }
}
