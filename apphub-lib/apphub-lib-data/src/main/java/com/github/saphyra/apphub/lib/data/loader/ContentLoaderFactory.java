package com.github.saphyra.apphub.lib.data.loader;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.data.ContentLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@RequiredArgsConstructor
@Slf4j
public class ContentLoaderFactory {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final PathMatchingResourcePatternResolver patternResolver;

    public <K, V> ContentLoader getInstance(Class<V> clazz, AbstractDataService<K, V> gameDataService) {
        return new ClassPathLoader<K, V>(
            objectMapperWrapper,
            patternResolver,
            gameDataService,
            clazz
        );
    }
}