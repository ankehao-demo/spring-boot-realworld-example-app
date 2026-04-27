# Testing: spring-boot-realworld-example-app

## Overview
This is an API-only backend implementing the RealWorld spec. No browser UI — all testing is done via curl/shell commands against REST and GraphQL endpoints.

## Devin Secrets Needed
None. The app uses an embedded SQLite database and a hardcoded JWT secret for local development.

## Local Dev Setup

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Build and run tests
./gradlew clean build

# Start the app (port 8080)
./gradlew bootRun
```

- The app runs on **port 8080** (default Spring Boot, no custom `server.port` configured).
- SQLite database file is `dev.db` in the project root. Delete it before `bootRun` for a clean state.
- Flyway auto-migrates the schema on startup.

## Key REST Endpoints

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| POST | `/users` | No | Register user (body: `{"user": {"username","email","password"}}`) |
| POST | `/users/login` | No | Login (body: `{"user": {"email","password"}}`) |
| GET | `/user` | JWT | Get current user profile |
| GET | `/articles` | No | List articles |
| POST | `/articles` | JWT | Create article (body: `{"article": {"title","description","body","tagList"}}`) |
| GET | `/articles/{slug}` | No | Get article by slug |
| GET | `/tags` | No | List all tags |
| GET | `/profiles/{username}` | No | Get user profile |
| POST | `/articles/{slug}/comments` | JWT | Add comment |
| POST | `/articles/{slug}/favorite` | JWT | Favorite article |

### Authentication
JWT tokens are returned in the `user.token` field of register/login responses. Pass them as:
```
Authorization: Token <jwt_token>
```
Request bodies use root-level wrapping (e.g. `{"user": {...}}`, `{"article": {...}}`).

## GraphQL

The GraphQL endpoint is at `/graphql` (no auth required for queries, auth required for mutations that modify user data).

Example queries:
```bash
# Query tags
curl -s -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ tags }"}'

# Query article
curl -s -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ article(slug: \"my-slug\") { title body author { username } } }"}'

# Mutation: create user
curl -s -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "mutation { createUser(input: {username: \"user1\", email: \"u@t.com\", password: \"pass\"}) { ... on UserPayload { user { token username } } } }"}'
```

GraphiQL UI may be available at `/graphiql`.

## Testing Strategy

Since this is API-only, testing is shell-based (no recording needed):

1. **Start app** with `./gradlew bootRun` (clean `dev.db` first)
2. **Register a user** via `POST /users`
3. **Login** via `POST /users/login`, extract JWT token
4. **Test authenticated endpoints** (create article, get current user)
5. **Test public endpoints** (get articles, tags, profiles)
6. **Test GraphQL** queries and mutations at `/graphql`

## Known Issues / Dependency Notes

- **DGS + graphql-java version conflict**: When upgrading DGS, Spring Boot's BOM may force an incompatible graphql-java version. If you see `NoSuchMethodError` at `Federation.java`, check the graphql-java version in `./gradlew dependencies` and consider adding a `resolutionStrategy` to force the correct version.
- **Spotless + Java 17**: If using `googleJavaFormat()`, ensure the version is at least `1.15.0` for Java 17 compatibility (e.g. `googleJavaFormat('1.15.0')`).
- **WebSecurityConfigurerAdapter**: The app uses the deprecated `WebSecurityConfigurerAdapter` which still works in Spring Boot 2.7.x but will need migration if upgrading to Spring Boot 3.x.
