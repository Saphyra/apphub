package com.github.saphyra.apphub.lib.data;

import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public abstract class AbstractDataService<K, V> extends HashMap<K, V> implements OptionalMap<K, V> {
    private static final String BASE_DIR = "src/main/resources/";

    private final ContentLoaderFactory contentLoaderFactory;

    @Getter
    private final String path;

    @Getter
    private final String jarPath;

    public AbstractDataService(String path, ContentLoaderFactory contentLoaderFactory) {
        this.path = BASE_DIR + path;
        jarPath = BASE_DIR;
        this.contentLoaderFactory = contentLoaderFactory;
    }

    protected void load(Class<V> clazz) {
        contentLoaderFactory.getInstance(clazz, this).load();
    }

    public abstract void init();

    public abstract void addItem(V content, String fileName);
}
