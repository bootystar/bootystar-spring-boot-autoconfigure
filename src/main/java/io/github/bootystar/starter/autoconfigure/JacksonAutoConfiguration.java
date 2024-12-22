package io.github.bootystar.starter.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonAutoConfiguration {

    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";


    /**
     * jackson2对象映射器生成器定制器配置
     * <p>
     * 以下为相关的配置
     * <p>
     * servlet中jackson的配置{@link org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration}
     * 实际在配置源码在同包下的JacksonHttpMessageConvertersConfiguration(非public类)
     * 其使用Import导入了配置
     * <p>
     * webflux中jackson的配置{@link org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration }
     * 在WebFluxAutoConfiguration中导入了CodecsAutoConfiguration
     *
     * @author bootystar
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
    @AutoConfigureBefore(org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class)
    static class Jackson2ObjectMapperBuilderCustomizerConfiguration {

        @Bean
        public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
            log.debug("Jackson2ObjectMapperBuilderCustomizer Configured");
            return builder -> {
                builder
                        // 序列化时，对象为 null，是否抛异常
                        .failOnEmptyBeans(false)
                        // 反序列化时，json 中包含 pojo 不存在属性时，是否抛异常
                        .failOnUnknownProperties(false)
                        // 禁止将 java.util.Date、Calendar 序列化为数字(时间戳)
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        // 设置 java.util.Date, Calendar 序列化、反序列化的格式
                        .dateFormat(new SimpleDateFormat(DEFAULT_DATETIME_PATTERN))
                        // 设置 java.util.Date, Calendar 序列化、反序列化的时区
                        .timeZone(TimeZone.getTimeZone("GMT+8"))
//                    // null 不参与序列化
//                    .serializationInclusion(JsonInclude.Include.NON_NULL)
                ;

                // Jackson 序列化 long类型为String，解决后端返回的Long类型在前端精度丢失的问题
                builder.serializerByType(BigInteger.class, ToStringSerializer.instance);
                builder.serializerByType(Long.class, ToStringSerializer.instance);
                builder.serializerByType(Long.TYPE, ToStringSerializer.instance);

                // 配置 Jackson 反序列化 LocalDateTime、LocalDate、LocalTime 时使用的格式
                builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)));
                builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
                builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

                // 配置 Jackson 序列化 LocalDateTime、LocalDate、LocalTime 时使用的格式
                builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN)));
                builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
                builder.serializers(new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
            };
        }
    }

    /* =====servlet中jackson的配置源码=====
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnBean(ObjectMapper.class)
    @ConditionalOnProperty(name = HttpMessageConvertersAutoConfiguration.PREFERRED_MAPPER_PROPERTY,
            havingValue = "jackson", matchIfMissing = true)
    static class MappingJackson2HttpMessageConverterConfiguration {

        @Bean
        @ConditionalOnMissingBean(value = MappingJackson2HttpMessageConverter.class,
                ignoredType = {
                        "org.springframework.hateoas.server.mvc.TypeConstrainedMappingJackson2HttpMessageConverter",
                        "org.springframework.data.rest.webmvc.alps.AlpsJsonHttpMessageConverter" })
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
            return new MappingJackson2HttpMessageConverter(objectMapper);
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(XmlMapper.class)
    @ConditionalOnBean(Jackson2ObjectMapperBuilder.class)
    protected static class MappingJackson2XmlHttpMessageConverterConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter(
                Jackson2ObjectMapperBuilder builder) {
            return new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build());
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(XmlMapper.class)
    @ConditionalOnBean(Jackson2ObjectMapperBuilder.class)
    protected static class MappingJackson2XmlHttpMessageConverterConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter(
                Jackson2ObjectMapperBuilder builder) {
            return new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build());
        }

    }
    =====servlet中jackson的配置源码===== */

    /* =====webflux中jackson的配置源码=====
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ObjectMapper.class)
    static class JacksonCodecConfiguration {

        @Bean
        @Order(0)
        @ConditionalOnBean(ObjectMapper.class)
        CodecCustomizer jacksonCodecCustomizer(ObjectMapper objectMapper) {
            return (configurer) -> {
                CodecConfigurer.DefaultCodecs defaults = configurer.defaultCodecs();
                defaults.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, EMPTY_MIME_TYPES));
                defaults.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, EMPTY_MIME_TYPES));
            };
        }

    }

    @Configuration(proxyBeanMethods = false)
    static class DefaultCodecsConfiguration {

        @Bean
        @Order(0)
        CodecCustomizer defaultCodecCustomizer(CodecProperties codecProperties) {
            return (configurer) -> {
                PropertyMapper map = PropertyMapper.get();
                CodecConfigurer.DefaultCodecs defaultCodecs = configurer.defaultCodecs();
                defaultCodecs.enableLoggingRequestDetails(codecProperties.isLogRequestDetails());
                map.from(codecProperties.getMaxInMemorySize())
                        .whenNonNull()
                        .asInt(DataSize::toBytes)
                        .to(defaultCodecs::maxInMemorySize);
            };
        }

    }
    =====webflux中jackson的配置源码===== */
}