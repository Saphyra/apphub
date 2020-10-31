package com.github.saphyra.apphub.lib.data;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class CommonDataConfiguration {
    @Bean
    public ContentLoaderFactory contentLoaderFactory(
        ObjectMapperWrapper objectMapperWrapper,
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
    ) {
        return new ContentLoaderFactory(objectMapperWrapper, pathMatchingResourcePatternResolver);
    }

    @Bean
    public PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }
}
