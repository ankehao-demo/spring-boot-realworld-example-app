package io.spring.api;

import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.DateTimeCursor;
import io.spring.application.Page;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.article.NewArticleParam;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CursorArticleDataList;
import io.spring.core.article.Article;
import io.spring.core.user.User;
import java.util.HashMap;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/articles")
@AllArgsConstructor
public class ArticlesApi {
  private ArticleCommandService articleCommandService;
  private ArticleQueryService articleQueryService;

  @PostMapping
  public ResponseEntity createArticle(
      @Valid @RequestBody NewArticleParam newArticleParam, @AuthenticationPrincipal User user) {
    Article article = articleCommandService.createArticle(newArticleParam, user);
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("article", articleQueryService.findById(article.getId(), user).get());
          }
        });
  }

  @GetMapping(path = "feed")
  public ResponseEntity getFeed(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @RequestParam(value = "first", required = false) Integer first,
      @RequestParam(value = "after", required = false) String after,
      @RequestParam(value = "last", required = false) Integer last,
      @RequestParam(value = "before", required = false) String before,
      @AuthenticationPrincipal User user) {
    if (first != null && last != null) {
      return ResponseEntity.badRequest()
          .body(
              new HashMap<String, Object>() {
                {
                  put("errors", "cannot specify both first and last");
                }
              });
    }
    if (first != null || last != null) {
      try {
        CursorPageParameter<org.joda.time.DateTime> cursorPageParameter =
            buildCursorPageParameter(first, after, last, before);
        CursorPager<ArticleData> cursorPager =
            articleQueryService.findUserFeedWithCursor(user, cursorPageParameter);
        return ResponseEntity.ok(CursorArticleDataList.fromCursorPager(cursorPager));
      } catch (NumberFormatException e) {
        return ResponseEntity.badRequest()
            .body(
                new HashMap<String, Object>() {
                  {
                    put("errors", "invalid cursor format");
                  }
                });
      }
    }
    return ResponseEntity.ok(articleQueryService.findUserFeed(user, new Page(offset, limit)));
  }

  @GetMapping
  public ResponseEntity getArticles(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @RequestParam(value = "tag", required = false) String tag,
      @RequestParam(value = "favorited", required = false) String favoritedBy,
      @RequestParam(value = "author", required = false) String author,
      @RequestParam(value = "first", required = false) Integer first,
      @RequestParam(value = "after", required = false) String after,
      @RequestParam(value = "last", required = false) Integer last,
      @RequestParam(value = "before", required = false) String before,
      @AuthenticationPrincipal User user) {
    if (first != null && last != null) {
      return ResponseEntity.badRequest()
          .body(
              new HashMap<String, Object>() {
                {
                  put("errors", "cannot specify both first and last");
                }
              });
    }
    if (first != null || last != null) {
      try {
        CursorPageParameter<org.joda.time.DateTime> cursorPageParameter =
            buildCursorPageParameter(first, after, last, before);
        CursorPager<ArticleData> cursorPager =
            articleQueryService.findRecentArticlesWithCursor(
                tag, author, favoritedBy, cursorPageParameter, user);
        return ResponseEntity.ok(CursorArticleDataList.fromCursorPager(cursorPager));
      } catch (NumberFormatException e) {
        return ResponseEntity.badRequest()
            .body(
                new HashMap<String, Object>() {
                  {
                    put("errors", "invalid cursor format");
                  }
                });
      }
    }
    return ResponseEntity.ok(
        articleQueryService.findRecentArticles(
            tag, author, favoritedBy, new Page(offset, limit), user));
  }

  private CursorPageParameter<org.joda.time.DateTime> buildCursorPageParameter(
      Integer first, String after, Integer last, String before) {
    if (first != null) {
      return new CursorPageParameter<>(
          DateTimeCursor.parse(after), first, CursorPager.Direction.NEXT);
    } else {
      return new CursorPageParameter<>(
          DateTimeCursor.parse(before), last, CursorPager.Direction.PREV);
    }
  }
}
