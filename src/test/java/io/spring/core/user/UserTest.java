package io.spring.core.user;

import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  void testConstructor() {
    User user = new User("test@test.com", "testuser", "password", "bio", "image");

    org.assertj.core.api.Assertions.assertThat(user.getEmail()).isEqualTo("test@test.com");
    org.assertj.core.api.Assertions.assertThat(user.getUsername()).isEqualTo("testuser");
    org.assertj.core.api.Assertions.assertThat(user.getPassword()).isEqualTo("password");
    org.assertj.core.api.Assertions.assertThat(user.getBio()).isEqualTo("bio");
    org.assertj.core.api.Assertions.assertThat(user.getImage()).isEqualTo("image");
    org.assertj.core.api.Assertions.assertThat(user.getId()).isNotNull();
  }

  @Test
  void testUpdate() {
    User user = new User("test@test.com", "testuser", "password", "bio", "image");

    user.update("new@test.com", "newuser", "newpassword", "new bio", "new image");

    org.assertj.core.api.Assertions.assertThat(user.getEmail()).isEqualTo("new@test.com");
    org.assertj.core.api.Assertions.assertThat(user.getUsername()).isEqualTo("newuser");
    org.assertj.core.api.Assertions.assertThat(user.getPassword()).isEqualTo("newpassword");
    org.assertj.core.api.Assertions.assertThat(user.getBio()).isEqualTo("new bio");
    org.assertj.core.api.Assertions.assertThat(user.getImage()).isEqualTo("new image");
  }

  @Test
  void testUpdateWithEmptyStrings() {
    User user = new User("test@test.com", "testuser", "password", "bio", "image");

    user.update("", "", "", "", "");

    org.assertj.core.api.Assertions.assertThat(user.getEmail()).isEqualTo("test@test.com");
    org.assertj.core.api.Assertions.assertThat(user.getUsername()).isEqualTo("testuser");
    org.assertj.core.api.Assertions.assertThat(user.getPassword()).isEqualTo("password");
    org.assertj.core.api.Assertions.assertThat(user.getBio()).isEqualTo("bio");
    org.assertj.core.api.Assertions.assertThat(user.getImage()).isEqualTo("image");
  }
}
