package io.spring;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IntegrationTest {

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  void testFullRoundTrip() {
    // 1. Create user
    Map<String, Object> userParam = new HashMap<>();
    userParam.put("email", "integration" + System.currentTimeMillis() + "@test.com");
    userParam.put("username", "integrationuser" + System.currentTimeMillis());
    userParam.put("password", "password123");
    Map<String, Object> userBody = new HashMap<>();
    userBody.put("user", userParam);

    String token =
        given()
            .contentType(ContentType.JSON)
            .body(userBody)
            .when()
            .post("/users")
            .then()
            .statusCode(201)
            .extract()
            .path("user.token");

    org.assertj.core.api.Assertions.assertThat(token).isNotNull();

    // 2. Login
    Map<String, Object> loginParam = new HashMap<>();
    loginParam.put("email", userParam.get("email"));
    loginParam.put("password", "password123");
    Map<String, Object> loginBody = new HashMap<>();
    loginBody.put("user", loginParam);

    String loginToken =
        given()
            .contentType(ContentType.JSON)
            .body(loginBody)
            .when()
            .post("/users/login")
            .then()
            .statusCode(200)
            .extract()
            .path("user.token");

    org.assertj.core.api.Assertions.assertThat(loginToken).isNotNull();

    // 3. Create article
    Map<String, Object> articleParam = new HashMap<>();
    articleParam.put("title", "Integration Test Article " + System.currentTimeMillis());
    articleParam.put("description", "test description");
    articleParam.put("body", "test body");
    articleParam.put("tagList", java.util.Arrays.asList("integration", "test"));
    Map<String, Object> articleBody = new HashMap<>();
    articleBody.put("article", articleParam);

    String slug =
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Token " + loginToken)
            .body(articleBody)
            .when()
            .post("/articles")
            .then()
            .statusCode(200)
            .extract()
            .path("article.slug");

    org.assertj.core.api.Assertions.assertThat(slug).isNotNull();

    // 4. Fetch article by slug
    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/articles/{slug}", slug)
        .then()
        .statusCode(200);
  }
}
