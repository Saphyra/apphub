package com.github.saphyra.apphub.service.platform.main_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.feign.FeignClientConfiguration;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.config.whitelist.EnableWhiteListedEndpointProperties;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.AntPathMatcher;

@Configuration
@Import({
    CommonConfigProperties.class,
    FeignClientConfiguration.class
})
@EnableHealthCheck
@EnableWhiteListedEndpointProperties
@ComponentScan(basePackageClasses = {
    ErrorReporterService.class
})
@EnableMemoryMonitoring
public class MainGatewayBeanConfiguration {
    private final ObjectFactory<HttpMessageConverters> messageConverters = HttpMessageConverters::new;

    @Bean
    @ConditionalOnMissingBean(DateTimeUtil.class)
    DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }

    @Bean
    AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    AccessTokenHeaderConverter accessTokenHeaderConverter(Base64Encoder base64Encoder, ObjectMapperWrapper objectMapperWrapper) {
        return new AccessTokenHeaderConverter(base64Encoder, objectMapperWrapper);
    }

    @Bean
    Base64Encoder base64Encoder() {
        return new Base64Encoder();
    }

    @Bean
    ObjectMapperWrapper objectMapperWrapper(ObjectMapper objectMapper) {
        return new ObjectMapperWrapper(objectMapper);
    }

    @Bean
    Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    Decoder feignFormDecoder() {
        return new SpringDecoder(messageConverters);
    }

    @Bean
    Random random() {
        return new Random();
    }
}
