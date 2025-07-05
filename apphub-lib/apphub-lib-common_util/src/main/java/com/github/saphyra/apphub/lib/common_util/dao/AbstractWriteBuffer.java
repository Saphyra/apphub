package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.google.common.cache.Cache;
import jakarta.annotation.Nullable;

import java.util.Map;

import static java.util.Objects.nonNull;

public abstract class AbstractWriteBuffer<DOMAIN_ID, DOMAIN> extends AbstractBuffer {
    @Nullable
    protected final Cache<DOMAIN_ID, DOMAIN> readCache;

    protected AbstractWriteBuffer(DateTimeUtil dateTimeUtil, @Nullable Cache<DOMAIN_ID, DOMAIN> readCache) {
        super(dateTimeUtil);
        this.readCache = readCache;
    }

    @Override
    protected void afterSynchronize() {
        if (nonNull(readCache)) {
            readCache.putAll(getBufferMap());
        }
    }

    protected abstract Map<DOMAIN_ID, DOMAIN> getBufferMap();
}
