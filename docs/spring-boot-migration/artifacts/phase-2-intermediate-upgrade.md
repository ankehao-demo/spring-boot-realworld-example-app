# Phase 2: Intermediate 2.7.x Upgrade Report

## Summary

Upgraded Spring Boot from **2.6.3** to **2.7.18** (latest 2.7.x release) as the required intermediate step before migrating to Spring Boot 3.x.

## Version Changes Made

| Dependency | Before | After |
|---|---|---|
| `org.springframework.boot` plugin | 2.6.3 | 2.7.18 |
| `io.spring.dependency-management` plugin | 1.0.11.RELEASE | 1.0.15.RELEASE |

No other dependency versions were changed. All existing dependencies remain at their previous versions.

## Compilation Results

- **Status**: SUCCESS
- **Compilation errors**: 0
- **No code changes were required** to compile against Spring Boot 2.7.18.

## Test Results

| Metric | Phase 1 Baseline (2.6.3) | Phase 2 (2.7.18) |
|---|---|---|
| Total tests | 68 | 68 |
| Passed | 68 | 68 |
| Failed | 0 | 0 |
| Ignored | 0 | 0 |

Test count is unchanged. No tests were disabled or removed.

## Deprecation Warnings

### 1. `WebSecurityConfigurerAdapter` (Spring Security)

- **File**: `src/main/java/io/spring/api/security/WebSecurityConfig.java`
- **Details**: `WebSecurityConfig` extends `WebSecurityConfigurerAdapter`, which is deprecated in Spring Security 5.7+ (shipped with Spring Boot 2.7.x). The recommended approach is to use component-based security configuration with a `SecurityFilterChain` bean instead.
- **Action**: **Do NOT fix yet** — this will be addressed in Phase 5 (Spring Security migration).

### 2. `DataFetcherExceptionHandler.onException()` (Netflix DGS / GraphQL)

- **File**: `src/main/java/io/spring/graphql/exception/GraphQLCustomizeExceptionHandler.java`
- **Details**: The synchronous `onException(DataFetcherExceptionHandlerParameters)` method is deprecated in favor of the asynchronous `handleException(DataFetcherExceptionHandlerParameters)` method that returns `CompletableFuture<DataFetcherExceptionHandlerResult>`.
- **Action**: Should be addressed during DGS framework upgrade in a later phase.

### 3. Unchecked/Unsafe Operations

- **File**: `src/main/java/io/spring/graphql/exception/GraphQLCustomizeExceptionHandler.java` (line 110)
- **Details**: Raw type cast `(List) json.get(...)` without generic type parameter.
- **Action**: Minor code quality issue, can be addressed in a future cleanup phase.

### 4. `javax.validation` → `jakarta.validation` (Future Migration)

- **File**: `src/main/java/io/spring/graphql/exception/GraphQLCustomizeExceptionHandler.java`
- **Details**: Uses `javax.validation.ConstraintViolation` and `javax.validation.ConstraintViolationException`. In Spring Boot 3.x, these will need to be migrated to `jakarta.validation.*`.
- **Action**: Will be required in Phase 3/4 (Spring Boot 3.x upgrade).

## Dependency Compatibility Notes

| Dependency | Version | Compatible with 2.7.18? | Notes |
|---|---|---|---|
| MyBatis Spring Boot Starter | 2.2.2 | Yes | All tests pass, no issues observed |
| Netflix DGS Spring Boot Starter | 4.9.21 | Yes | Works correctly; `onException` deprecation warning only |
| Flyway Core | (managed by Spring Boot) | Yes | Database migrations run successfully |
| SQLite JDBC | 3.36.0.3 | Yes | No issues |
| jjwt (JSON Web Token) | 0.11.2 | Yes | JWT generation/validation works correctly |
| Joda-Time | 2.10.13 | Yes | No issues |
| Rest Assured (test) | 4.5.1 | Yes | All API tests pass |
| Lombok | (managed by Spring Boot) | Yes | No issues |
| Spring Security | (managed: 5.7.x) | Yes | `WebSecurityConfigurerAdapter` deprecated but functional |
| Spotless (Google Java Format) | 6.2.1 | Yes | Code formatting works |
| DGS Codegen | 5.0.6 | Yes | GraphQL code generation works |

## Risks and Considerations for Phase 3+

1. **Spring Security refactor (Phase 5)**: `WebSecurityConfigurerAdapter` is deprecated and will be removed in Spring Security 6.x (Spring Boot 3.x). Must migrate to `SecurityFilterChain` bean approach.
2. **javax → jakarta namespace (Phase 3/4)**: Spring Boot 3.x requires Jakarta EE 9+ namespace (`jakarta.*` instead of `javax.*`).
3. **Netflix DGS upgrade**: The current DGS 4.9.21 may need upgrading for Spring Boot 3.x compatibility. The deprecated `onException` method will need to be migrated to `handleException`.
4. **JDK upgrade**: Spring Boot 3.x requires JDK 17+. Current project targets JDK 11.

## Conclusion

The upgrade from Spring Boot 2.6.3 to 2.7.18 was **clean and seamless**. No code changes were required — only the plugin versions in `build.gradle` were updated. All 68 tests pass, compilation succeeds, and all dependencies remain compatible. The deprecation warnings identified are expected and will be addressed in subsequent migration phases.
