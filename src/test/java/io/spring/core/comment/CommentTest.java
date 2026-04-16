package io.spring.core.comment;

import org.junit.jupiter.api.Test;

public class CommentTest {

  @Test
  void testConstructor() {
    Comment comment = new Comment("comment body", "user-id", "article-id");

    org.assertj.core.api.Assertions.assertThat(comment.getBody()).isEqualTo("comment body");
    org.assertj.core.api.Assertions.assertThat(comment.getUserId()).isEqualTo("user-id");
    org.assertj.core.api.Assertions.assertThat(comment.getArticleId()).isEqualTo("article-id");
    org.assertj.core.api.Assertions.assertThat(comment.getId()).isNotNull();
    org.assertj.core.api.Assertions.assertThat(comment.getCreatedAt()).isNotNull();
  }
}
