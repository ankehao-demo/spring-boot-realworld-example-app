# Phase 1: Test Stability Check — Test Baseline Report

**Date**: 2026-04-07  
**Repository**: `ankehao-demo/spring-boot-realworld-example-app`  
**Branch**: `master` (commit `c20e1f6`)  
**Objective**: Establish a test baseline on the UNMODIFIED codebase before Spring Boot 3.x migration.

---

## Test Environment

| Property         | Value                                                                 |
|------------------|-----------------------------------------------------------------------|
| **JDK Version**  | OpenJDK 11.0.30 (build 11.0.30+7-post-Ubuntu-1ubuntu122.04)          |
| **JVM**          | OpenJDK 64-Bit Server VM (mixed mode, sharing)                       |
| **Gradle Version** | 7.4 (wrapper)                                                      |
| **Spring Boot**  | 2.6.3                                                                 |
| **OS**           | Linux 5.15.200 amd64                                                  |
| **Database**     | SQLite (in-memory via `jdbc:sqlite:dev.db`)                           |
| **Test Framework** | JUnit 5 (Jupiter)                                                   |

---

## Test Execution Command

```bash
./gradlew clean test
```

**Build Result**: `BUILD SUCCESSFUL in 1m 9s` (6 actionable tasks: 6 executed)

---

## Test Results Summary

| Metric           | Count |
|------------------|-------|
| **Total Tests**  | 68    |
| **Passed**       | 68    |
| **Failed**       | 0     |
| **Errors**       | 0     |
| **Skipped**      | 0     |
| **Total Time**   | 4.550s (test execution only) |

### ✅ Baseline Test Count: 68

> **This number must not decrease during the Spring Boot 3.x migration.**

---

## Pre-Existing Failures

**None.** All 68 tests pass on the unmodified codebase.

---

## Detailed Test Results by Class

### API Layer Tests (35 tests)

| Test Class | Tests | Status | Time |
|-----------|-------|--------|------|
| `io.spring.api.ArticleApiTest` | 6 | ALL PASSED | 1.824s |
| `io.spring.api.ArticleFavoriteApiTest` | 2 | ALL PASSED | 0.202s |
| `io.spring.api.ArticlesApiTest` | 3 | ALL PASSED | 0.193s |
| `io.spring.api.CommentsApiTest` | 5 | ALL PASSED | 0.101s |
| `io.spring.api.CurrentUserApiTest` | 6 | ALL PASSED | 0.184s |
| `io.spring.api.ListArticleApiTest` | 3 | ALL PASSED | 0.042s |
| `io.spring.api.ProfileApiTest` | 3 | ALL PASSED | 0.053s |
| `io.spring.api.UsersApiTest` | 7 | ALL PASSED | 0.440s |

### Application Layer Tests (13 tests)

| Test Class | Tests | Status | Time |
|-----------|-------|--------|------|
| `io.spring.application.article.ArticleQueryServiceTest` | 9 | ALL PASSED | 0.515s |
| `io.spring.application.comment.CommentQueryServiceTest` | 2 | ALL PASSED | 0.021s |
| `io.spring.application.profile.ProfileQueryServiceTest` | 1 | ALL PASSED | 0.006s |
| `io.spring.application.tag.TagsQueryServiceTest` | 1 | ALL PASSED | 0.008s |

### Core Domain Tests (5 tests)

| Test Class | Tests | Status | Time |
|-----------|-------|--------|------|
| `io.spring.core.article.ArticleTest` | 5 | ALL PASSED | 0.003s |

### Infrastructure Layer Tests (14 tests)

| Test Class | Tests | Status | Time |
|-----------|-------|--------|------|
| `io.spring.infrastructure.article.ArticleRepositoryTransactionTest` | 1 | ALL PASSED | 0.048s |
| `io.spring.infrastructure.article.MyBatisArticleRepositoryTest` | 3 | ALL PASSED | 0.027s |
| `io.spring.infrastructure.comment.MyBatisCommentRepositoryTest` | 1 | ALL PASSED | 0.006s |
| `io.spring.infrastructure.favorite.MyBatisArticleFavoriteRepositoryTest` | 2 | ALL PASSED | 0.008s |
| `io.spring.infrastructure.service.DefaultJwtServiceTest` | 3 | ALL PASSED | 0.067s |
| `io.spring.infrastructure.user.MyBatisUserRepositoryTest` | 4 | ALL PASSED | 0.022s |

### Application Context Tests (1 test)

| Test Class | Tests | Status | Time |
|-----------|-------|--------|------|
| `io.spring.RealworldApplicationTests` | 1 | ALL PASSED | 0.780s |

---

## Full Test Method Listing

```
PASSED | io.spring.RealworldApplicationTests.contextLoads() (0.78s)
PASSED | io.spring.api.ArticleApiTest.should_read_article_success() (1.61s)
PASSED | io.spring.api.ArticleApiTest.should_update_article_content_success() (0.153s)
PASSED | io.spring.api.ArticleApiTest.should_403_if_not_author_delete_article() (0.014s)
PASSED | io.spring.api.ArticleApiTest.should_delete_article_success() (0.017s)
PASSED | io.spring.api.ArticleApiTest.should_get_403_if_not_author_to_update_article() (0.013s)
PASSED | io.spring.api.ArticleApiTest.should_404_if_article_not_found() (0.008s)
PASSED | io.spring.api.ArticleFavoriteApiTest.should_unfavorite_an_article_success() (0.177s)
PASSED | io.spring.api.ArticleFavoriteApiTest.should_favorite_an_article_success() (0.024s)
PASSED | io.spring.api.ArticlesApiTest.should_get_error_message_with_duplicated_title() (0.063s)
PASSED | io.spring.api.ArticlesApiTest.should_get_error_message_with_wrong_parameter() (0.061s)
PASSED | io.spring.api.ArticlesApiTest.should_create_article_success() (0.066s)
PASSED | io.spring.api.CommentsApiTest.should_create_comment_success() (0.032s)
PASSED | io.spring.api.CommentsApiTest.should_get_403_if_not_author_of_article_or_author_of_comment_when_delete_comment() (0.01s)
PASSED | io.spring.api.CommentsApiTest.should_get_comments_of_article_success() (0.019s)
PASSED | io.spring.api.CommentsApiTest.should_delete_comment_success() (0.008s)
PASSED | io.spring.api.CommentsApiTest.should_get_422_with_empty_body() (0.026s)
PASSED | io.spring.api.CurrentUserApiTest.should_get_401_with_invalid_token() (0.011s)
PASSED | io.spring.api.CurrentUserApiTest.should_get_401_if_not_login() (0.013s)
PASSED | io.spring.api.CurrentUserApiTest.should_get_current_user_with_token() (0.058s)
PASSED | io.spring.api.CurrentUserApiTest.should_get_error_if_email_exists_when_update_user_profile() (0.063s)
PASSED | io.spring.api.CurrentUserApiTest.should_update_current_user_profile() (0.025s)
PASSED | io.spring.api.CurrentUserApiTest.should_get_401_without_token() (0.008s)
PASSED | io.spring.api.ListArticleApiTest.should_get_default_article_list() (0.022s)
PASSED | io.spring.api.ListArticleApiTest.should_get_feeds_success() (0.012s)
PASSED | io.spring.api.ListArticleApiTest.should_get_feeds_401_without_login() (0.006s)
PASSED | io.spring.api.ProfileApiTest.should_unfollow_user_success() (0.019s)
PASSED | io.spring.api.ProfileApiTest.should_follow_user_success() (0.01s)
PASSED | io.spring.api.ProfileApiTest.should_get_user_profile_success() (0.021s)
PASSED | io.spring.api.UsersApiTest.should_login_success() (0.219s)
PASSED | io.spring.api.UsersApiTest.should_create_user_success() (0.068s)
PASSED | io.spring.api.UsersApiTest.should_show_error_message_for_invalid_email() (0.022s)
PASSED | io.spring.api.UsersApiTest.should_show_error_for_duplicated_username() (0.018s)
PASSED | io.spring.api.UsersApiTest.should_show_error_for_duplicated_email() (0.015s)
PASSED | io.spring.api.UsersApiTest.should_show_error_message_for_blank_username() (0.02s)
PASSED | io.spring.api.UsersApiTest.should_fail_login_with_wrong_password() (0.072s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_query_article_by_author() (0.275s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_query_article_by_tag() (0.022s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_get_user_feed() (0.016s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_get_article_with_right_favorite_and_favorite_count() (0.014s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_fetch_article_success() (0.012s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_get_default_article_list() (0.017s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_show_following_if_user_followed_author() (0.057s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_query_article_by_favorite() (0.071s)
PASSED | io.spring.application.article.ArticleQueryServiceTest.should_get_default_article_list_by_cursor() (0.024s)
PASSED | io.spring.application.comment.CommentQueryServiceTest.should_read_comments_of_article() (0.014s)
PASSED | io.spring.application.comment.CommentQueryServiceTest.should_read_comment_success() (0.006s)
PASSED | io.spring.application.profile.ProfileQueryServiceTest.should_fetch_profile_success() (0.006s)
PASSED | io.spring.application.tag.TagsQueryServiceTest.should_get_all_tags() (0.008s)
PASSED | io.spring.core.article.ArticleTest.should_get_right_slug_with_number_in_title() (0.001s)
PASSED | io.spring.core.article.ArticleTest.should_handle_other_language() (0.001s)
PASSED | io.spring.core.article.ArticleTest.should_get_right_slug() (0.0s)
PASSED | io.spring.core.article.ArticleTest.should_get_lower_case_slug() (0.0s)
PASSED | io.spring.core.article.ArticleTest.should_handle_commas() (0.0s)
PASSED | io.spring.infrastructure.article.ArticleRepositoryTransactionTest.transactional_test() (0.048s)
PASSED | io.spring.infrastructure.article.MyBatisArticleRepositoryTest.should_update_and_fetch_article_success() (0.012s)
PASSED | io.spring.infrastructure.article.MyBatisArticleRepositoryTest.should_delete_article() (0.007s)
PASSED | io.spring.infrastructure.article.MyBatisArticleRepositoryTest.should_create_and_fetch_article_success() (0.006s)
PASSED | io.spring.infrastructure.comment.MyBatisCommentRepositoryTest.should_create_and_fetch_comment_success() (0.006s)
PASSED | io.spring.infrastructure.favorite.MyBatisArticleFavoriteRepositoryTest.should_remove_favorite_success() (0.004s)
PASSED | io.spring.infrastructure.favorite.MyBatisArticleFavoriteRepositoryTest.should_save_and_fetch_articleFavorite_success() (0.003s)
PASSED | io.spring.infrastructure.service.DefaultJwtServiceTest.should_generate_and_parse_token() (0.063s)
PASSED | io.spring.infrastructure.service.DefaultJwtServiceTest.should_get_null_with_wrong_jwt() (0.001s)
PASSED | io.spring.infrastructure.service.DefaultJwtServiceTest.should_get_null_with_expired_jwt() (0.002s)
PASSED | io.spring.infrastructure.user.MyBatisUserRepositoryTest.should_update_user_success() (0.008s)
PASSED | io.spring.infrastructure.user.MyBatisUserRepositoryTest.should_create_new_user_follow_success() (0.005s)
PASSED | io.spring.infrastructure.user.MyBatisUserRepositoryTest.should_save_and_fetch_user_success() (0.004s)
PASSED | io.spring.infrastructure.user.MyBatisUserRepositoryTest.should_unfollow_user_success() (0.004s)
```

---

## Test Coverage by Layer

| Layer | Test Classes | Test Methods | Percentage |
|-------|-------------|-------------|------------|
| API (REST Controllers) | 8 | 35 | 51.5% |
| Application (Services) | 4 | 13 | 19.1% |
| Core (Domain) | 1 | 5 | 7.4% |
| Infrastructure (Repositories/Services) | 6 | 14 | 20.6% |
| Application Context | 1 | 1 | 1.5% |
| **Total** | **20** | **68** | **100%** |

---

## Compiler Warnings (Non-Blocking)

During compilation, the following warnings were noted (not test failures):

```
Note: GraphQLCustomizeExceptionHandler.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
Note: GraphQLCustomizeExceptionHandler.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```

These are pre-existing compiler warnings in the GraphQL exception handler and are informational only.

---

## Environment-Specific Notes

1. **SQLite**: The project uses an embedded SQLite database (`dev.db`) which is recreated during `clean`. No external database setup is required for tests.
2. **Flyway**: Database schema is managed via Flyway migrations (`V1__create_tables.sql`), applied automatically during test execution.
3. **DGS CodeGen**: The Netflix DGS GraphQL code generation plugin runs as part of the build (`generateJava` task).
4. **No network dependencies**: All tests run locally without external service dependencies.

---

## Conclusion

The test suite is **fully green** with all 68 tests passing. There are **zero pre-existing failures**. This establishes a clean baseline for the Spring Boot 3.x migration.

**Baseline test count for Phase 9 verification: 68**
