package io.spring.infrastructure.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DateTimeHandlerTest {

  private DateTimeHandler handler = new DateTimeHandler();

  @Mock private PreparedStatement ps;
  @Mock private ResultSet rs;
  @Mock private CallableStatement cs;

  @Test
  void testSetParameter() throws Exception {
    DateTime now = new DateTime().withZone(DateTimeZone.UTC);

    handler.setParameter(ps, 1, now, null);

    verify(ps).setTimestamp(eq(1), any(Timestamp.class), any(java.util.Calendar.class));
  }

  @Test
  void testGetResultByColumnName() throws Exception {
    Timestamp timestamp = new Timestamp(1700000000000L);
    when(rs.getTimestamp(eq("created_at"), any(java.util.Calendar.class))).thenReturn(timestamp);

    DateTime result = handler.getResult(rs, "created_at");

    org.assertj.core.api.Assertions.assertThat(result).isNotNull();
    org.assertj.core.api.Assertions.assertThat(result.getMillis()).isEqualTo(1700000000000L);
  }

  @Test
  void testGetResultByColumnIndex() throws Exception {
    Timestamp timestamp = new Timestamp(1700000000000L);
    when(rs.getTimestamp(eq(1), any(java.util.Calendar.class))).thenReturn(timestamp);

    DateTime result = handler.getResult(rs, 1);

    org.assertj.core.api.Assertions.assertThat(result).isNotNull();
    org.assertj.core.api.Assertions.assertThat(result.getMillis()).isEqualTo(1700000000000L);
  }

  @Test
  void testGetResultFromCallableStatement() throws Exception {
    Timestamp timestamp = new Timestamp(1700000000000L);
    when(cs.getTimestamp(eq(1), any(java.util.Calendar.class))).thenReturn(timestamp);

    DateTime result = handler.getResult(cs, 1);

    org.assertj.core.api.Assertions.assertThat(result).isNotNull();
  }

  @Test
  void testRoundTrip() throws Exception {
    DateTime original = new DateTime(1700000000000L).withZone(DateTimeZone.UTC);

    handler.setParameter(ps, 1, original, null);
    verify(ps).setTimestamp(eq(1), any(Timestamp.class), any(java.util.Calendar.class));
  }
}
