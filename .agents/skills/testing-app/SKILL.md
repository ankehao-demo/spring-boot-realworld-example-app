# Testing the Spring Boot RealWorld Example App

## Overview
This is a Spring Boot app implementing the RealWorld (Conduit) API spec with both REST and GraphQL (Netflix DGS) endpoints. It uses an in-memory H2/SQLite database by default.

## Prerequisites
- Java 17+ (OpenJDK recommended)
- No external database or services required — uses embedded DB

## Build & Run

```bash
# Build (includes running all tests)
./gradlew clean build

# Run tests only
./gradlew test

# Boot the app (runs on port 8080 by default)
./gradlew bootRun
```

The app starts on `http://localhost:8080`. No authentication/secrets are needed to boot.

## Key Endpoints

### REST API
| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| POST | `/users` | No | Register a new user |
| POST | `/users/login` | No | Login |
| GET | `/tags` | No | List all tags |
| GET | `/articles` | No | List articles |
| POST | `/articles` | Yes (Token) | Create an article |
| GET | `/articles/{slug}` | No | Get single article |

Authentication uses JWT tokens via `Authorization: Token <jwt>` header.

### GraphQL
- **Endpoint:** `POST /graphql` with `Content-Type: application/json`
- **GraphiQL IDE:** `GET /graphiql` (serves HTML page; may not render in headless/restricted browser environments due to external JS dependencies from unpkg.com)

### Key GraphQL Queries
```graphql
# Articles with pagination (tests PageInfo type)
{
  articles(first: 10) {
    pageInfo {
      hasNextPage
      hasPreviousPage
      startCursor
      endCursor
    }
    edges {
      cursor
      node {
        title
        slug
        tagList
      }
    }
  }
}

# Tags
{ tags }
```

## Testing Flow

1. **Boot the app:** `./gradlew bootRun`
2. **Register a user:**
   ```bash
   curl -X POST http://localhost:8080/users \
     -H "Content-Type: application/json" \
     -d '{"user":{"email":"test@test.com","username":"testuser","password":"password123"}}'
   ```
3. **Save the JWT token** from the response `user.token` field
4. **Create an article** (use the token):
   ```bash
   curl -X POST http://localhost:8080/articles \
     -H "Content-Type: application/json" \
     -H "Authorization: Token <jwt>" \
     -d '{"article":{"title":"Test","description":"Desc","body":"Body","tagList":["tag1"]}}'
   ```
5. **Verify REST:** `GET /tags`, `GET /articles`
6. **Verify GraphQL:** POST to `/graphql` with articles query above — check that `pageInfo` fields are present and correct

## Known Issues & Tips

- **GraphiQL blank page:** The `/graphiql` endpoint loads JS from `unpkg.com`. In restricted or headless browser environments, the page may appear blank. Use `curl` to verify it returns HTTP 200 with valid HTML instead.
- **DGS version constraints:** DGS 7.x+ requires Jakarta EE (Spring Boot 3.x). If on Spring Boot 2.7.x, stay with DGS 4.9.x using the BOM approach.
- **Spotless formatting:** Run `./gradlew spotlessApply` before committing if you modify Java files.
- **Gradle 8.5 strict validation:** Spotless config must target `src/` specifically (not project root) to avoid task dependency errors.
- **PageInfo type:** The app uses generated `io.spring.graphql.types.PageInfo` (not `graphql.relay.DefaultPageInfo`). When modifying datafetchers, use the generated type's builder pattern.

## Devin Secrets Needed
None — this app runs entirely locally with no external service dependencies.
