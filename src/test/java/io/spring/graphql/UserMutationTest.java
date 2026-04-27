package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import io.spring.application.user.UserService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserMutationTest {

  @Autowired private DgsQueryExecutor dgsQueryExecutor;

  @MockBean private UserRepository userRepository;

  @MockBean private UserService userService;

  @MockBean private PasswordEncoder passwordEncoder;

  @Test
  public void should_create_user() {
    User user = new User("test@example.com", "testuser", "password123", "", "");
    when(userService.createUser(any())).thenReturn(user);

    Object result =
        dgsQueryExecutor.executeAndExtractJsonPath(
            "mutation { createUser(input: {email: \"test@example.com\", username: \"testuser\", password: \"password123\"}) { ... on UserPayload { user { email } } } }",
            "data.createUser");
    Assertions.assertNotNull(result);
  }

  @Test
  public void should_login_user() {
    User user = new User("test@example.com", "testuser", "encodedpass", "", "");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", "encodedpass")).thenReturn(true);

    String email =
        dgsQueryExecutor.executeAndExtractJsonPath(
            "mutation { login(email: \"test@example.com\", password: \"password123\") { user { email } } }",
            "data.login.user.email");
    Assertions.assertEquals("test@example.com", email);
  }
}
