# Phase 3: Core Version Upgrade — Spring Boot 3.4.5 / JDK 21

## Summary

Phase 3 bumps the core Spring Boot version from 2.7.18 to 3.4.5 and the JDK from 17 to 21. As expected, the build fails with **84 compilation errors**. This document catalogs every error, categorizes them for subsequent fix phases, and provides a risk assessment.

## Exact Version Changes

| Component | Before (Phase 2) | After (Phase 3) |
|---|---|---|
| Spring Boot | 2.7.18 | 3.4.5 |
| `io.spring.dependency-management` | 1.1.4 | 1.1.7 |
| `sourceCompatibility` | 17 | 21 |
| `targetCompatibility` | 17 | 21 |
| Gradle wrapper | 7.6.3 | 8.5 |
| CI JDK (GitHub Actions) | 17 | 21 |

> **Note:** Gradle 7.6.3 does not support JDK 21 (class file major version 65), so the wrapper was also upgraded to 8.5 as part of this phase.

## Compilation Results

```
BUILD FAILED in 33s
84 errors
```

Command used:
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew clean compileJava 2>&1 | tee compilation-output.txt
```

## Error Categorization

### Category 1: Namespace Errors — `javax.*` → `jakarta.*` (Phase 6)

**Count: 82 errors**

These are the dominant error class. Spring Boot 3.x requires Jakarta EE 9+ which renamed all `javax.*` packages to `jakarta.*`.

#### 1a. `javax.validation` → `jakarta.validation` (54 errors)

| # | File | Line | Import / Symbol |
|---|---|---|---|
| 1 | `GraphQLCustomizeExceptionHandler.java` | 20 | `import javax.validation.ConstraintViolation` |
| 2 | `GraphQLCustomizeExceptionHandler.java` | 21 | `import javax.validation.ConstraintViolationException` |
| 3 | `GraphQLCustomizeExceptionHandler.java` | 70 | `ConstraintViolationException` (unresolved type) |
| 4 | `ArticleCommandService.java` | 6 | `import javax.validation.Valid` |
| 5 | `ArticleCommandService.java` | 18 | `@Valid` (unresolved annotation) |
| 6 | `ArticleCommandService.java` | 30 | `@Valid` (unresolved annotation) |
| 7 | `NewArticleParam.java` | 5 | `import javax.validation.constraints.NotBlank` |
| 8 | `NewArticleParam.java` | 17 | `@NotBlank` (unresolved annotation) |
| 9 | `NewArticleParam.java` | 21 | `@NotBlank` (unresolved annotation) |
| 10 | `NewArticleParam.java` | 24 | `@NotBlank` (unresolved annotation) |
| 11 | `UserMutation.java` | 21 | `import javax.validation.ConstraintViolationException` |
| 12 | `UserService.java` | 7 | `import javax.validation.Constraint` |
| 13 | `UserService.java` | 8 | `import javax.validation.ConstraintValidator` |
| 14 | `UserService.java` | 9 | `import javax.validation.ConstraintValidatorContext` |
| 15 | `UserService.java` | 10 | `import javax.validation.Valid` |
| 16 | `UserService.java` | 34 | `@Valid` (unresolved annotation) |
| 17 | `UserService.java` | 46 | `@Valid` (unresolved annotation) |
| 18 | `UserService.java` | 59 | `@Constraint` (unresolved annotation) |
| 19 | `UserService.java` | 70 | `ConstraintValidator` (unresolved type) |
| 20 | `UserService.java` | 75 | `ConstraintValidatorContext` (unresolved type) |
| 21 | `RegisterParam.java` | 4 | `import javax.validation.constraints.Email` |
| 22 | `RegisterParam.java` | 5 | `import javax.validation.constraints.NotBlank` |
| 23 | `RegisterParam.java` | 15 | `@NotBlank` (unresolved annotation) |
| 24 | `RegisterParam.java` | 16 | `@Email` (unresolved annotation) |
| 25 | `RegisterParam.java` | 20 | `@NotBlank` (unresolved annotation) |
| 26 | `RegisterParam.java` | 24 | `@NotBlank` (unresolved annotation) |
| 27 | `UpdateUserParam.java` | 4 | `import javax.validation.constraints.Email` |
| 28 | `UpdateUserParam.java` | 18 | `@Email` (unresolved annotation) |
| 29 | `CurrentUserApi.java` | 12 | `import javax.validation.Valid` |
| 30 | `CurrentUserApi.java` | 44 | `@Valid` (unresolved annotation) |
| 31 | `CommentsApi.java` | 17 | `import javax.validation.Valid` |
| 32 | `CommentsApi.java` | 18 | `import javax.validation.constraints.NotBlank` |
| 33 | `CommentsApi.java` | 44 | `@Valid` (unresolved annotation) |
| 34 | `CommentsApi.java` | 100 | `@NotBlank` (unresolved annotation) |
| 35 | `CustomizeExceptionHandler.java` | 10 | `import javax.validation.ConstraintViolation` |
| 36 | `CustomizeExceptionHandler.java` | 11 | `import javax.validation.ConstraintViolationException` |
| 37 | `CustomizeExceptionHandler.java` | 82 | `@ExceptionHandler({ConstraintViolationException.class})` |
| 38 | `CustomizeExceptionHandler.java` | 86 | `ConstraintViolationException` (unresolved type) |
| 39 | `ArticlesApi.java` | 10 | `import javax.validation.Valid` |
| 40 | `ArticlesApi.java` | 30 | `@Valid` (unresolved annotation) |
| 41 | `UsersApi.java` | 18 | `import javax.validation.Valid` |
| 42 | `UsersApi.java` | 19 | `import javax.validation.constraints.Email` |
| 43 | `UsersApi.java` | 20 | `import javax.validation.constraints.NotBlank` |
| 44 | `UsersApi.java` | 40 | `@Valid` (unresolved annotation) |
| 45 | `UsersApi.java` | 48 | `@Valid` (unresolved annotation) |
| 46 | `UsersApi.java` | 73 | `@NotBlank` (unresolved annotation) |
| 47 | `UsersApi.java` | 74 | `@Email` (unresolved annotation) |
| 48 | `UsersApi.java` | 77 | `@NotBlank` (unresolved annotation) |
| 49 | `ArticleApi.java` | 15 | `import javax.validation.Valid` |
| 50 | `ArticleApi.java` | 48 | `@Valid` (unresolved annotation) |
| 51 | `DuplicatedEmailConstraint.java` | 5 | `import javax.validation.Constraint` |
| 52 | `DuplicatedEmailConstraint.java` | 6 | `import javax.validation.Payload` |
| 53 | `DuplicatedEmailConstraint.java` | 8 | `@Constraint` (unresolved annotation) |
| 54 | `DuplicatedEmailConstraint.java` | 15 | `Payload` (unresolved type) |
| 55 | `DuplicatedUsernameConstraint.java` | 5 | `import javax.validation.Constraint` |
| 56 | `DuplicatedUsernameConstraint.java` | 6 | `import javax.validation.Payload` |
| 57 | `DuplicatedUsernameConstraint.java` | 8 | `@Constraint` (unresolved annotation) |
| 58 | `DuplicatedUsernameConstraint.java` | 15 | `Payload` (unresolved type) |
| 59 | `DuplicatedEmailValidator.java` | 4 | `import javax.validation.ConstraintValidator` |
| 60 | `DuplicatedEmailValidator.java` | 5 | `import javax.validation.ConstraintValidatorContext` |
| 61 | `DuplicatedEmailValidator.java` | 9 | `ConstraintValidator` (unresolved type) |
| 62 | `DuplicatedEmailValidator.java` | 14 | `ConstraintValidatorContext` (unresolved type) |
| 63 | `DuplicatedUsernameValidator.java` | 4 | `import javax.validation.ConstraintValidator` |
| 64 | `DuplicatedUsernameValidator.java` | 5 | `import javax.validation.ConstraintValidatorContext` |
| 65 | `DuplicatedUsernameValidator.java` | 9 | `ConstraintValidator` (unresolved type) |
| 66 | `DuplicatedUsernameValidator.java` | 14 | `ConstraintValidatorContext` (unresolved type) |
| 67 | `DuplicatedArticleConstraint.java` | 8 | `import javax.validation.Constraint` |
| 68 | `DuplicatedArticleConstraint.java` | 9 | `import javax.validation.Payload` |
| 69 | `DuplicatedArticleConstraint.java` | 12 | `@Constraint` (unresolved annotation) |
| 70 | `DuplicatedArticleConstraint.java` | 20 | `Payload` (unresolved type) |
| 71 | `DuplicatedArticleValidator.java` | 5 | `import javax.validation.ConstraintValidator` |
| 72 | `DuplicatedArticleValidator.java` | 6 | `import javax.validation.ConstraintValidatorContext` |
| 73 | `DuplicatedArticleValidator.java` | 10 | `ConstraintValidator` (unresolved type) |
| 74 | `DuplicatedArticleValidator.java` | 15 | `ConstraintValidatorContext` (unresolved type) |

**Fix**: Replace all `javax.validation` imports with `jakarta.validation`. Mechanical find-and-replace across 19 source files.

#### 1b. `javax.servlet` → `jakarta.servlet` (8 errors)

| # | File | Line | Import / Symbol |
|---|---|---|---|
| 1 | `JwtTokenFilter.java` | 8 | `import javax.servlet.FilterChain` |
| 2 | `JwtTokenFilter.java` | 9 | `import javax.servlet.ServletException` |
| 3 | `JwtTokenFilter.java` | 10 | `import javax.servlet.http.HttpServletRequest` |
| 4 | `JwtTokenFilter.java` | 11 | `import javax.servlet.http.HttpServletResponse` |
| 5 | `JwtTokenFilter.java` | 26 | `HttpServletRequest` (unresolved type) |
| 6 | `JwtTokenFilter.java` | 26 | `HttpServletResponse` (unresolved type) |
| 7 | `JwtTokenFilter.java` | 26 | `FilterChain` (unresolved type) |
| 8 | `JwtTokenFilter.java` | 27 | `ServletException` (unresolved type) |

**Fix**: Replace all `javax.servlet` imports with `jakarta.servlet` in `JwtTokenFilter.java`.

### Category 2: Framework API Errors — Removed/Changed Spring APIs (Phase 5)

**Count: 2 errors**

| # | File | Line | Error |
|---|---|---|---|
| 1 | `WebSecurityConfig.java` | 11 | `import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter` — class not found |
| 2 | `WebSecurityConfig.java` | 23 | `class WebSecurityConfig extends WebSecurityConfigurerAdapter` — cannot resolve superclass |

**Details**: `WebSecurityConfigurerAdapter` was deprecated in Spring Security 5.7 and **removed** in Spring Security 6.0 (shipped with Spring Boot 3.x). The security configuration must be rewritten to use the component-based `SecurityFilterChain` bean approach.

**Fix**: Rewrite `WebSecurityConfig` to use `@Bean SecurityFilterChain` pattern instead of extending `WebSecurityConfigurerAdapter`.

### Category 3: Dependency Errors — Incompatible Library Versions (Phase 4)

**Count: 0 compilation errors (but see notes)**

No compilation errors are directly attributable to incompatible third-party library versions at this stage. However, the following dependencies are likely to cause **runtime** or **test compilation** errors in subsequent phases:

- **Netflix DGS `5.5.1`**: Not compatible with Spring Boot 3.x (requires DGS 7.x+ or 8.x+)
- **MyBatis Spring Boot Starter `2.3.2`**: Needs 3.x for Spring Boot 3 / Jakarta EE
- **JJWT `0.11.5`**: May need update for Jakarta compatibility
- **SQLite JDBC `3.42.0.1`**: Should work but may need minor version bump
- **Joda-Time `2.12.5`**: No direct issue but recommended to migrate to `java.time`

These will surface as runtime failures or test compilation errors in later phases.

### Category 4: Configuration Errors — Changed Property Names (Phase 7)

**Count: 0 compilation errors**

No compilation errors from changed property names. Configuration property changes (e.g., `spring.redis.*` → `spring.data.redis.*`, updated actuator paths) will only surface at runtime and must be audited separately in Phase 7.

### Category 5: API Adaptation Errors — Changed REST/GraphQL APIs (Phase 8)

**Count: 0 compilation errors**

No direct REST or GraphQL API adaptation errors at compilation time. The DGS framework incompatibility (Category 3) will likely produce errors when test compilation is attempted.

## Error Summary by Category

| Category | Error Count | Fix Phase | Complexity |
|---|---|---|---|
| Namespace: `javax.validation` → `jakarta.validation` | 74 | Phase 6 | Low — mechanical find-and-replace |
| Namespace: `javax.servlet` → `jakarta.servlet` | 8 | Phase 6 | Low — mechanical find-and-replace |
| Framework API: `WebSecurityConfigurerAdapter` removed | 2 | Phase 5 | Medium — requires rewrite to `SecurityFilterChain` |
| Dependency: incompatible library versions | 0 (compile) | Phase 4 | Medium — version bumps with potential API changes |
| Configuration: changed properties | 0 (compile) | Phase 7 | Low — property rename audit |
| API Adaptation: REST/GraphQL changes | 0 (compile) | Phase 8 | TBD — depends on DGS migration |
| **Total** | **84** | — | — |

## Risk Assessment

### High Risk
- **DGS GraphQL Framework (Phase 4/8)**: Netflix DGS 5.x is incompatible with Spring Boot 3.x. Migration to DGS 7.x+ or 8.x+ may require significant changes to GraphQL data fetchers, schema configuration, and code generation. Consider removing GraphQL support entirely if it is not critical.

### Medium Risk
- **Spring Security Rewrite (Phase 5)**: `WebSecurityConfigurerAdapter` removal requires a full rewrite of `WebSecurityConfig.java` to the `SecurityFilterChain` bean approach. The current configuration includes JWT filter integration, CORS, and endpoint-level authorization — all must be preserved.
- **MyBatis Starter (Phase 4)**: Upgrading from `mybatis-spring-boot-starter:2.3.2` to `3.x` may require mapper XML adjustments and configuration changes.

### Low Risk
- **Jakarta Namespace Migration (Phase 6)**: Pure mechanical find-and-replace of `javax.validation` → `jakarta.validation` and `javax.servlet` → `jakarta.servlet`. Well-understood, no logic changes needed. Affects 19 source files.
- **JJWT Library (Phase 4)**: Minor version bump expected; API is stable.
- **Configuration Properties (Phase 7)**: The application uses SQLite and minimal Spring properties, so the impact should be limited.

## Affected Files Summary

| File | Error Count | Categories |
|---|---|---|
| `UserService.java` | 8 | Namespace |
| `RegisterParam.java` | 6 | Namespace |
| `JwtTokenFilter.java` | 8 | Namespace |
| `UsersApi.java` | 8 | Namespace |
| `GraphQLCustomizeExceptionHandler.java` | 3 | Namespace |
| `NewArticleParam.java` | 4 | Namespace |
| `CommentsApi.java` | 3 | Namespace |
| `CustomizeExceptionHandler.java` | 4 | Namespace |
| `DuplicatedEmailConstraint.java` | 4 | Namespace |
| `DuplicatedEmailValidator.java` | 4 | Namespace |
| `DuplicatedUsernameConstraint.java` | 4 | Namespace |
| `DuplicatedUsernameValidator.java` | 4 | Namespace |
| `DuplicatedArticleConstraint.java` | 4 | Namespace |
| `DuplicatedArticleValidator.java` | 4 | Namespace |
| `ArticleCommandService.java` | 3 | Namespace |
| `CurrentUserApi.java` | 2 | Namespace |
| `ArticlesApi.java` | 2 | Namespace |
| `ArticleApi.java` | 2 | Namespace |
| `UpdateUserParam.java` | 2 | Namespace |
| `UserMutation.java` | 1 | Namespace |
| `WebSecurityConfig.java` | 2 | Framework API |

## Appendix: Full Compilation Output

The complete compilation output is saved in `compilation-output.txt` at the project root (not committed to the repository).
