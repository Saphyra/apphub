package com.github.saphyra.apphub.lib.data.loader;

import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.data.ContentLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Slf4j
public class ContentLoaderFactory {
    private final ObjectMapper objectMapper;
    private final PathMatchingResourcePatternResolver patternResolver;

    public <K, V> ContentLoader getInstance(Class<V> clazz, AbstractDataService<K, V> gameDataService) {
        return new ClassPathLoader<K, V>(
            objectMapper,
            patternResolver,
            gameDataService,
            clazz
        );
    }
}