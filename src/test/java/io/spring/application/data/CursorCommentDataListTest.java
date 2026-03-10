package io.spring.application.data;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CursorCommentDataListTest {

  private CommentData createCommentData(String id, DateTime createdAt) {
    return new CommentData(
        id,
        "body-" + id,
        "articleId",
        createdAt,
        createdAt,
        new ProfileData("authorId", "author", "bio", "image", false));
  }

  @Test
  public void should_convert_from_cursor_pager_with_data() {
    DateTime now = DateTime.now();
    List<CommentData> comments = new ArrayList<>();
    comments.add(createCommentData("1", now));
    comments.add(createCommentData("2", now.minusHours(1)));

    CursorPager<CommentData> pager = new CursorPager<>(comments, Direction.NEXT, true);
    CursorCommentDataList result = CursorCommentDataList.fromCursorPager(pager);

    assertNotNull(result);
    assertEquals(2, result.getComments().size());
    assertNotNull(result.getPageInfo());
    assertNotNull(result.getPageInfo().getStartCursor());
    assertNotNull(result.getPageInfo().getEndCursor());
    assertTrue(result.getPageInfo().isHasNextPage());
    assertFalse(result.getPageInfo().isHasPreviousPage());
  }

  @Test
  public void should_convert_from_cursor_pager_backward() {
    DateTime now = DateTime.now();
    List<CommentData> comments = new ArrayList<>();
    comments.add(createCommentData("1", now));

    CursorPager<CommentData> pager = new CursorPager<>(comments, Direction.PREV, true);
    CursorCommentDataList result = CursorCommentDataList.fromCursorPager(pager);

    assertNotNull(result);
    assertEquals(1, result.getComments().size());
    assertFalse(result.getPageInfo().isHasNextPage());
    assertTrue(result.getPageInfo().isHasPreviousPage());
  }

  @Test
  public void should_handle_empty_list() {
    CursorPager<CommentData> pager =
        new CursorPager<>(new ArrayList<>(), Direction.NEXT, false);
    CursorCommentDataList result = CursorCommentDataList.fromCursorPager(pager);

    assertNotNull(result);
    assertTrue(result.getComments().isEmpty());
    assertNull(result.getPageInfo().getStartCursor());
    assertNull(result.getPageInfo().getEndCursor());
    assertFalse(result.getPageInfo().isHasNextPage());
    assertFalse(result.getPageInfo().isHasPreviousPage());
  }

  @Test
  public void should_have_correct_cursor_values() {
    DateTime time1 = new DateTime(1000000L);
    DateTime time2 = new DateTime(2000000L);
    List<CommentData> comments = new ArrayList<>();
    comments.add(createCommentData("1", time1));
    comments.add(createCommentData("2", time2));

    CursorPager<CommentData> pager = new CursorPager<>(comments, Direction.NEXT, false);
    CursorCommentDataList result = CursorCommentDataList.fromCursorPager(pager);

    assertEquals(String.valueOf(time1.getMillis()), result.getPageInfo().getStartCursor());
    assertEquals(String.valueOf(time2.getMillis()), result.getPageInfo().getEndCursor());
    assertFalse(result.getPageInfo().isHasNextPage());
    assertFalse(result.getPageInfo().isHasPreviousPage());
  }
}
