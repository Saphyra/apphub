package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.lib.common_util.cache.AbstractCache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
class LastUpdateCache extends AbstractCache<LastUpdateId, LastUpdate> {
    private final LastUpdateDao lastUpdateDao;

    LastUpdateCache(@Lazy LastUpdateDao lastUpdateDao) {
        super(CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10)).build());
        this.lastUpdateDao = lastUpdateDao;
    }

    @Override
    protected Optional<LastUpdate> load(LastUpdateId key) {
        return lastUpdateDao.findById(key);
    }
}
