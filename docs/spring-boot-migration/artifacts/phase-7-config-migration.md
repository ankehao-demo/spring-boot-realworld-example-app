# Phase 7: Configuration Migration — Spring Boot 3.x

## 1. Properties Migrator Output

### Setup
Added temporary dependency to `build.gradle`:
```gradle
runtimeOnly 'org.springframework.boot:spring-boot-properties-migrator'
```

### Execution
```bash
./gradlew bootRun --args='--server.port=8099'
```

### Full Output Log
```
> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.6.3)

2026-04-07 14:43:45.386  INFO 10282 --- [           main] io.spring.RealWorldApplication           : Starting RealWorldApplication using Java 11.0.30 on devin-box with PID 10282
2026-04-07 14:43:45.389  INFO 10282 --- [           main] io.spring.RealWorldApplication           : No active profile set, falling back to default profiles: default
2026-04-07 14:43:46.817  INFO 10282 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8099 (http)
2026-04-07 14:43:46.825  INFO 10282 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-04-07 14:43:46.826  INFO 10282 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.56]
2026-04-07 14:43:46.907  INFO 10282 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-04-07 14:43:46.908  INFO 10282 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1345 ms
2026-04-07 14:43:47.568  INFO 10282 --- [           main] .s.s.UserDetailsServiceAutoConfiguration : Using generated security password: 512dda96-520b-445b-a769-39904c9ee4d7
2026-04-07 14:43:47.627  INFO 10282 --- [           main] o.s.s.web.DefaultSecurityFilterChain     : Will secure any request with [...]
2026-04-07 14:43:47.808  INFO 10282 --- [           main] c.n.g.d.w.a.GraphiQLConfigurer           : Configuring GraphiQL to use GraphQL endpoint at '/graphql'
2026-04-07 14:43:48.342  INFO 10282 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-04-07 14:43:48.412  INFO 10282 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-04-07 14:43:48.424  INFO 10282 --- [           main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 8.0.5 by Redgate
2026-04-07 14:43:48.425  INFO 10282 --- [           main] o.f.c.i.database.base.BaseDatabaseType   : Database: jdbc:sqlite:dev.db (SQLite 3.36)
2026-04-07 14:43:48.448  INFO 10282 --- [           main] o.f.core.internal.command.DbValidate     : Successfully validated 1 migration (execution time 00:00.009s)
2026-04-07 14:43:48.450  INFO 10282 --- [           main] o.f.core.internal.command.DbMigrate      : Current version of schema "main": 1
2026-04-07 14:43:48.450  INFO 10282 --- [           main] o.f.core.internal.command.DbMigrate      : Schema "main" is up to date. No migration necessary.
2026-04-07 14:43:48.541  INFO 10282 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8099 (http) with context path ''
2026-04-07 14:43:48.553  INFO 10282 --- [           main] io.spring.RealWorldApplication           : Started RealWorldApplication in 3.577 seconds (JVM running for 3.943)
```

### Result
**No deprecated property warnings were emitted by the Properties Migrator.** The application started successfully. This means none of the current properties in `application.properties` have been renamed or removed within the Spring Boot 2.x line. The migrator dependency was removed after capturing this output.

---

## 2. Properties Changed (Old Name → New Name)

### application.properties Analysis

| Property | Current Value | SB3 Status | Action Required |
|----------|--------------|------------|-----------------|
| `spring.datasource.url` | `jdbc:sqlite:dev.db` | **Valid** — No change | None |
| `spring.datasource.driver-class-name` | `org.sqlite.JDBC` | **Valid** — No change | None |
| `spring.datasource.username` | (empty) | **Valid** — No change | None |
| `spring.datasource.password` | (empty) | **Valid** — No change | None |
| `spring.jackson.deserialization.UNWRAP_ROOT_VALUE` | `true` | **Valid** — No change | None |
| `image.default` | URL | **Valid** — Custom property | None |
| `jwt.secret` | Base64 key | **Valid** — Custom property | None |
| `jwt.sessionTime` | `86400` | **Valid** — Custom property | None |
| `mybatis.configuration.cache-enabled` | `true` | **Valid** — Same in mybatis-spring-boot-starter 3.x | None |
| `mybatis.configuration.default-statement-timeout` | `3000` | **Valid** — Same in mybatis-spring-boot-starter 3.x | None |
| `mybatis.configuration.map-underscore-to-camel-case` | `true` | **Valid** — Same in mybatis-spring-boot-starter 3.x | None |
| `mybatis.configuration.use-generated-keys` | `true` | **Valid** — Same in mybatis-spring-boot-starter 3.x | None |
| `mybatis.type-handlers-package` | `io.spring.infrastructure.mybatis` | **Valid** — Same in mybatis-spring-boot-starter 3.x | None |
| `mybatis.mapper-locations` | `mapper/*.xml` | **Valid** — Same in mybatis-spring-boot-starter 3.x | None |
| `logging.level.io.spring.infrastructure.mybatis.readservice.ArticleReadService` | `DEBUG` | **Valid** — No change | None |
| `logging.level.io.spring.infrastructure.mybatis.mapper` | `DEBUG` | **Valid** — No change | None |

### application-test.properties Analysis

| Property | Current Value | SB3 Status | Action Required |
|----------|--------------|------------|-----------------|
| `spring.datasource.url` | `jdbc:sqlite::memory:` | **Valid** — No change | None |

### Summary
No property names have changed between Spring Boot 2.6.3 and Spring Boot 3.x for the properties used in this application. All `spring.datasource.*`, `spring.jackson.*`, `mybatis.*`, and `logging.level.*` properties retain their names and semantics.

**Note:** Some commonly renamed SB3 properties (e.g., `spring.redis.*` → `spring.data.redis.*`, `spring.elasticsearch.*` → `spring.elasticsearch.uris`) do not apply to this project since it does not use Redis, Elasticsearch, or other affected subsystems.

---

## 3. MyBatis Configuration Changes

### mybatis-config.xml
**No `mybatis-config.xml` file exists** in this project. All MyBatis configuration is done via `application.properties` using the `mybatis.*` prefix, which is the recommended approach for Spring Boot applications.

### MyBatis Starter Version Change
- **Current:** `mybatis-spring-boot-starter:2.2.2` (for Spring Boot 2.x)
- **Target:** `mybatis-spring-boot-starter:3.0.3` (for Spring Boot 3.x)

The mybatis-spring-boot-starter 3.0.x is the version compatible with Spring Boot 3.x. Key changes:
- Transitive dependency on MyBatis 3.5.13+ (no breaking config changes)
- Transitive dependency on mybatis-spring 3.0.x (Jakarta namespace support)
- All `mybatis.*` configuration property names remain unchanged
- MyBatis mapper XML DTD (`mybatis-3-mapper.dtd`) remains the same

### MyBatis Mapper XML Files
All 11 mapper XML files use the standard MyBatis 3.0 DTD:
```xml
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
```
This DTD reference remains valid with MyBatis 3.5.x. No changes needed to any mapper XML files.

### MyBatis Type Handler
The custom `DateTimeHandler` (in `io.spring.infrastructure.mybatis`) uses Joda-Time `DateTime`. It is registered via the `mybatis.type-handlers-package` property. The handler itself uses standard `org.apache.ibatis.type.*` interfaces which haven't changed. However, the Joda-Time dependency is a separate migration concern (Phase 0 scope).

### MyBatis Test Starter
- **Current:** `mybatis-spring-boot-starter-test:2.2.2`
- **Target:** `mybatis-spring-boot-starter-test:3.0.3`

---

## 4. Flyway Configuration Changes

### Current State
- Flyway is included via `org.flywaydb:flyway-core` (managed by Spring Boot BOM)
- Migration files are at the default path: `src/main/resources/db/migration/`
- Single migration: `V1__create_tables.sql`
- No explicit Flyway properties in `application.properties`

### Spring Boot 3.x Changes for Flyway

#### Flyway Version Upgrade
- **SB 2.6.3** ships with Flyway 8.0.5
- **SB 3.2.x** ships with Flyway 9.22.x / 10.x
- Flyway 10.x restructured database-specific support into separate modules

#### SQLite Module Requirement
Starting with Flyway 10.x (shipped with Spring Boot 3.2+), SQLite support is no longer bundled in `flyway-core`. A separate module is required:

```gradle
implementation 'org.flywaydb:flyway-database-sqlite'
```

This dependency must be added to `build.gradle` for Flyway to recognize the SQLite database type. Without it, the application will fail at startup with an error like:
```
org.flywaydb.core.api.FlywayException: No database found to handle jdbc:sqlite:dev.db
```

#### Migration Path
The default migration path `classpath:db/migration` remains unchanged in SB3. No additional Flyway properties are needed.

#### Migration Script Compatibility
The `V1__create_tables.sql` script uses standard SQL DDL (CREATE TABLE) that is compatible with SQLite across all Flyway versions. No migration script changes required.

### Configuration Added
Added to `application.properties`:
```properties
# Flyway — explicitly set default location for clarity during SB3 migration
spring.flyway.enabled=true
```

---

## 5. Auto-Configuration Changes

### spring.factories → AutoConfiguration.imports

Spring Boot 3.x deprecated `META-INF/spring.factories` for auto-configuration registration in favor of `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

**This project does NOT define any custom auto-configuration classes.** There is:
- No `META-INF/spring.factories` file
- No `META-INF/spring/` directory
- No classes annotated with `@AutoConfiguration`

Therefore, **no auto-configuration migration is needed**.

### Other Auto-Configuration Notes
- The project uses standard Spring Boot auto-configuration for web, security, datasource, and Flyway
- All these auto-configurations are handled internally by Spring Boot 3.x and require no user-side changes
- The `@SpringBootApplication` annotation on `RealWorldApplication` continues to trigger component scanning and auto-configuration as before

---

## 6. Summary of Required Configuration Changes

### Changes Made in This Phase

1. **`application.properties`** — Added `spring.flyway.enabled=true` for explicit Flyway enablement
2. **`build.gradle`** (noted for future phases) — Will need:
   - `implementation 'org.flywaydb:flyway-database-sqlite'` when upgrading to SB 3.2+

### No Changes Needed
- All existing `spring.datasource.*` properties are valid
- All existing `spring.jackson.*` properties are valid
- All existing `mybatis.*` properties are valid
- All existing `logging.level.*` properties are valid
- All custom properties (`jwt.*`, `image.default`) are valid
- No `mybatis-config.xml` exists (and none needed)
- No auto-configuration migration needed
- No mapper XML changes needed
- Migration SQL scripts are compatible

### Dependencies to Update (in build phase, not config phase)
These dependency version changes will be needed when the full SB3 migration is applied:
- `mybatis-spring-boot-starter` 2.2.2 → 3.0.3
- `mybatis-spring-boot-starter-test` 2.2.2 → 3.0.3
- Add `flyway-database-sqlite` module (new dependency for Flyway 10.x)
