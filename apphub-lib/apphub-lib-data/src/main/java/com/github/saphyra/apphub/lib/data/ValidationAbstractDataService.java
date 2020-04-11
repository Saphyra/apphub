package com.github.saphyra.apphub.lib.data;

import com.github.saphyra.apphub.lib.data.loader.ContentLoaderFactory;

import java.util.Map;

public abstract class ValidationAbstractDataService<K, V> extends AbstractDataService<K, V> {
    private final DataValidator<Map<K, V>> validator;

    public ValidationAbstractDataService(String path, ContentLoaderFactory contentLoaderFactory, DataValidator<Map<K, V>> validator) {
        super(path, contentLoaderFactory);
        this.validator = validator;
    }

    @Override
    public void load(Class<V> clazz) {
        super.load(clazz);
        validator.validate(this);
    }
}
