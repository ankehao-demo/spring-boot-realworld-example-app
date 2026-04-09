package io.spring.graphql.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.FieldErrorResource;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.graphql.types.Error;
import io.spring.graphql.types.ErrorItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLCustomizeExceptionHandler extends DataFetcherExceptionResolverAdapter {

  @Override
  protected List<GraphQLError> resolveToMultipleErrors(Throwable ex, DataFetchingEnvironment env) {
    if (ex instanceof InvalidAuthenticationException) {
      GraphQLError graphqlError =
          GraphqlErrorBuilder.newError(env)
              .errorType(ErrorType.UNAUTHORIZED)
              .message(ex.getMessage())
              .build();
      return Collections.singletonList(graphqlError);
    } else if (ex instanceof ConstraintViolationException) {
      List<FieldErrorResource> errors = new ArrayList<>();
      for (ConstraintViolation<?> violation :
          ((ConstraintViolationException) ex).getConstraintViolations()) {
        FieldErrorResource fieldErrorResource =
            new FieldErrorResource(
                violation.getRootBeanClass().getName(),
                getParam(violation.getPropertyPath().toString()),
                violation
                    .getConstraintDescriptor()
                    .getAnnotation()
                    .annotationType()
                    .getSimpleName(),
                violation.getMessage());
        errors.add(fieldErrorResource);
      }
      GraphQLError graphqlError =
          GraphqlErrorBuilder.newError(env)
              .errorType(ErrorType.BAD_REQUEST)
              .message(ex.getMessage())
              .extensions(errorsToMap(errors))
              .build();
      return Collections.singletonList(graphqlError);
    } else {
      return null;
    }
  }

  public static Error getErrorsAsData(ConstraintViolationException cve) {
    List<FieldErrorResource> errors = new ArrayList<>();
    for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
      FieldErrorResource fieldErrorResource =
          new FieldErrorResource(
              violation.getRootBeanClass().getName(),
              getParam(violation.getPropertyPath().toString()),
              violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
              violation.getMessage());
      errors.add(fieldErrorResource);
    }
    Map<String, List<String>> errorMap = new HashMap<>();
    for (FieldErrorResource fieldErrorResource : errors) {
      if (!errorMap.containsKey(fieldErrorResource.getField())) {
        errorMap.put(fieldErrorResource.getField(), new ArrayList<>());
      }
      errorMap.get(fieldErrorResource.getField()).add(fieldErrorResource.getMessage());
    }
    List<ErrorItem> errorItems =
        errorMap.entrySet().stream()
            .map(kv -> ErrorItem.newBuilder().key(kv.getKey()).value(kv.getValue()).build())
            .collect(Collectors.toList());
    return Error.newBuilder().message("BAD_REQUEST").errors(errorItems).build();
  }

  private static String getParam(String s) {
    String[] splits = s.split("\\.");
    if (splits.length == 1) {
      return s;
    } else {
      return String.join(".", Arrays.copyOfRange(splits, 2, splits.length));
    }
  }

  private static Map<String, Object> errorsToMap(List<FieldErrorResource> errors) {
    Map<String, Object> json = new HashMap<>();
    for (FieldErrorResource fieldErrorResource : errors) {
      if (!json.containsKey(fieldErrorResource.getField())) {
        json.put(fieldErrorResource.getField(), new ArrayList<>());
      }
      ((List) json.get(fieldErrorResource.getField())).add(fieldErrorResource.getMessage());
    }
    return json;
  }
}
