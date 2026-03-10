package io.spring.api;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.DateTimeCursor;
import io.spring.application.Page;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.CursorArticleDataList;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.HashMap;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "profiles/{username}")
@AllArgsConstructor
public class ProfileApi {
  private ProfileQueryService profileQueryService;
  private UserRepository userRepository;
  private ArticleQueryService articleQueryService;

  @GetMapping
  public ResponseEntity getProfile(
      @PathVariable("username") String username, @AuthenticationPrincipal User user) {
    return profileQueryService
        .findByUsername(username, user)
        .map(this::profileResponse)
        .orElseThrow(ResourceNotFoundException::new);
  }

  @GetMapping(path = "feed")
  public ResponseEntity getProfileFeed(
      @PathVariable("username") String username,
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @RequestParam(value = "first", required = false) Integer first,
      @RequestParam(value = "after", required = false) String after,
      @RequestParam(value = "last", required = false) Integer last,
      @RequestParam(value = "before", required = false) String before,
      @AuthenticationPrincipal User user) {
    User target =
        userRepository.findByUsername(username).orElseThrow(ResourceNotFoundException::new);
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
        CursorPageParameter<org.joda.time.DateTime> cursorPageParameter;
        if (first != null) {
          cursorPageParameter =
              new CursorPageParameter<>(
                  DateTimeCursor.parse(after), first, CursorPager.Direction.NEXT);
        } else {
          cursorPageParameter =
              new CursorPageParameter<>(
                  DateTimeCursor.parse(before), last, CursorPager.Direction.PREV);
        }
        CursorPager<ArticleData> cursorPager =
            articleQueryService.findUserFeedWithCursor(target, cursorPageParameter);
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
    return ResponseEntity.ok(articleQueryService.findUserFeed(target, new Page(offset, limit)));
  }

  @PostMapping(path = "follow")
  public ResponseEntity follow(
      @PathVariable("username") String username, @AuthenticationPrincipal User user) {
    return userRepository
        .findByUsername(username)
        .map(
            target -> {
              FollowRelation followRelation = new FollowRelation(user.getId(), target.getId());
              userRepository.saveRelation(followRelation);
              return profileResponse(profileQueryService.findByUsername(username, user).get());
            })
        .orElseThrow(ResourceNotFoundException::new);
  }

  @DeleteMapping(path = "follow")
  public ResponseEntity unfollow(
      @PathVariable("username") String username, @AuthenticationPrincipal User user) {
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      User target = userOptional.get();
      return userRepository
          .findRelation(user.getId(), target.getId())
          .map(
              relation -> {
                userRepository.removeRelation(relation);
                return profileResponse(profileQueryService.findByUsername(username, user).get());
              })
          .orElseThrow(ResourceNotFoundException::new);
    } else {
      throw new ResourceNotFoundException();
    }
  }

  private ResponseEntity profileResponse(ProfileData profile) {
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("profile", profile);
          }
        });
  }
}
