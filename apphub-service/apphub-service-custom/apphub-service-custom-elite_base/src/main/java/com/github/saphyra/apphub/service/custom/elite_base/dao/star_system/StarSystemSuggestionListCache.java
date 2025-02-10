package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.cache.AbstractCache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
public class StarSystemSuggestionListCache extends AbstractCache<String, List<StarSystem>> {
    private final StarSystemDao starSystemDao;

    public StarSystemSuggestionListCache(StarSystemDao starSystemDao) {
        super(CacheBuilder.newBuilder().expireAfterWrite(Duration.ofHours(1)).expireAfterAccess(Duration.ofMinutes(10)).build());
        this.starSystemDao = starSystemDao;
    }

    @Override
    protected Optional<List<StarSystem>> load(String key) {
        return Optional.of(starSystemDao.getByStarNameLike(key));
    }
}
