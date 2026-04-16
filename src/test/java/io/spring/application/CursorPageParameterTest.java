package io.spring.application;

import io.spring.application.CursorPager.Direction;
import org.junit.jupiter.api.Test;

public class CursorPageParameterTest {

  @Test
  void testNormalLimit() {
    CursorPageParameter<DateTimeCursor> param =
        new CursorPageParameter<>(null, 20, Direction.NEXT);

    org.assertj.core.api.Assertions.assertThat(param.getLimit()).isEqualTo(20);
    org.assertj.core.api.Assertions.assertThat(param.getQueryLimit()).isEqualTo(21);
  }

  @Test
  void testLimitClampedToMax() {
    CursorPageParameter<DateTimeCursor> param =
        new CursorPageParameter<>(null, 2000, Direction.NEXT);

    org.assertj.core.api.Assertions.assertThat(param.getLimit()).isEqualTo(1000);
    org.assertj.core.api.Assertions.assertThat(param.getQueryLimit()).isEqualTo(1001);
  }

  @Test
  void testNegativeLimitKeepsDefault() {
    CursorPageParameter<DateTimeCursor> param =
        new CursorPageParameter<>(null, -5, Direction.NEXT);

    org.assertj.core.api.Assertions.assertThat(param.getLimit()).isEqualTo(20);
    org.assertj.core.api.Assertions.assertThat(param.getQueryLimit()).isEqualTo(21);
  }

  @Test
  void testZeroLimitKeepsDefault() {
    CursorPageParameter<DateTimeCursor> param =
        new CursorPageParameter<>(null, 0, Direction.NEXT);

    org.assertj.core.api.Assertions.assertThat(param.getLimit()).isEqualTo(20);
    org.assertj.core.api.Assertions.assertThat(param.getQueryLimit()).isEqualTo(21);
  }

  @Test
  void testDirection() {
    CursorPageParameter<DateTimeCursor> param =
        new CursorPageParameter<>(null, 10, Direction.PREV);

    org.assertj.core.api.Assertions.assertThat(param.getDirection()).isEqualTo(Direction.PREV);
    org.assertj.core.api.Assertions.assertThat(param.isNext()).isFalse();
  }

  @Test
  void testIsNext() {
    CursorPageParameter<DateTimeCursor> param =
        new CursorPageParameter<>(null, 10, Direction.NEXT);

    org.assertj.core.api.Assertions.assertThat(param.isNext()).isTrue();
  }
}
