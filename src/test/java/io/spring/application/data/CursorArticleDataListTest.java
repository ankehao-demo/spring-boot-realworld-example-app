package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CursorArticleDataListTest {

  private ArticleData createArticleData(String id, DateTime updatedAt) {
    return new ArticleData(
        id,
        "slug-" + id,
        "title-" + id,
        "desc",
        "body",
        false,
        0,
        new DateTime(),
        updatedAt,
        Arrays.asList("tag1"),
        new ProfileData("authorId", "author", "bio", "image", false));
  }

  @Test
  public void should_convert_from_cursor_pager_with_data() {
    DateTime now = DateTime.now();
    List<ArticleData> articles = new ArrayList<>();
    articles.add(createArticleData("1", now));
    articles.add(createArticleData("2", now.minusHours(1)));

    CursorPager<ArticleData> pager = new CursorPager<>(articles, Direction.NEXT, true);
    CursorArticleDataList result = CursorArticleDataList.fromCursorPager(pager);

    assertNotNull(result);
    assertEquals(2, result.getArticles().size());
    assertNotNull(result.getPageInfo());
    assertNotNull(result.getPageInfo().getStartCursor());
    assertNotNull(result.getPageInfo().getEndCursor());
    assertTrue(result.getPageInfo().isHasNextPage());
    assertFalse(result.getPageInfo().isHasPreviousPage());
  }

  @Test
  public void should_convert_from_cursor_pager_backward() {
    DateTime now = DateTime.now();
    List<ArticleData> articles = new ArrayList<>();
    articles.add(createArticleData("1", now));

    CursorPager<ArticleData> pager = new CursorPager<>(articles, Direction.PREV, true);
    CursorArticleDataList result = CursorArticleDataList.fromCursorPager(pager);

    assertNotNull(result);
    assertEquals(1, result.getArticles().size());
    assertFalse(result.getPageInfo().isHasNextPage());
    assertTrue(result.getPageInfo().isHasPreviousPage());
  }

  @Test
  public void should_handle_empty_list() {
    CursorPager<ArticleData> pager =
        new CursorPager<>(new ArrayList<>(), Direction.NEXT, false);
    CursorArticleDataList result = CursorArticleDataList.fromCursorPager(pager);

    assertNotNull(result);
    assertTrue(result.getArticles().isEmpty());
    assertNull(result.getPageInfo().getStartCursor());
    assertNull(result.getPageInfo().getEndCursor());
    assertFalse(result.getPageInfo().isHasNextPage());
    assertFalse(result.getPageInfo().isHasPreviousPage());
  }

  @Test
  public void should_have_correct_cursor_values() {
    DateTime time1 = new DateTime(1000000L);
    DateTime time2 = new DateTime(2000000L);
    List<ArticleData> articles = new ArrayList<>();
    articles.add(createArticleData("1", time1));
    articles.add(createArticleData("2", time2));

    CursorPager<ArticleData> pager = new CursorPager<>(articles, Direction.NEXT, false);
    CursorArticleDataList result = CursorArticleDataList.fromCursorPager(pager);

    assertEquals(String.valueOf(time1.getMillis()), result.getPageInfo().getStartCursor());
    assertEquals(String.valueOf(time2.getMillis()), result.getPageInfo().getEndCursor());
    assertFalse(result.getPageInfo().isHasNextPage());
    assertFalse(result.getPageInfo().isHasPreviousPage());
  }
}
