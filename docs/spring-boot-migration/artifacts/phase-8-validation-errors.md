# Phase 8 Session 3: Validation & Error Handling Adaptation

## Migration Target
- **From**: Spring Boot 2.6.3 / Java 11 / `javax.validation`
- **To**: Spring Boot 3.3.5 / Java 17 / `jakarta.validation`

---

## Conditional Detection Results

### 1. Jakarta Validation API Changes — Custom Constraint Annotations

**Detection**: CHANGE REQUIRED

All custom constraint annotations use `@Constraint(validatedBy = ...)` which is structurally identical in Jakarta Validation. The `validatedBy` attribute, `message()`, `groups()`, and `payload()` members are unchanged. The only required change is the import namespace: `javax.validation.Constraint` → `jakarta.validation.Constraint` and `javax.validation.Payload` → `jakarta.validation.Payload`.

**Affected files**:
| File | Change |
|------|--------|
| `DuplicatedArticleConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `DuplicatedUsernameConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `DuplicatedEmailConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `UserService.java` (`UpdateUserConstraint`) | `javax.validation.Constraint` → `jakarta.validation.Constraint` |

### 2. Custom Validator Implementations — `ConstraintValidator<A, T>` Interface

**Detection**: CHANGE REQUIRED (imports only)

The `ConstraintValidator<A, T>` interface is identical in Jakarta Validation. The `isValid(T value, ConstraintValidatorContext context)` method signature is unchanged. The `ConstraintValidatorContext` API (including `disableDefaultConstraintViolation()`, `buildConstraintViolationWithTemplate()`, `addPropertyNode()`, `addConstraintViolation()`) is fully compatible.

**Affected files**:
| File | Change |
|------|--------|
| `DuplicatedArticleValidator.java` | `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext` |
| `DuplicatedUsernameValidator.java` | Same import changes |
| `DuplicatedEmailValidator.java` | Same import changes |
| `UserService.java` (`UpdateUserValidator`) | `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext` |

### 3. `@Valid`, `@NotBlank`, `@Email` Behavior Changes

**Detection**: CHANGE REQUIRED (imports only)

All standard Bean Validation annotations have moved from `javax.validation` → `jakarta.validation` namespace. Their behavior and error message formats are unchanged between Hibernate Validator 6.x (javax) and 8.x (jakarta).

**Affected files**:
| File | Annotations | Change |
|------|-------------|--------|
| `NewArticleParam.java` | `@NotBlank` | `javax.validation.constraints.NotBlank` → `jakarta.validation.constraints.NotBlank` |
| `RegisterParam.java` | `@NotBlank`, `@Email` | `javax.validation.constraints.Email` → `jakarta.validation.constraints.Email`, `javax.validation.constraints.NotBlank` → `jakarta.validation.constraints.NotBlank` |
| `UpdateUserParam.java` | `@Email` | `javax.validation.constraints.Email` → `jakarta.validation.constraints.Email` |
| `ArticleCommandService.java` | `@Valid` | `javax.validation.Valid` → `jakarta.validation.Valid` |
| `UserService.java` | `@Valid` | `javax.validation.Valid` → `jakarta.validation.Valid` |

### 4. `ConstraintViolationException` Handling

**Detection**: CHANGE REQUIRED (out of scope for this session)

`ConstraintViolationException` is used in three files outside validation scope:
- `api/exception/CustomizeExceptionHandler.java` — REST exception handler (Session 1 scope)
- `graphql/UserMutation.java` — GraphQL mutation (Session 2 scope)
- `graphql/exception/GraphQLCustomizeExceptionHandler.java` — GraphQL exception handler (Session 2 scope)

The `ConstraintViolationException` API is identical in Jakarta Validation. The only change needed is the import namespace: `javax.validation.ConstraintViolationException` → `jakarta.validation.ConstraintViolationException` and `javax.validation.ConstraintViolation` → `jakarta.validation.ConstraintViolation`.

**Additional note for Session 1**: The `CustomizeExceptionHandler.handleMethodArgumentNotValid()` override has a signature change in Spring Boot 3.x. The `HttpStatus` parameter becomes `HttpStatusCode` and the return type must use `HttpHeaders` from `org.springframework.http`.

### 5. `UserService.java` Validation Integration

**Detection**: CHANGE REQUIRED (completed)

`UserService.java` is annotated with `@Validated` (from `org.springframework.validation.annotation`) which does NOT change in Spring Boot 3.x — it's a Spring annotation, not a Jakarta annotation. The `@Valid` annotation on method parameters has been migrated to `jakarta.validation.Valid`.

The file also contains inline classes:
- **`UpdateUserConstraint`** annotation — migrated `@Constraint` import to `jakarta.validation`
- **`UpdateUserValidator`** class — migrated `ConstraintValidator` and `ConstraintValidatorContext` imports to `jakarta.validation`

The `ConstraintValidatorContext` API used in `UpdateUserValidator.isValid()` (specifically `disableDefaultConstraintViolation()`, `buildConstraintViolationWithTemplate()`, `addPropertyNode()`, `addConstraintViolation()`) is fully compatible with Jakarta Validation.

**Note**: `UserService.java` line 65 and 67 use raw `Class[]` types for `groups()` and `payload()` in `UpdateUserConstraint`. While this compiles, the standard pattern uses `Class<?>[]` and `Class<? extends Payload>[]` respectively. This is a pre-existing code style issue, not a migration issue.

---

## Summary of Changes

### Files Modified (11 files)

| File | Changes Made |
|------|-------------|
| `application/article/DuplicatedArticleConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `application/article/DuplicatedArticleValidator.java` | `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext` |
| `application/article/ArticleCommandService.java` | `javax.validation.Valid` → `jakarta.validation.Valid` |
| `application/article/NewArticleParam.java` | `javax.validation.constraints.NotBlank` → `jakarta.validation.constraints.NotBlank` |
| `application/user/DuplicatedUsernameConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `application/user/DuplicatedUsernameValidator.java` | `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext` |
| `application/user/DuplicatedEmailConstraint.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.Payload` → `jakarta.validation.Payload` |
| `application/user/DuplicatedEmailValidator.java` | `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext` |
| `application/user/RegisterParam.java` | `javax.validation.constraints.Email` → `jakarta.validation.constraints.Email`, `javax.validation.constraints.NotBlank` → `jakarta.validation.constraints.NotBlank` |
| `application/user/UpdateUserParam.java` | `javax.validation.constraints.Email` → `jakarta.validation.constraints.Email` |
| `application/user/UserService.java` | `javax.validation.Constraint` → `jakarta.validation.Constraint`, `javax.validation.ConstraintValidator` → `jakarta.validation.ConstraintValidator`, `javax.validation.ConstraintValidatorContext` → `jakarta.validation.ConstraintValidatorContext`, `javax.validation.Valid` → `jakarta.validation.Valid` |

### Build Configuration Changes

| File | Change | Reason |
|------|--------|--------|
| `build.gradle` | Spring Boot 2.6.3 → 3.3.5 | Required for Jakarta Validation (jakarta.validation.*) |
| `build.gradle` | Java 11 → Java 17 | Spring Boot 3.x minimum requirement |
| `build.gradle` | Dependency version upgrades | Compatible versions for Spring Boot 3.x |
| `gradle-wrapper.properties` | Gradle 7.4 → 8.5 | Required for Spring Boot 3.3.5 plugin |

### Compilation Status

- **Validation files (in scope)**: 0 errors — all 11 files compile successfully
- **Other files (out of scope)**: Remaining errors are in files handled by parallel sessions:
  - `api/` controllers: `javax.validation` imports (Session 1)
  - `graphql/` files: `javax.validation` imports (Session 2)
  - Joda-Time usages: `org.joda.time` removed (separate migration concern)
  - `CustomizeExceptionHandler`: `handleMethodArgumentNotValid` signature change (Session 1)

---

## Jakarta Validation API Differences Found

| Aspect | javax.validation (Bean Validation 2.0) | jakarta.validation (Bean Validation 3.0) | Impact |
|--------|---------------------------------------|------------------------------------------|--------|
| Package namespace | `javax.validation.*` | `jakarta.validation.*` | Import changes only |
| `@Constraint` annotation | Same | Same | No structural change |
| `ConstraintValidator` interface | Same | Same | No structural change |
| `isValid()` method signature | `boolean isValid(T, ConstraintValidatorContext)` | `boolean isValid(T, ConstraintValidatorContext)` | Identical |
| `ConstraintValidatorContext` API | `disableDefaultConstraintViolation()`, `buildConstraintViolationWithTemplate()`, `addPropertyNode()`, `addConstraintViolation()` | Same methods | Identical |
| `ConstraintViolationException` | Same | Same | Import change only |
| `@Valid`, `@NotBlank`, `@Email` | Same behavior | Same behavior | Import changes only |
| Default messages | Same | Same | No change |

**Conclusion**: The migration from `javax.validation` to `jakarta.validation` is purely a namespace change for all validation APIs used in this project. No behavioral or structural changes were needed beyond updating import statements.
