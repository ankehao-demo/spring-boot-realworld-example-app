package io.spring.core.article;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class ArticleTest {

  @Test
  public void should_get_right_slug() {
    Article article = new Article("a new   title", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_get_right_slug_with_number_in_title() {
    Article article = new Article("a new title 2", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title-2"));
  }

  @Test
  public void should_get_lower_case_slug() {
    Article article = new Article("A NEW TITLE", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_handle_other_language() {
    Article article = new Article("中文：标题", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("中文-标题"));
  }

  @Test
  public void should_handle_commas() {
    Article article = new Article("what?the.hell,w", "desc", "body", Arrays.asList("java"), "123");
    assertThat(article.getSlug(), is("what-the-hell-w"));
  }

  @Test
  public void should_calculate_word_count() {
    Article article =
        new Article("title", "desc", "hello world foo bar baz", Arrays.asList("java"), "123");
    assertThat(article.getWordCount(), is(5));
  }

  @Test
  public void should_calculate_word_count_for_empty_body() {
    Article article = new Article("title", "desc", "", Arrays.asList("java"), "123");
    assertThat(article.getWordCount(), is(0));
  }

  @Test
  public void should_update_word_count_on_body_change() {
    Article article =
        new Article("title", "desc", "hello world", Arrays.asList("java"), "123");
    assertThat(article.getWordCount(), is(2));
    article.update("", "", "one two three four");
    assertThat(article.getWordCount(), is(4));
  }
}
