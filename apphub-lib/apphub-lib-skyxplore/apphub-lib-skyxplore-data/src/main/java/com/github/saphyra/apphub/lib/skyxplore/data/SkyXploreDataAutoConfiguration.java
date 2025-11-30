package com.github.saphyra.apphub.lib.skyxplore.data;

import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.RomanNumberConverter;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
@ComponentScan
public class SkyXploreDataAutoConfiguration {
    @ConditionalOnMissingBean(ContentLoaderFactory.class)
    @Bean
    ContentLoaderFactory contentLoaderFactory(ObjectMapper objectMapper, PathMatchingResourcePatternResolver resolver) {
        return new ContentLoaderFactory(objectMapper, resolver);
    }

    @ConditionalOnMissingBean(PathMatchingResourcePatternResolver.class)
    @Bean
    PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }

    @ConditionalOnMissingBean(Random.class)
    @Bean
    Random random() {
        return new Random();
    }

    @ConditionalOnMissingBean(RomanNumberConverter.class)
    @Bean
    RomanNumberConverter romanNumberConverter() {
        return new RomanNumberConverter();
    }
}
