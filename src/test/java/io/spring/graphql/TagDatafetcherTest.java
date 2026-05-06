package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TagDatafetcherTest extends GraphQLTestBase {

  @Test
  public void should_get_all_tags() {
    when(tagsQueryService.allTags()).thenReturn(Arrays.asList("java", "spring", "graphql"));

    String query = "{ tags }";

    List<String> result = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.tags");

    assertNotNull(result);
    assertEquals(3, result.size());
    assertTrue(result.contains("java"));
    assertTrue(result.contains("spring"));
    assertTrue(result.contains("graphql"));
  }
}
