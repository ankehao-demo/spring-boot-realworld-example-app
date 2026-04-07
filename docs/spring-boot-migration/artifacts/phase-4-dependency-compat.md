# Phase 4: Dependency Compatibility Report

## Summary

Updated all third-party dependencies in `build.gradle` to Spring Boot 3.x-compatible versions. The build now fails only due to expected `javax` -> `jakarta` namespace changes (Phase 6) and Spring Security API changes (Phase 5). No dependency version incompatibilities remain.

## Dependency Version Changes

| Dependency | Old Version (Phase 3) | New Version (Phase 4) | Notes |
|---|---|---|---|
| `org.springframework.boot` (plugin) | 3.2.5 | **3.4.5** | Target SB version for migration |
| `io.spring.dependency-management` (plugin) | 1.1.4 | **1.1.7** | Compatible with SB 3.4.5 |
| `com.netflix.dgs.codegen` (plugin) | 6.2.1 | **8.3.0** | Must match DGS framework major version; 6.x caused graphql-java version conflict with DGS 8.x |
| `com.diffplug.spotless` (plugin) | 6.25.0 | 6.25.0 | No change needed (already compatible) |
| `mybatis-spring-boot-starter` | 3.0.3 | **3.0.4** | Latest 3.x; uses jakarta namespace |
| `mybatis-spring-boot-starter-test` | 3.0.3 | **3.0.4** | Matches starter version |
| `graphql-dgs-spring-graphql-starter` | 8.5.0 | **8.7.1** | Spring Boot 3 / Spring GraphQL compatible |
| `jjwt-api` | 0.12.5 | **0.12.6** | Latest 0.12.x |
| `jjwt-impl` | 0.12.5 | **0.12.6** | Matches jjwt-api |
| `jjwt-jackson` | 0.12.5 | **0.12.6** | Matches jjwt-api |
| `joda-time` | *(removed by Phase 3)* | **2.13.0** | Re-added; still used by `ArticleDatafetcher.java` and `DateTimeCursor.java` |
| `sqlite-jdbc` | 3.45.3.0 | **3.46.1.0** | Latest stable |
| `rest-assured` (all 4 modules) | 5.4.0 | **5.5.0** | Latest 5.x |
| `flyway-core` | *(SB-managed)* | *(SB-managed: 10.20.1)* | No explicit version; managed by SB 3.4.5 BOM |

### Additional Infrastructure Changes

| Item | Old | New | Notes |
|---|---|---|---|
| Gradle wrapper | 8.5 | **8.12** | Required for Java 21 + SB 3.4.5 compatibility |
| JDK (build-time) | 11 | **21** | Required by Spring Boot 3.4.5 plugin (needs Java 17+) |

## Compatibility Notes

### DGS GraphQL (Highest Risk)

**Risk Level: HIGH** - This was the most complex dependency to align.

- **Starter artifact changed**: Phase 3 already migrated from `graphql-dgs-spring-boot-starter` (DGS 4.x) to `graphql-dgs-spring-graphql-starter` (DGS 8.x). Phase 4 bumped to 8.7.1.
- **Codegen plugin version**: The task suggested codegen 6.4.0, but that version does not exist. Version 6.3.0 caused a transitive dependency conflict (`java-dataloader` 3.3.0 vs 3.2.2 required by DGS 8.7.1). Resolved by using codegen **8.3.0** which aligns with the DGS 8.x framework.
- **`@DgsComponent`, `@DgsData`, `@DgsQuery`, `@InputArgument`**: All still exist in DGS 8.x. No annotation changes required.
- **`DgsDataFetchingEnvironment`**: Still present and compatible.
- **`generateJava` task**: Config syntax (`schemaPaths`, `packageName`) unchanged in codegen 8.3.0.
- **Generated types**: Still use `.newBuilder()` pattern. No breaking changes in generated code API.
- **Key risk for later phases**: DGS 8.x depends on Spring GraphQL (`spring-graphql-starter`) instead of the old standalone DGS web stack. This may affect auto-configuration and endpoint behavior in Phase 7/8.

### JJWT API Changes (0.11.x -> 0.12.6)

**Risk Level: MEDIUM** - Several deprecated APIs in `DefaultJwtService.java` need updating in a later phase.

The following changes were identified in `DefaultJwtService.java`:

| Current Code (0.11.x style) | Required Change (0.12.x) | Status |
|---|---|---|
| `SignatureAlgorithm.HS512` (enum) | Deprecated; use `Jwts.SIG.HS512` | Deferred to Phase 7 |
| `new SecretKeySpec(bytes, alg.getJcaName())` | Use `Keys.hmacShaKeyFor(bytes)` or keep SecretKeySpec with string literal | Deferred to Phase 7 |
| `Jwts.builder().setSubject(id)` | `Jwts.builder().subject(id)` | Deferred to Phase 7 |
| `Jwts.builder().setExpiration(date)` | `Jwts.builder().expiration(date)` | Deferred to Phase 7 |
| `Jwts.builder().signWith(key)` | Still works, but deprecated overload may change | Deferred to Phase 7 |
| `Jwts.parserBuilder().setSigningKey(key).build()` | `Jwts.parser().verifyWith(key).build()` | Deferred to Phase 7 |
| `.parseClaimsJws(token)` | `.parseSignedClaims(token)` | Deferred to Phase 7 |
| `claimsJws.getBody().getSubject()` | `claimsJws.getPayload().getSubject()` | Deferred to Phase 7 |

**Note**: The 0.12.x library maintains backward compatibility for most of these methods (they're deprecated, not removed), so the code compiles. These are non-breaking but should be updated to the modern API in Phase 7.

### MyBatis Spring Boot Starter 3.0.4

- Uses `jakarta.*` namespace internally — will work once Phase 6 namespace migration is complete.
- No API surface changes from 3.0.3 to 3.0.4.

### Rest Assured 5.5.0

- Minor version bump from 5.4.0. No breaking API changes.
- `spring-mock-mvc` module compatible with Spring 6.x / Spring Boot 3.x.

### SQLite JDBC 3.46.1.0

- Drop-in replacement. No API changes.
- Improved Java 21 compatibility.

### Joda-Time 2.13.0

- Re-added because the codebase still actively uses `org.joda.time` in:
  - `ArticleDatafetcher.java` (ISODateTimeFormat)
  - `DateTimeCursor.java` (cursor pagination)
  - MyBatis type handlers
- No breaking changes from 2.10.13 to 2.13.0.
- **Migration note**: Joda-Time should eventually be replaced with `java.time` (Phase 0 or future work).

### Flyway

- `flyway-core` version managed by Spring Boot 3.4.5 BOM (resolves to 10.20.1).
- `flyway-database-sqlite` does NOT exist as a separate artifact. SQLite support is included in `flyway-core` for community databases.
- Flyway 10.x has some changes to its Java API but the app uses Flyway via Spring Boot auto-configuration, so no source changes needed.

### Spotless 6.25.0

- Already at target version from Phase 3. No change needed.
- Compatible with Gradle 8.12 and Java 21.

## Remaining Compilation Errors (84 total)

All errors are **expected** and will be addressed in subsequent phases:

### javax.validation -> jakarta.validation (Phase 6) — ~70 errors

Affected files:
- `RegisterParam.java` — `javax.validation.constraints.NotBlank`, `@Email`
- `UpdateUserParam.java` — `javax.validation.constraints.Email`
- `NewArticleParam.java` — `javax.validation.constraints.NotBlank`
- `LoginParam` (inner class in `UsersApi.java`) — `javax.validation.constraints.*`
- `NewCommentParam` (inner class in `CommentsApi.java`) — `javax.validation.constraints.NotBlank`
- `ArticleCommandService.java` — `javax.validation.Valid`
- `UserService.java` — `javax.validation.*` (Validator, ConstraintViolation, etc.)
- `DuplicatedEmailConstraint.java` / `DuplicatedEmailValidator.java` — `javax.validation.*`
- `DuplicatedUsernameConstraint.java` / `DuplicatedUsernameValidator.java` — `javax.validation.*`
- `DuplicatedArticleConstraint.java` / `DuplicatedArticleValidator.java` — `javax.validation.*`
- `CustomizeExceptionHandler.java` — `javax.validation.ConstraintViolationException`
- `GraphQLCustomizeExceptionHandler.java` — `javax.validation.ConstraintViolationException`
- `UserMutation.java` — `javax.validation.ConstraintViolationException`
- REST controllers (`ArticlesApi`, `ArticleApi`, `CommentsApi`, `CurrentUserApi`, `UsersApi`) — `javax.validation.Valid`

### javax.servlet -> jakarta.servlet (Phase 6) — ~4 errors

Affected files:
- `JwtTokenFilter.java` — `javax.servlet.FilterChain`, `javax.servlet.ServletException`, `javax.servlet.http.HttpServletRequest`, `javax.servlet.http.HttpServletResponse`

### Spring Security API Changes (Phase 5) — ~2 errors

Affected files:
- `WebSecurityConfig.java`:
  - `WebSecurityConfigurerAdapter` removed in Spring Security 6.x — must refactor to `SecurityFilterChain` bean
  - `antMatchers()` replaced by `requestMatchers()`
  - `http.csrf().disable().cors().and()...` chaining style deprecated; use lambda DSL

## Conclusion

All dependency version incompatibilities have been resolved. The 84 remaining compilation errors are exclusively:
1. **Namespace changes** (`javax.*` -> `jakarta.*`) — Phase 6
2. **Spring Security API changes** — Phase 5
3. **JJWT deprecated API usage** — Phase 7 (compiles but uses deprecated methods)

No dependency lacks a Spring Boot 3.x-compatible version. The migration can proceed to Phase 5 (Spring Security) and Phase 6 (Namespace Migration).
