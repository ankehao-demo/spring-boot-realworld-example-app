package io.spring.application.data;

import io.spring.application.CursorPager;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursorArticleDataList {
  private List<ArticleData> articles;
  private CursorPageInfo pageInfo;

  public static CursorArticleDataList fromCursorPager(CursorPager<ArticleData> cursorPager) {
    CursorPageInfo pageInfo =
        new CursorPageInfo(
            cursorPager.getStartCursor() != null
                ? cursorPager.getStartCursor().toString()
                : null,
            cursorPager.getEndCursor() != null ? cursorPager.getEndCursor().toString() : null,
            cursorPager.hasNext(),
            cursorPager.hasPrevious());
    return new CursorArticleDataList(cursorPager.getData(), pageInfo);
  }
}
