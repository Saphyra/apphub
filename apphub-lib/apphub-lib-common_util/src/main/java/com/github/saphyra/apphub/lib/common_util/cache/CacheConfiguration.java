package com.github.saphyra.apphub.lib.common_util.cache;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {
    public static final String REQUEST_SCOPED_CACHE_MANAGER_BEAN_NAME = "requestScopedCacheManager";

    @Bean(REQUEST_SCOPED_CACHE_MANAGER_BEAN_NAME)
    public CacheManager requestScopedCacheManager() {
        return new RequestScopedCacheManager();
    }
}
