package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.google.common.cache.Cache;
import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.Map;

import static java.util.Objects.nonNull;

public abstract class AbstractWriteBuffer<DOMAIN_ID, DOMAIN> extends AbstractBuffer<DOMAIN> {
    @Nullable
    protected final Cache<DOMAIN_ID, DOMAIN> readCache;

    protected AbstractWriteBuffer(DateTimeUtil dateTimeUtil, @Nullable Cache<DOMAIN_ID, DOMAIN> readCache) {
        super(dateTimeUtil);
        this.readCache = readCache;
    }

    @Override
    protected void afterSynchronize(Collection<DOMAIN> bufferCopy) {
        if (nonNull(readCache)) {
            readCache.putAll(getBufferMap(bufferCopy));
        }
    }

    protected abstract Map<DOMAIN_ID, DOMAIN> getBufferMap(Collection<DOMAIN> bufferCopy);

    @Override
    public int getOrder() {
        return DaoConstants.DEFAULT_WRITE_BUFFER_ORDER;
    }
}
