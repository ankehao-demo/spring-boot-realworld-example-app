package io.spring.application.data;

import io.spring.application.CursorPager;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursorCommentDataList {
  private List<CommentData> comments;
  private CursorPageInfo pageInfo;

  public static CursorCommentDataList fromCursorPager(CursorPager<CommentData> cursorPager) {
    CursorPageInfo pageInfo =
        new CursorPageInfo(
            cursorPager.getStartCursor() != null
                ? cursorPager.getStartCursor().toString()
                : null,
            cursorPager.getEndCursor() != null ? cursorPager.getEndCursor().toString() : null,
            cursorPager.hasNext(),
            cursorPager.hasPrevious());
    return new CursorCommentDataList(cursorPager.getData(), pageInfo);
  }
}
