package io.spring.application.article;

import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonRootName("articles")
@NoArgsConstructor
@AllArgsConstructor
public class NewArticleListParam {
  @NotEmpty(message = "articles list can't be empty")
  @Valid
  private List<NewArticleParam> articles;
}
