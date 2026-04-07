# Phase 0: Pre-Flight Analysis Report

## Spring Boot 3.x Migration — `spring-boot-realworld-example-app`

| Field | Value |
|-------|-------|
| **Report Date** | 2026-04-07 |
| **Target Spring Boot Version** | 3.4.5 |
| **Target JDK Version** | 21 |
| **Current Spring Boot Version** | 2.6.3 |
| **Current JDK Version** | 11 |
| **Current Gradle Wrapper Version** | 8.5 |

---

## 1. Current Version Inventory

### 1.1 Spring Boot Version

- **Plugin**: `org.springframework.boot` version `2.6.3` (`build.gradle` line 2)
- **Dependency Management**: `io.spring.dependency-management` version `1.0.11.RELEASE` (`build.gradle` line 3)

### 1.2 JDK Source/Target Compatibility

- **sourceCompatibility**: `11` (`build.gradle` line 10)
- **targetCompatibility**: `11` (`build.gradle` line 11)

### 1.3 Gradle Wrapper Version

- **Distribution URL**: `gradle-8.5-bin.zip` (`gradle/wrapper/gradle-wrapper.properties` line 3)
- **Status**: Gradle 8.5 is compatible with Spring Boot 3.4.x and JDK 21. No upgrade needed.

### 1.4 CI/CD Configuration

- **GitHub Actions workflow**: `.github/workflows/gradle.yml`
- **Current JDK in CI**: JDK 11 (Zulu distribution) (`actions/setup-java@v2`)
- **CI command**: `./gradlew clean test`
- **Note**: `actions/checkout@v2`, `actions/setup-java@v2`, and `actions/cache@v2` are outdated and should be updated to `v4`.

---

## 2. Full Dependency Inventory

### 2.1 Production Dependencies

| # | Dependency | Current Version | SB 3.x Compatible Version | Risk | Notes |
|---|-----------|----------------|--------------------------|------|-------|
| 1 | `spring-boot-starter-web` | Managed (2.6.3) | Managed (3.4.5) | LOW | Auto-managed by Spring Boot BOM |
| 2 | `spring-boot-starter-validation` | Managed (2.6.3) | Managed (3.4.5) | MEDIUM | `javax.validation` -> `jakarta.validation` namespace change |
| 3 | `spring-boot-starter-hateoas` | Managed (2.6.3) | Managed (3.4.5) | LOW | Auto-managed by Spring Boot BOM |
| 4 | `spring-boot-starter-security` | Managed (2.6.3) | Managed (3.4.5) | **HIGH** | Spring Security 5 -> 6 breaking changes; `WebSecurityConfigurerAdapter` removed |
| 5 | `mybatis-spring-boot-starter` | 2.2.2 | 3.0.3+ | **HIGH** | Major version jump; must use 3.x for Spring Boot 3 compatibility |
| 6 | `graphql-dgs-spring-boot-starter` (Netflix DGS) | 4.9.21 | 8.x+ / 9.x+ | **HIGH** | Major version jump (4.x -> 8.x+); requires Spring Boot 3; API changes |
| 7 | `flyway-core` | Managed (2.6.3) | Managed (3.4.5) | LOW | Auto-managed; may need `flyway-database-sqlite` module for SB 3 |
| 8 | `jjwt-api` | 0.11.2 | 0.12.x | MEDIUM | `SignatureAlgorithm` enum deprecated in 0.12.x; builder API changed |
| 9 | `jjwt-impl` (runtime) | 0.11.2 | 0.12.x | MEDIUM | Must match `jjwt-api` version |
| 10 | `jjwt-jackson` (runtime) | 0.11.2 | 0.12.x | MEDIUM | Must match `jjwt-api` version |
| 11 | `joda-time` | 2.10.13 | 2.12.x (or replace) | MEDIUM | Not directly affected by SB3, but recommended to migrate to `java.time` |
| 12 | `sqlite-jdbc` | 3.36.0.3 | 3.45.x+ | LOW | JDBC driver; version update straightforward |
| 13 | `lombok` (compileOnly) | Managed | Managed | LOW | Works with JDK 21; ensure latest version |
| 14 | `lombok` (annotationProcessor) | Managed | Managed | LOW | Same as above |

### 2.2 Test Dependencies

| # | Dependency | Current Version | SB 3.x Compatible Version | Risk | Notes |
|---|-----------|----------------|--------------------------|------|-------|
| 15 | `rest-assured` | 4.5.1 | 5.4.x+ | **HIGH** | Major version jump; `io.restassured` package changes; `javax` -> `jakarta` |
| 16 | `rest-assured:json-path` | 4.5.1 | 5.4.x+ | **HIGH** | Must match rest-assured version |
| 17 | `rest-assured:xml-path` | 4.5.1 | 5.4.x+ | **HIGH** | Must match rest-assured version |
| 18 | `rest-assured:spring-mock-mvc` | 4.5.1 | 5.4.x+ | **HIGH** | Must match rest-assured version |
| 19 | `spring-security-test` | Managed (2.6.3) | Managed (3.4.5) | LOW | Auto-managed |
| 20 | `spring-boot-starter-test` | Managed (2.6.3) | Managed (3.4.5) | LOW | Auto-managed |
| 21 | `mybatis-spring-boot-starter-test` | 2.2.2 | 3.0.3+ | **HIGH** | Must match mybatis-spring-boot-starter |

### 2.3 Gradle Plugins

| # | Plugin | Current Version | SB 3.x Compatible Version | Risk | Notes |
|---|--------|----------------|--------------------------|------|-------|
| 1 | `org.springframework.boot` | 2.6.3 | 3.4.5 | **HIGH** | Core migration target |
| 2 | `io.spring.dependency-management` | 1.0.11.RELEASE | 1.1.x | LOW | Minor version bump |
| 3 | `com.netflix.dgs.codegen` | 5.0.6 | 6.x+ | **HIGH** | Must match DGS framework version |
| 4 | `com.diffplug.spotless` | 6.2.1 | 6.25.x+ | LOW | Compatible; update for JDK 21 formatting support |

### 2.4 High-Risk Dependencies Summary

| Dependency | Current | Target | Migration Effort |
|-----------|---------|--------|-----------------|
| Spring Security (via starter) | 5.6.x | 6.x | **Major** - complete rewrite of `WebSecurityConfig` |
| Netflix DGS | 4.9.21 | 8.x+ | **Major** - API changes, codegen plugin update |
| MyBatis Spring Boot | 2.2.2 | 3.0.3+ | **Major** - jakarta namespace, config changes |
| Rest Assured | 4.5.1 | 5.4.x+ | **Major** - all test files affected |
| JJWT | 0.11.2 | 0.12.x | **Moderate** - deprecated API replacement |

---

## 3. `javax.*` Usage Scan

### 3.1 Summary

| Metric | Count |
|--------|-------|
| Total `javax.*` import statements (needing migration) | **36** |
| Files affected | **15** |
| `javax.crypto.*` imports (JDK-provided, EXCLUDED) | 2 |

### 3.2 Affected Files — `javax.validation.*` (must change to `jakarta.validation.*`)

| # | File | Imports | Count |
|---|------|---------|-------|
| 1 | `application/user/RegisterParam.java` | `javax.validation.constraints.Email`, `javax.validation.constraints.NotBlank` | 2 |
| 2 | `application/user/DuplicatedUsernameValidator.java` | `javax.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` | 2 |
| 3 | `application/user/DuplicatedEmailValidator.java` | `javax.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` | 2 |
| 4 | `application/user/DuplicatedUsernameConstraint.java` | `javax.validation.Constraint`, `javax.validation.Payload` | 2 |
| 5 | `application/user/DuplicatedEmailConstraint.java` | `javax.validation.Constraint`, `javax.validation.Payload` | 2 |
| 6 | `application/user/UpdateUserParam.java` | `javax.validation.constraints.Email` | 1 |
| 7 | `application/user/UserService.java` | `javax.validation.Constraint`, `javax.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext`, `javax.validation.Valid` | 4 |
| 8 | `application/article/NewArticleParam.java` | `javax.validation.constraints.NotBlank` | 1 |
| 9 | `application/article/ArticleCommandService.java` | `javax.validation.Valid` | 1 |
| 10 | `application/article/DuplicatedArticleValidator.java` | `javax.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` | 2 |
| 11 | `application/article/DuplicatedArticleConstraint.java` | `javax.validation.Constraint`, `javax.validation.Payload` | 2 |
| 12 | `api/CurrentUserApi.java` | `javax.validation.Valid` | 1 |
| 13 | `api/ArticleApi.java` | `javax.validation.Valid` | 1 |
| 14 | `api/UsersApi.java` | `javax.validation.Valid`, `javax.validation.constraints.Email`, `javax.validation.constraints.NotBlank` | 3 |
| 15 | `api/ArticlesApi.java` | `javax.validation.Valid` | 1 |
| 16 | `api/CommentsApi.java` | `javax.validation.Valid`, `javax.validation.constraints.NotBlank` | 2 |
| 17 | `api/exception/CustomizeExceptionHandler.java` | `javax.validation.ConstraintViolation`, `javax.validation.ConstraintViolationException` | 2 |
| 18 | `graphql/UserMutation.java` | `javax.validation.ConstraintViolationException` | 1 |
| 19 | `graphql/exception/GraphQLCustomizeExceptionHandler.java` | `javax.validation.ConstraintViolation`, `javax.validation.ConstraintViolationException` | 2 |

### 3.3 Affected Files — `javax.servlet.*` (must change to `jakarta.servlet.*`)

| # | File | Imports | Count |
|---|------|---------|-------|
| 1 | `api/security/JwtTokenFilter.java` | `javax.servlet.FilterChain`, `javax.servlet.ServletException`, `javax.servlet.http.HttpServletRequest`, `javax.servlet.http.HttpServletResponse` | 4 |

### 3.4 Excluded — JDK-Provided `javax.crypto.*` (no change needed)

| # | File | Imports | Count |
|---|------|---------|-------|
| 1 | `infrastructure/service/DefaultJwtService.java` | `javax.crypto.SecretKey`, `javax.crypto.spec.SecretKeySpec` | 2 |

---

## 4. Deprecated/Removed API Usage

### 4.1 `WebSecurityConfigurerAdapter` (REMOVED in Spring Security 6)

| File | Line | Usage |
|------|------|-------|
| `api/security/WebSecurityConfig.java` | 11 | `import ...WebSecurityConfigurerAdapter` |
| `api/security/WebSecurityConfig.java` | 23 | `public class WebSecurityConfig extends WebSecurityConfigurerAdapter` |
| `api/security/WebSecurityConfig.java` | 36 | `protected void configure(HttpSecurity http) throws Exception` (override) |

**Migration**: Replace with component-based configuration using `SecurityFilterChain` `@Bean` method.

### 4.2 `.antMatchers()` (REMOVED in Spring Security 6)

| File | Line | Usage |
|------|------|-------|
| `api/security/WebSecurityConfig.java` | 49 | `.antMatchers(HttpMethod.OPTIONS)` |
| `api/security/WebSecurityConfig.java` | 51 | `.antMatchers("/graphiql")` |
| `api/security/WebSecurityConfig.java` | 53 | `.antMatchers("/graphql")` |
| `api/security/WebSecurityConfig.java` | 55 | `.antMatchers(HttpMethod.GET, "/articles/feed")` |
| `api/security/WebSecurityConfig.java` | 57 | `.antMatchers(HttpMethod.POST, "/users", "/users/login")` |
| `api/security/WebSecurityConfig.java` | 59 | `.antMatchers(HttpMethod.GET, "/articles/**", "/profiles/**", "/tags")` |

**Migration**: Replace with `.requestMatchers()`.

### 4.3 `.authorizeRequests()` (DEPRECATED in Spring Security 6)

| File | Line | Usage |
|------|------|-------|
| `api/security/WebSecurityConfig.java` | 48 | `.authorizeRequests()` |

**Migration**: Replace with `.authorizeHttpRequests()`.

### 4.4 Chained `.and()` configuration style (DEPRECATED in Spring Security 6)

| File | Lines | Usage |
|------|-------|-------|
| `api/security/WebSecurityConfig.java` | 41, 44, 47 | `.and()` chaining between `csrf()`, `cors()`, `exceptionHandling()`, `sessionManagement()`, `authorizeRequests()` |

**Migration**: Replace with lambda DSL style (e.g., `http.csrf(csrf -> csrf.disable())`).

### 4.5 `ResponseEntityExceptionHandler.handleMethodArgumentNotValid()` signature change

| File | Line | Usage |
|------|------|-------|
| `api/exception/CustomizeExceptionHandler.java` | 63-67 | Override uses `HttpStatus` parameter; in Spring 6, signature changes to use `HttpStatusCode` |

**Migration**: Update method signature to match new `ResponseEntityExceptionHandler` API.

### 4.6 JJWT Deprecated APIs

| File | Line | Usage |
|------|------|-------|
| `infrastructure/service/DefaultJwtService.java` | 6, 20, 27 | `SignatureAlgorithm` enum (deprecated in jjwt 0.12.x) |
| `infrastructure/service/DefaultJwtService.java` | 34 | `.setSubject()` (deprecated; use `.subject()` in 0.12.x) |
| `infrastructure/service/DefaultJwtService.java` | 35 | `.setExpiration()` (deprecated; use `.expiration()` in 0.12.x) |
| `infrastructure/service/DefaultJwtService.java` | 44 | `.setSigningKey()` / `.parseClaimsJws()` (deprecated; use `.verifyWith()` / `.parseSignedClaims()`) |

**Migration**: Update to jjwt 0.12.x builder/parser APIs.

### 4.7 Summary of Deprecated/Removed APIs

| API | Severity | Files Affected | Migration Effort |
|-----|----------|---------------|-----------------|
| `WebSecurityConfigurerAdapter` | **CRITICAL** | 1 | Complete rewrite of security config |
| `.antMatchers()` | **CRITICAL** | 1 (6 occurrences) | Rename to `.requestMatchers()` |
| `.authorizeRequests()` | **HIGH** | 1 | Rename to `.authorizeHttpRequests()` |
| `.and()` chaining | **HIGH** | 1 (3 occurrences) | Convert to lambda DSL |
| `ResponseEntityExceptionHandler` signature | **MEDIUM** | 1 | Update method signature |
| JJWT deprecated APIs | **MEDIUM** | 1 | Update builder/parser calls |

---

## 5. Baseline Test Inventory

### 5.1 Summary

| Metric | Count |
|--------|-------|
| Total test files | **23** |
| Test classes (with `@Test` methods) | **20** |
| Test support/base classes | **3** |

### 5.2 Test File Listing

#### API Tests (Controller layer — `@WebMvcTest`)

| # | File | Path |
|---|------|------|
| 1 | `ArticleApiTest.java` | `src/test/java/io/spring/api/` |
| 2 | `ArticlesApiTest.java` | `src/test/java/io/spring/api/` |
| 3 | `ArticleFavoriteApiTest.java` | `src/test/java/io/spring/api/` |
| 4 | `CommentsApiTest.java` | `src/test/java/io/spring/api/` |
| 5 | `CurrentUserApiTest.java` | `src/test/java/io/spring/api/` |
| 6 | `ListArticleApiTest.java` | `src/test/java/io/spring/api/` |
| 7 | `ProfileApiTest.java` | `src/test/java/io/spring/api/` |
| 8 | `UsersApiTest.java` | `src/test/java/io/spring/api/` |

#### Application/Service Tests

| # | File | Path |
|---|------|------|
| 9 | `ArticleQueryServiceTest.java` | `src/test/java/io/spring/application/article/` |
| 10 | `CommentQueryServiceTest.java` | `src/test/java/io/spring/application/comment/` |
| 11 | `ProfileQueryServiceTest.java` | `src/test/java/io/spring/application/profile/` |
| 12 | `TagsQueryServiceTest.java` | `src/test/java/io/spring/application/tag/` |

#### Core Domain Tests

| # | File | Path |
|---|------|------|
| 13 | `ArticleTest.java` | `src/test/java/io/spring/core/article/` |

#### Infrastructure/Repository Tests

| # | File | Path |
|---|------|------|
| 14 | `MyBatisArticleRepositoryTest.java` | `src/test/java/io/spring/infrastructure/article/` |
| 15 | `ArticleRepositoryTransactionTest.java` | `src/test/java/io/spring/infrastructure/article/` |
| 16 | `MyBatisCommentRepositoryTest.java` | `src/test/java/io/spring/infrastructure/comment/` |
| 17 | `MyBatisArticleFavoriteRepositoryTest.java` | `src/test/java/io/spring/infrastructure/favorite/` |
| 18 | `MyBatisUserRepositoryTest.java` | `src/test/java/io/spring/infrastructure/user/` |
| 19 | `DefaultJwtServiceTest.java` | `src/test/java/io/spring/infrastructure/service/` |

#### Integration/Smoke Tests

| # | File | Path |
|---|------|------|
| 20 | `RealworldApplicationTests.java` | `src/test/java/io/spring/` |

#### Test Support/Base Classes (not test classes themselves)

| # | File | Path | Purpose |
|---|------|------|---------|
| 21 | `TestWithCurrentUser.java` | `src/test/java/io/spring/api/` | Base class providing authenticated user context |
| 22 | `DbTestBase.java` | `src/test/java/io/spring/infrastructure/` | Base class for repository integration tests |
| 23 | `TestHelper.java` | `src/test/java/io/spring/` | Test utility methods |

---

## 6. Conditional Feature Detection

### 6.1 Feature Matrix

| Feature | Present? | Evidence | Migration Impact |
|---------|----------|----------|-----------------|
| **GraphQL (Netflix DGS)** | **YES** | `build.gradle`: `graphql-dgs-spring-boot-starter:4.9.21`; `src/main/java/io/spring/graphql/` contains 10 data fetchers/mutations; `src/main/resources/schema/schema.graphqls` | **HIGH** — DGS 4.x -> 8.x+ is a major version jump |
| **Spring HATEOAS** | **YES** | `build.gradle`: `spring-boot-starter-hateoas` | **LOW** — Auto-managed by Spring Boot BOM |
| **MyBatis** | **YES** | `build.gradle`: `mybatis-spring-boot-starter:2.2.2`; `src/main/java/io/spring/infrastructure/mybatis/` with mappers, read services, type handlers; `src/main/resources/mapper/*.xml` | **HIGH** — Requires MyBatis Spring Boot 3.x; jakarta namespace |
| **Flyway** | **YES** | `build.gradle`: `flyway-core`; `src/main/resources/db/migration/V1__create_tables.sql` | **LOW** — Auto-managed; verify SQLite dialect support |
| **Spring Security** | **YES** | `build.gradle`: `spring-boot-starter-security`; `src/main/java/io/spring/api/security/WebSecurityConfig.java`, `JwtTokenFilter.java` | **CRITICAL** — Complete security config rewrite needed |
| **Custom Validators** | **YES** | `src/main/java/io/spring/application/user/`: `DuplicatedEmailValidator`, `DuplicatedUsernameValidator`, `DuplicatedEmailConstraint`, `DuplicatedUsernameConstraint`; `src/main/java/io/spring/application/article/`: `DuplicatedArticleValidator`, `DuplicatedArticleConstraint` | **MEDIUM** — All use `javax.validation`, needs `jakarta.validation` |

### 6.2 GraphQL Component Inventory

| Component | File | Type |
|-----------|------|------|
| `ArticleDatafetcher` | `graphql/ArticleDatafetcher.java` | Query resolver |
| `ArticleMutation` | `graphql/ArticleMutation.java` | Mutation |
| `CommentDatafetcher` | `graphql/CommentDatafetcher.java` | Query resolver |
| `CommentMutation` | `graphql/CommentMutation.java` | Mutation |
| `MeDatafetcher` | `graphql/MeDatafetcher.java` | Query resolver |
| `ProfileDatafetcher` | `graphql/ProfileDatafetcher.java` | Query resolver |
| `RelationMutation` | `graphql/RelationMutation.java` | Mutation |
| `SecurityUtil` | `graphql/SecurityUtil.java` | Utility |
| `TagDatafetcher` | `graphql/TagDatafetcher.java` | Query resolver |
| `UserMutation` | `graphql/UserMutation.java` | Mutation |
| `GraphQLCustomizeExceptionHandler` | `graphql/exception/GraphQLCustomizeExceptionHandler.java` | Exception handler |

### 6.3 MyBatis Component Inventory

| Type | Components |
|------|-----------|
| **Type Handlers** | `DateTimeHandler` (Joda-Time `DateTime` <-> SQL) |
| **Mappers** | `ArticleMapper`, `ArticleFavoriteMapper`, `CommentMapper`, `UserMapper` |
| **Read Services** | `ArticleReadService`, `ArticleFavoritesReadService`, `CommentReadService`, `TagReadService`, `UserReadService`, `UserRelationshipQueryService` |
| **XML Mappers** | `src/main/resources/mapper/*.xml` |

### 6.4 Custom Validator Inventory

| Validator Class | Constraint Annotation | Domain |
|----------------|----------------------|--------|
| `DuplicatedEmailValidator` | `@DuplicatedEmailConstraint` | User registration |
| `DuplicatedUsernameValidator` | `@DuplicatedUsernameConstraint` | User registration |
| `DuplicatedArticleValidator` | `@DuplicatedArticleConstraint` | Article creation |
| (Inline in `UserService.java`) | `@UpdateUserConstraint` | User update |

---

## 7. Project Architecture

### 7.1 Module Structure

- **Single-module** Gradle project (no multi-module build)
- Root `build.gradle` with all dependencies
- Single `src/main/java` and `src/test/java` source sets

### 7.2 DDD Layer Structure

```
src/main/java/io/spring/
├── api/                          # PRESENTATION LAYER (REST Controllers)
│   ├── ArticleApi.java           #   Single article CRUD
│   ├── ArticlesApi.java          #   Article listing/creation
│   ├── ArticleFavoriteApi.java   #   Favorite/unfavorite
│   ├── CommentsApi.java          #   Comment CRUD
│   ├── CurrentUserApi.java       #   Authenticated user ops
│   ├── ProfileApi.java           #   User profiles & follow
│   ├── TagsApi.java              #   Tag listing
│   ├── UsersApi.java             #   Registration & login
│   ├── exception/                #   Exception handlers & error models
│   └── security/                 #   JWT filter & security config
├── application/                  # APPLICATION LAYER (Use Case Orchestration)
│   ├── ArticleQueryService.java  #   CQRS: Article reads
│   ├── CommentQueryService.java  #   CQRS: Comment reads
│   ├── ProfileQueryService.java  #   CQRS: Profile reads
│   ├── UserQueryService.java     #   CQRS: User reads
│   ├── TagsQueryService.java     #   CQRS: Tag reads
│   ├── article/                  #   Article commands, params, validators
│   ├── user/                     #   User commands, params, validators
│   ├── data/                     #   DTOs (ArticleData, UserData, etc.)
│   ├── CursorPager.java          #   Cursor-based pagination
│   ├── CursorPageParameter.java  #   Pagination parameters
│   ├── DateTimeCursor.java       #   DateTime cursor impl
│   ├── Page.java                 #   Offset-based pagination
│   ├── PageCursor.java           #   Page cursor
│   └── Node.java                 #   Graph node interface
├── core/                         # DOMAIN LAYER (Entities & Business Logic)
│   ├── article/                  #   Article, Tag, ArticleRepository
│   ├── comment/                  #   Comment, CommentRepository
│   ├── favorite/                 #   ArticleFavorite, ArticleFavoriteRepository
│   ├── user/                     #   User, FollowRelation, UserRepository
│   └── service/                  #   AuthorizationService, JwtService (interfaces)
├── graphql/                      # PRESENTATION LAYER (GraphQL — Netflix DGS)
│   ├── *Datafetcher.java         #   Query resolvers
│   ├── *Mutation.java            #   Mutation resolvers
│   ├── SecurityUtil.java         #   Auth utility
│   └── exception/                #   GraphQL exception handler
├── infrastructure/               # INFRASTRUCTURE LAYER (Technical Implementations)
│   ├── repository/               #   MyBatis repository implementations
│   ├── mybatis/                  #   Type handlers, mappers, read services
│   └── service/                  #   DefaultJwtService (JWT impl)
├── JacksonCustomizations.java    # Jackson serialization config
├── MyBatisConfig.java            # MyBatis transaction config
├── RealWorldApplication.java     # Spring Boot main class
└── Util.java                     # Utility class
```

### 7.3 CQRS Pattern Confirmation

The project implements CQRS (Command Query Responsibility Segregation):

**Command Side (Write)**:
- `ArticleCommandService` — Article create/update operations
- `UserService` — User create/update operations
- Repository interfaces in `core/` with MyBatis implementations in `infrastructure/repository/`

**Query Side (Read)**:
- `ArticleQueryService` — Article queries with enriched DTOs
- `CommentQueryService` — Comment queries
- `ProfileQueryService` — Profile queries with follow status
- `UserQueryService` — User data queries
- `TagsQueryService` — Tag queries
- MyBatis read services in `infrastructure/mybatis/readservice/` for optimized SQL

### 7.4 Dual API Support

The application exposes identical functionality through two API layers:
1. **REST API** via Spring MVC controllers in `api/` package
2. **GraphQL API** via Netflix DGS framework in `graphql/` package

Both delegate to the same application and domain services.

### 7.5 Database

- **Database**: SQLite (`dev.db` file-based)
- **JDBC Driver**: `sqlite-jdbc:3.36.0.3`
- **ORM**: MyBatis (not JPA/Hibernate)
- **Schema Management**: Flyway with single migration `V1__create_tables.sql`
- **Configuration**: `application.properties` with `jdbc:sqlite:dev.db`

### 7.6 Additional Architectural Notes

- **Authentication**: Stateless JWT (24-hour expiration, HS512 signing)
- **Date/Time**: Uses Joda-Time library (not `java.time`); custom `DateTimeHandler` for MyBatis
- **Code Formatting**: Google Java Format via Spotless plugin
- **Lombok**: Used for boilerplate reduction (compileOnly + annotationProcessor)

---

## 8. Migration Risk Assessment

### 8.1 Overall Risk: **HIGH**

The migration involves multiple breaking changes across several dimensions:

### 8.2 Risk Breakdown

| Area | Risk Level | Rationale |
|------|-----------|-----------|
| **javax -> jakarta namespace** | **HIGH** | 36 imports across 15+ files need updating |
| **Spring Security rewrite** | **CRITICAL** | `WebSecurityConfigurerAdapter` completely removed; `.antMatchers()` removed; full rewrite of `WebSecurityConfig.java` |
| **Netflix DGS upgrade** | **HIGH** | Major version jump (4.x -> 8.x+); GraphQL layer has 10+ components |
| **MyBatis upgrade** | **HIGH** | Major version jump (2.x -> 3.x); namespace change |
| **Rest Assured upgrade** | **HIGH** | Major version jump (4.x -> 5.x); affects all 8 API test files |
| **JJWT API changes** | **MEDIUM** | Deprecated APIs in `DefaultJwtService.java` |
| **ResponseEntityExceptionHandler** | **MEDIUM** | Method signature change in `CustomizeExceptionHandler.java` |
| **Joda-Time** | **LOW** | Not strictly required for SB3, but recommended to migrate to `java.time` |
| **JDK 11 -> 21** | **LOW** | Java is backward-compatible; no known issues |
| **Gradle 8.5** | **NONE** | Already compatible with JDK 21 and SB 3.4.x |

### 8.3 Recommended Migration Order

1. **Phase 1**: Update `build.gradle` versions (Spring Boot, dependencies, plugins) and JDK target
2. **Phase 2**: `javax.*` -> `jakarta.*` namespace migration (mechanical find-and-replace)
3. **Phase 3**: Rewrite `WebSecurityConfig.java` for Spring Security 6
4. **Phase 4**: Update JJWT usage in `DefaultJwtService.java`
5. **Phase 5**: Update `CustomizeExceptionHandler.java` for new `ResponseEntityExceptionHandler` API
6. **Phase 6**: Update Netflix DGS framework and GraphQL components
7. **Phase 7**: Update MyBatis configuration and mappers
8. **Phase 8**: Update test dependencies (Rest Assured 5.x, MyBatis test)
9. **Phase 9**: Update CI workflow (`.github/workflows/gradle.yml`) for JDK 21
10. **Phase 10**: Verify all tests pass; fix any remaining issues

---

## 9. File Counts

| Category | Count |
|----------|-------|
| Main Java source files | 78 |
| Test Java files | 23 |
| XML mapper files | 6 (in `src/main/resources/mapper/`) |
| GraphQL schema files | 1 (`schema.graphqls`) |
| Flyway migration files | 1 (`V1__create_tables.sql`) |
| CI workflow files | 1 (`gradle.yml`) |
| Configuration files | 2 (`application.properties`, `mybatis-config.xml` [referenced]) |

---

*Report generated: 2026-04-07 | Phase 0: Pre-Flight Analysis | READ-ONLY — no code changes made*
