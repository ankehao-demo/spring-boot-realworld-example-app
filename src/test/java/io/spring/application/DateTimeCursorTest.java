package io.spring.application;

import java.time.Instant;
import org.junit.jupiter.api.Test;

public class DateTimeCursorTest {

  @Test
  void testToStringAndParse() {
    Instant now = Instant.now();
    DateTimeCursor cursor = new DateTimeCursor(now);

    String serialized = cursor.toString();
    Instant parsed = DateTimeCursor.parse(serialized);

    org.assertj.core.api.Assertions.assertThat(parsed.toEpochMilli()).isEqualTo(now.toEpochMilli());
  }

  @Test
  void testRoundTrip() {
    long millis = 1700000000000L;
    Instant instant = Instant.ofEpochMilli(millis);
    DateTimeCursor cursor = new DateTimeCursor(instant);

    String serialized = cursor.toString();
    org.assertj.core.api.Assertions.assertThat(serialized).isEqualTo(String.valueOf(millis));

    Instant parsed = DateTimeCursor.parse(serialized);
    org.assertj.core.api.Assertions.assertThat(parsed.toEpochMilli()).isEqualTo(millis);
  }

  @Test
  void testParseNull() {
    Instant parsed = DateTimeCursor.parse(null);

    org.assertj.core.api.Assertions.assertThat(parsed).isNull();
  }
}
