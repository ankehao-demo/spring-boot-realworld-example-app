package io.spring.graphql.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.ResultPath;
import graphql.schema.DataFetchingEnvironmentImpl;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.graphql.types.Error;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GraphQLCustomizeExceptionHandlerTest {

  private GraphQLCustomizeExceptionHandler handler;

  @BeforeEach
  public void setUp() {
    handler = new GraphQLCustomizeExceptionHandler();
  }

  private DataFetcherExceptionHandlerParameters buildParams(Throwable exception) {
    ExecutionStepInfo stepInfo =
        ExecutionStepInfo.newExecutionStepInfo()
            .type(graphql.schema.GraphQLObjectType.newObject().name("Mutation").build())
            .path(ResultPath.rootPath().segment("test"))
            .build();

    return DataFetcherExceptionHandlerParameters.newExceptionParameters()
        .dataFetchingEnvironment(
            DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                .executionStepInfo(stepInfo)
                .mergedField(
                    graphql.execution.MergedField.newMergedField()
                        .addField(graphql.language.Field.newField("test").build())
                        .build())
                .build())
        .exception(exception)
        .build();
  }

  @Test
  public void should_handle_InvalidAuthenticationException() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();
    DataFetcherExceptionHandlerParameters params = buildParams(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(params);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().get(0).getMessage().contains("invalid email or password"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void should_handle_ConstraintViolationException() {
    ConstraintViolationException cve = buildConstraintViolationException("createUser.email");
    DataFetcherExceptionHandlerParameters params = buildParams(cve);

    DataFetcherExceptionHandlerResult result = handler.onException(params);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
  }

  @Test
  public void should_delegate_to_default_handler_for_other_exceptions() {
    RuntimeException exception = new RuntimeException("generic error");
    DataFetcherExceptionHandlerParameters params = buildParams(exception);

    DataFetcherExceptionHandlerResult result = handler.onException(params);

    assertNotNull(result);
    assertEquals(1, result.getErrors().size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void should_convert_constraint_violations_to_error_data() {
    ConstraintViolationException cve = buildConstraintViolationException("createUser.param.email");

    Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(cve);

    assertNotNull(error);
    assertEquals("BAD_REQUEST", error.getMessage());
    assertNotNull(error.getErrors());
    assertFalse(error.getErrors().isEmpty());
    assertEquals("email", error.getErrors().get(0).getKey());
    assertTrue(error.getErrors().get(0).getValue().contains("must not be blank"));
  }

  @SuppressWarnings("unchecked")
  private ConstraintViolationException buildConstraintViolationException(String pathStr) {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    ConstraintViolation violation = mock(ConstraintViolation.class);

    when(violation.getRootBeanClass()).thenReturn(String.class);

    Path path = mock(Path.class);
    when(path.toString()).thenReturn(pathStr);
    when(violation.getPropertyPath()).thenReturn(path);

    ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);
    java.lang.annotation.Annotation annotation = mock(java.lang.annotation.Annotation.class);
    when(annotation.annotationType())
        .thenReturn((Class) javax.validation.constraints.NotBlank.class);
    when(descriptor.getAnnotation()).thenReturn(annotation);
    when(violation.getConstraintDescriptor()).thenReturn(descriptor);

    when(violation.getMessage()).thenReturn("must not be blank");

    violations.add(violation);
    return new ConstraintViolationException("validation failed", violations);
  }
}
