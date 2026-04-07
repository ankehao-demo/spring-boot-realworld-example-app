# Phase 5: Spring Security Migration

## Objective
Migrate Spring Security configuration from the deprecated `WebSecurityConfigurerAdapter` pattern to the modern `SecurityFilterChain` bean pattern required by Spring Security 6.x (Spring Boot 3.x).

## Files Modified

### 1. `src/main/java/io/spring/api/security/WebSecurityConfig.java`

#### Before (Spring Security 5.x / Spring Boot 2.x)
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .cors()
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS)
        .permitAll()
        .antMatchers("/graphiql")
        .permitAll()
        .antMatchers("/graphql")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/articles/feed")
        .authenticated()
        .antMatchers(HttpMethod.POST, "/users", "/users/login")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/articles/**", "/profiles/**", "/tags")
        .permitAll()
        .anyRequest()
        .authenticated();

    http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
```

#### After (Spring Security 6.x / Spring Boot 3.x)
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .exceptionHandling(
            ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS)
                    .permitAll()
                    .requestMatchers("/graphiql")
                    .permitAll()
                    .requestMatchers("/graphql")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/articles/feed")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/users", "/users/login")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/articles/**", "/profiles/**", "/tags")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
```

### 2. `src/main/java/io/spring/api/security/JwtTokenFilter.java`

**No changes required for this phase.** The filter extends `OncePerRequestFilter`, which remains the correct base class in Spring Security 6.x. The filter logic (JWT extraction, validation, and `SecurityContextHolder` population) is unchanged.

**Note for Phase 6 (javax → jakarta):** The `javax.servlet.*` imports in `JwtTokenFilter.java` will need to be updated to `jakarta.servlet.*` as part of the namespace migration in Phase 6. This is intentionally deferred per the phased migration plan.

## API Changes Applied

| Old API (Spring Security 5.x) | New API (Spring Security 6.x) | Notes |
|---|---|---|
| `extends WebSecurityConfigurerAdapter` | Removed (standalone `@Configuration` class) | Adapter class removed in Spring Security 6 |
| `@Override configure(HttpSecurity)` | `@Bean SecurityFilterChain securityFilterChain(HttpSecurity)` | Returns `SecurityFilterChain` via `http.build()` |
| `http.csrf().disable()` | `http.csrf(csrf -> csrf.disable())` | Lambda DSL replaces `.and()` chaining |
| `http.cors()` | `http.cors(Customizer.withDefaults())` | Explicit `Customizer.withDefaults()` for default CORS |
| `.exceptionHandling().authenticationEntryPoint(...)` | `.exceptionHandling(ex -> ex.authenticationEntryPoint(...))` | Lambda DSL |
| `.sessionManagement().sessionCreationPolicy(...)` | `.sessionManagement(session -> session.sessionCreationPolicy(...))` | Lambda DSL |
| `.authorizeRequests()` | `.authorizeHttpRequests()` | New authorization API |
| `.antMatchers(...)` | `.requestMatchers(...)` | Renamed in Spring Security 6 |
| `.and()` chaining | Lambda DSL closures | `.and()` deprecated; lambda DSL is the recommended pattern |

## Import Changes (WebSecurityConfig.java)

| Removed Import | Added Import |
|---|---|
| `o.s.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter` | `o.s.security.config.Customizer` |
| — | `o.s.security.web.SecurityFilterChain` |

## CORS Configuration
The CORS configuration is preserved unchanged. The `CorsConfigurationSource` bean continues to be picked up by Spring Security via `Customizer.withDefaults()`, which delegates to the existing `corsConfigurationSource()` bean. All allowed origins, methods, headers, and credentials settings remain identical.

## Beans Preserved
- `JwtTokenFilter jwtTokenFilter()` — unchanged
- `PasswordEncoder passwordEncoder()` — unchanged (BCryptPasswordEncoder)
- `CorsConfigurationSource corsConfigurationSource()` — unchanged

## Issues Encountered
- **Phase 4 (Dependency Compatibility) not yet merged to master**: The current `build.gradle` still references Spring Boot 2.6.3 and Spring Security 5.x. The `requestMatchers()` and `authorizeHttpRequests()` APIs used in the refactored code are available in Spring Security 6.x (Spring Boot 3.x). Until the Spring Boot version is upgraded, the build will fail to compile these new APIs. This is expected per the phased migration plan.
- **javax.servlet imports in JwtTokenFilter.java**: These will need to be migrated to `jakarta.servlet` in Phase 6 (javax → jakarta namespace migration). Not addressed in this phase per the migration plan.

## Verification
This phase focuses solely on the security configuration pattern migration. Full compilation and test verification will be possible after:
1. Phase 4 (Dependency Compatibility) upgrades Spring Boot to 3.x
2. Phase 6 (javax → jakarta) migrates servlet namespace imports
