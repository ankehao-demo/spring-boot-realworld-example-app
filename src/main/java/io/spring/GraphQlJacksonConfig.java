package io.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures a custom Jackson HttpMessageConverter for the GraphQL endpoint. The global
 * ObjectMapper has UNWRAP_ROOT_VALUE=true (required by the REST API's @JsonRootName DTOs), but this
 * breaks GraphQL request deserialization which reads Map&lt;String, Object&gt;. This config adds a
 * Map-only converter without UNWRAP_ROOT_VALUE at the start of the converter chain so GraphQL works
 * while REST endpoints remain unaffected.
 */
@Configuration
public class GraphQlJacksonConfig implements WebMvcConfigurer {

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    ObjectMapper graphQlMapper = null;
    for (HttpMessageConverter<?> converter : converters) {
      if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
        graphQlMapper = jacksonConverter.getObjectMapper().copy();
        graphQlMapper.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        break;
      }
    }
    if (graphQlMapper == null) {
      graphQlMapper = new ObjectMapper();
    }

    MappingJackson2HttpMessageConverter graphQlConverter =
        new MapOnlyJackson2HttpMessageConverter(graphQlMapper);
    converters.add(0, graphQlConverter);
  }

  /**
   * A Jackson HTTP message converter that only handles Map types for reading. This ensures it
   * intercepts GraphQL request body deserialization (Map&lt;String, Object&gt;) but does not
   * interfere with REST DTO deserialization (@JsonRootName types like RegisterParam, LoginParam,
   * etc.).
   */
  private static class MapOnlyJackson2HttpMessageConverter
      extends MappingJackson2HttpMessageConverter {

    MapOnlyJackson2HttpMessageConverter(ObjectMapper objectMapper) {
      super(objectMapper);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
      return Map.class.isAssignableFrom(clazz) && super.canRead(clazz, mediaType);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
      Class<?> rawType = resolveRawType(type);
      return rawType != null
          && Map.class.isAssignableFrom(rawType)
          && super.canRead(type, contextClass, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
      // Don't interfere with response serialization; let the default converter handle it
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
