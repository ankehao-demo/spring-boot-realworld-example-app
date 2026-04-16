package io.spring.application;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

public class DateTimeCursorTest {

  @Test
  void testToStringAndParse() {
    DateTime now = new DateTime().withZone(DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(now);

    String serialized = cursor.toString();
    DateTime parsed = DateTimeCursor.parse(serialized);

    org.assertj.core.api.Assertions.assertThat(parsed.getMillis()).isEqualTo(now.getMillis());
  }

  @Test
  void testRoundTrip() {
    long millis = 1700000000000L;
    DateTime dateTime = new DateTime(millis).withZone(DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(dateTime);

    String serialized = cursor.toString();
    org.assertj.core.api.Assertions.assertThat(serialized).isEqualTo(String.valueOf(millis));

    DateTime parsed = DateTimeCursor.parse(serialized);
    org.assertj.core.api.Assertions.assertThat(parsed.getMillis()).isEqualTo(millis);
  }

  @Test
  void testParseWithUTCZone() {
    String millis = "1700000000000";
    DateTime parsed = DateTimeCursor.parse(millis);

    org.assertj.core.api.Assertions.assertThat(parsed.getZone()).isEqualTo(DateTimeZone.UTC);
  }

  @Test
  void testParseNull() {
    DateTime parsed = DateTimeCursor.parse(null);

    org.assertj.core.api.Assertions.assertThat(parsed).isNull();
  }
}
