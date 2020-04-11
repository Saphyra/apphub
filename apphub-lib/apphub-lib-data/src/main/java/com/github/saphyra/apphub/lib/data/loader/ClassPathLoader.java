package com.github.saphyra.apphub.lib.data.loader;

import com.github.saphyra.apphub.lib.common_util.CustomObjectMapperWrapper;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.data.ContentLoader;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

@RequiredArgsConstructor
@Builder
@Slf4j
class ClassPathLoader<K, V> implements ContentLoader {
    private final CustomObjectMapperWrapper objectMapperWrapper;
    private final PathMatchingResourcePatternResolver patternResolver;

    private final AbstractDataService<K, V> dataService;
    private final Class<V> clazz;

    @Override
    public void load() {
        try {
            String locationPattern = dataService.getPath().replace("src/main/resources/", "classpath:") + "/*.json";
            log.info("Loading resources for pattern {}", locationPattern);

            Resource[] resources = patternResolver.getResources(locationPattern);
            for (Resource resource : resources) {
                dataService.addItem(objectMapperWrapper.readValue(resource.getURL(), clazz), resource.getFilename());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
