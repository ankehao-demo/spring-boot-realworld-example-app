package io.spring;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Jackson HTTP message converters so that REST API DTOs annotated with {@link
 * JsonRootName} are deserialized with {@code UNWRAP_ROOT_VALUE} enabled, while all other types
 * (including GraphQL's {@code Map<String, Object>} request body) use the default ObjectMapper
 * without root-value unwrapping.
 *
 * <p>The global {@code spring.jackson.deserialization.UNWRAP_ROOT_VALUE} property is intentionally
 * NOT set in {@code application.properties} because it would break GraphQL request deserialization.
 * This config provides the equivalent behaviour only for the types that need it.
 */
@Configuration
public class GraphQlJacksonConfig implements WebMvcConfigurer {

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    ObjectMapper unwrapMapper = null;
    for (HttpMessageConverter<?> converter : converters) {
      if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
        unwrapMapper = jacksonConverter.getObjectMapper().copy();
        unwrapMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        break;
      }
    }
    if (unwrapMapper == null) {
      unwrapMapper = new ObjectMapper();
      unwrapMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
    }

    converters.add(0, new JsonRootNameAwareConverter(unwrapMapper));
  }

  /**
   * A Jackson HTTP message converter that only handles types annotated with {@link JsonRootName}
   * for reading. This ensures REST API DTOs (RegisterParam, LoginParam, etc.) get {@code
   * UNWRAP_ROOT_VALUE} behaviour, while GraphQL requests ({@code Map<String, Object>}) fall through
   * to the default converter which does NOT unwrap root values.
   */
  private static class JsonRootNameAwareConverter extends MappingJackson2HttpMessageConverter {

    JsonRootNameAwareConverter(ObjectMapper objectMapper) {
      super(objectMapper);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
      return clazz.isAnnotationPresent(JsonRootName.class) && super.canRead(clazz, mediaType);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
      Class<?> rawType = resolveRawType(type);
      return rawType != null
          && rawType.isAnnotationPresent(JsonRootName.class)
          && super.canRead(type, contextClass, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
      // REST responses are wrapped manually (e.g. UsersApi.userResponse()),
      // so this converter should not interfere with serialization.
      return false;
    }

    private static Class<?> resolveRawType(Type type) {
      if (type instanceof ParameterizedType pt && pt.getRawType() instanceof Class<?> rawClass) {
        return rawClass;
      }
      if (type instanceof Class<?> clazz) {
        return clazz;
      }
      return null;
    }
  }
}
