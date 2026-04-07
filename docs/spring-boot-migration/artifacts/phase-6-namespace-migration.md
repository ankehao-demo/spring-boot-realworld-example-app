# Phase 6: Namespace Migration (javax → jakarta)

## Objective
Migrate all `javax.*` imports to `jakarta.*` namespace in preparation for Spring Boot 3.x, which requires Jakarta EE 9+.

## OpenRewrite Attempt

### First Attempt — OpenRewrite 6.8.0 (FAILED)
- **Plugin version**: `org.openrewrite.rewrite` 6.8.0
- **Recipe version**: `rewrite-migrate-java` 2.11.0
- **Recipe**: `org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta`
- **Result**: FAILED

**Error log**:
```
> Task :rewriteRun FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':rewriteRun'.
> class org.openrewrite.gradle.marker.GradleProjectBuilder tried to access private method
  'java.util.List org.openrewrite.gradle.marker.GradleDependencyConfiguration.resolveTransitiveDependencies(
  java.util.List, java.util.Set)' (org.openrewrite.gradle.marker.GradleProjectBuilder and
  org.openrewrite.gradle.marker.GradleDependencyConfiguration are in unnamed module of loader
  org.openrewrite.gradle.RewriteClassLoader @419a727a)

BUILD FAILED in 1m 19s
```

### Second Attempt — OpenRewrite 6.3.0 (SUCCESS)
- **Plugin version**: `org.openrewrite.rewrite` 6.3.0
- **Recipe version**: `rewrite-migrate-java` 2.5.0
- **Recipe**: `org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta`
- **Result**: SUCCESS

**Full output log**:
```
Validating active recipes
Scanning sources in project spring-boot-realworld-example-app
All sources parsed, running active recipes: org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta

Changes have been made to src/main/java/io/spring/graphql/exception/GraphQLCustomizeExceptionHandler.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/graphql/UserMutation.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/api/CurrentUserApi.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/api/CommentsApi.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/api/exception/CustomizeExceptionHandler.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/api/ArticlesApi.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/api/UsersApi.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/api/security/JwtTokenFilter.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxServletToJakartaServlet
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.servlet, newPackageName=jakarta.servlet, recursive=true}

Changes have been made to src/main/java/io/spring/api/ArticleApi.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/user/UserService.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/user/UpdateUserParam.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/user/DuplicatedEmailConstraint.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/user/DuplicatedUsernameConstraint.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/user/RegisterParam.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/user/DuplicatedEmailValidator.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/user/DuplicatedUsernameValidator.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/article/DuplicatedArticleValidator.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/article/DuplicatedArticleConstraint.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/article/ArticleCommandService.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to src/main/java/io/spring/application/article/NewArticleParam.java by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.JavaxValidationMigrationToJakartaValidation
            org.openrewrite.java.ChangePackage: {oldPackageName=javax.validation, newPackageName=jakarta.validation, recursive=true}

Changes have been made to build.gradle by:
    org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta
        org.openrewrite.java.migrate.jakarta.RestAssuredJavaxToJakarta
            org.openrewrite.java.dependencies.UpgradeDependencyVersion: {groupId=io.rest-assured, artifactId=*, newVersion=5.1.x}

BUILD SUCCESSFUL in 53s
```

## Files Modified (21 files)

### API Layer (7 files)
| File | Namespace Replacements |
|------|----------------------|
| `src/main/java/io/spring/api/ArticleApi.java` | `javax.validation.Valid` → `jakarta.validation.Valid` |
| `src/main/java/io/spring/api/ArticlesApi.java` | `javax.validation.Valid` → `jakarta.validation.Valid` |
| `src/main/java/io/spring/api/CommentsApi.java` | `javax.validation.Valid` → `jakarta.validation.Valid`, `javax.validation.constraints.NotBlank` → `jakarta.validation.constraints.NotBlank` |
| `src/main/java/io/spring/api/CurrentUserApi.java` | `javax.validation.Valid` → `jakarta.validation.Valid` |
| `src/main/java/io/spring/api/UsersApi.java` | `javax.validation.Valid` → `jakarta.validation.Valid`, `javax.validation.constraints.Email` → `jakarta.validation.constraints.Email`, `javax.validation.constraints.NotBlank` → `jakarta.validation.constraints.NotBlank` |
| `src/main/java/io/spring/api/security/JwtTokenFilter.java` | `javax.servlet.FilterChain` → `jakarta.servlet.FilterChain`, `javax.servlet.ServletException` → `jakarta.servlet.ServletException`, `javax.servlet.http.HttpServletRequest` → `jakarta.servlet.http.HttpServletRequest`, `javax.servlet.http.HttpServletResponse` → `jakarta.servlet.http.HttpServletResponse` |
| `src/main/java/io/spring/api/exception/CustomizeExceptionHandler.java` | `javax.validation.ConstraintViolation` → `jakarta.validation.ConstraintViolation`, `javax.validation.ConstraintViolationException` → `jakarta.validation.ConstraintViolationException` |

### Application Layer (10 files)
| File | Namespace Replacements |
|------|----------------------|
| `src/main/java/io/spring/application/article/ArticleCommandService.java` | `javax.validation.Valid` → `jakarta.validation.Valid` |
| `src/main/java/io/spring/application/article/NewArticleParam.java` | `javax.validation.constraints.NotBlank` → `jakarta.validation.constraints.NotBlank` |
| `src/main/java/io/spring/application/article/DuplicatedArticleValidator.java` | `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext` |
| `src/main/java/io/spring/application/article/DuplicatedArticleConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `src/main/java/io/spring/application/user/RegisterParam.java` | `javax.validation.constraints.Email` → `jakarta.validation.constraints.Email`, `javax.validation.constraints.NotBlank` → `jakarta.validation.constraints.NotBlank` |
| `src/main/java/io/spring/application/user/DuplicatedUsernameConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `src/main/java/io/spring/application/user/DuplicatedUsernameValidator.java` | `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext` |
| `src/main/java/io/spring/application/user/DuplicatedEmailConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `src/main/java/io/spring/application/user/DuplicatedEmailValidator.java` | `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext` |
| `src/main/java/io/spring/application/user/UpdateUserParam.java` | `javax.validation.constraints.Email` → `jakarta.validation.constraints.Email` |
| `src/main/java/io/spring/application/user/UserService.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext`, `javax.validation.Valid` → `jakarta.validation.Valid` |

### GraphQL Layer (2 files)
| File | Namespace Replacements |
|------|----------------------|
| `src/main/java/io/spring/graphql/UserMutation.java` | `javax.validation.ConstraintViolationException` → `jakarta.validation.ConstraintViolationException` |
| `src/main/java/io/spring/graphql/exception/GraphQLCustomizeExceptionHandler.java` | `javax.validation.ConstraintViolation` → `jakarta.validation.ConstraintViolation`, `javax.validation.ConstraintViolationException` → `jakarta.validation.ConstraintViolationException` |

### Build Configuration (1 file)
| File | Change |
|------|--------|
| `build.gradle` | rest-assured upgraded from 4.5.1 → 5.1.1 (Jakarta namespace support) |

## Namespace Replacements Summary

| Old Namespace | New Namespace | Occurrences |
|---------------|---------------|-------------|
| `javax.validation.*` | `jakarta.validation.*` | 34 import statements across 19 files |
| `javax.servlet.*` | `jakarta.servlet.*` | 4 import statements in 1 file |
| **Total** | | **38 import statements across 20 Java files** |

## Packages NOT Migrated (JDK-Provided — Intentionally Kept)

| Package | File | Reason |
|---------|------|--------|
| `javax.crypto.SecretKey` | `DefaultJwtService.java` | JDK-provided package (part of `java.base` module) |
| `javax.crypto.spec.SecretKeySpec` | `DefaultJwtService.java` | JDK-provided package (part of `java.base` module) |

## Verification Grep Output

```bash
$ grep -rn "import javax\." src/ --include="*.java" | grep -v "javax\.crypto" | grep -v "javax\.sql" | grep -v "javax\.net"
(zero results — all javax EE imports migrated)
```

## Compilation Attempt

```bash
$ ./gradlew clean compileJava
```

**Result**: BUILD FAILED — 82 compilation errors

### Error Summary
All 82 errors are of the same category: **`jakarta.*` packages not found on the classpath**. This is expected because the project is still on Spring Boot 2.6.3, which ships with `javax.*` packages. The `jakarta.*` packages will become available when Spring Boot is upgraded to 3.x in a later phase.

**Error categories**:
- `package jakarta.validation does not exist` — 18 errors
- `cannot find symbol` (for `Valid`, `NotBlank`, `Email`, `Constraint`, `Payload`, `ConstraintValidator`, `ConstraintValidatorContext`, `ConstraintViolation`, `ConstraintViolationException`) — 53 errors
- `package jakarta.servlet does not exist` — 3 errors
- `cannot find symbol` (for `FilterChain`, `HttpServletRequest`, `HttpServletResponse`, `ServletException`) — 8 errors

**Total**: 82 errors across 20 files

These errors will be resolved when:
1. Spring Boot is upgraded to 3.x (which transitively provides Jakarta EE 9+ dependencies)
2. Or explicit `jakarta.servlet-api` and `jakarta.validation-api` dependencies are added

## Tool Used
- **OpenRewrite** v6.3.0 with `rewrite-migrate-java` v2.5.0
- Recipe: `org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta`
- OpenRewrite handled 100% of the migration (no manual changes needed)
- OpenRewrite plugin was removed from `build.gradle` after migration
