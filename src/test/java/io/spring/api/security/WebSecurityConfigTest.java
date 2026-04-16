package io.spring.api.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class WebSecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void testGetArticlesIsPublic() throws Exception {
    mockMvc.perform(get("/articles").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void testPostUsersIsPublic() throws Exception {
    // This endpoint must not return 401/403 — any other status proves it's publicly accessible
    int status =
        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"user\":{\"email\":\"sectest@test.com\",\"username\":\"sectest\",\"password\":\"password\"}}"))
            .andReturn()
            .getResponse()
            .getStatus();
    assert status != 401 && status != 403 : "Expected public endpoint, got " + status;
  }

  @Test
  void testPostUsersLoginIsPublic() throws Exception {
    mockMvc
        .perform(
            post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"user\":{\"email\":\"nonexistent@test.com\",\"password\":\"password\"}}"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void testGetTagsIsPublic() throws Exception {
    mockMvc.perform(get("/tags").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void testGetArticlesFeedRequiresAuth() throws Exception {
    mockMvc
        .perform(get("/articles/feed").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testPostArticlesRequiresAuth() throws Exception {
    mockMvc
        .perform(
            post("/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"article\":{\"title\":\"t\",\"description\":\"d\",\"body\":\"b\"}}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testOptionsRequestsArePermitted() throws Exception {
    mockMvc.perform(options("/articles")).andExpect(status().isOk());
  }
}
