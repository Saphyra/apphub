package com.github.saphyra.apphub.lib.skyxplore.data;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@ComponentScan
public class SkyXploreDataConfig {
    @ConditionalOnMissingBean(ContentLoaderFactory.class)
    @Bean
    ContentLoaderFactory contentLoaderFactory(ObjectMapperWrapper objectMapperWrapper, PathMatchingResourcePatternResolver resolver) {
        return new ContentLoaderFactory(objectMapperWrapper, resolver);
    }

    @ConditionalOnMissingBean(PathMatchingResourcePatternResolver.class)
    @Bean
    PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }
}
