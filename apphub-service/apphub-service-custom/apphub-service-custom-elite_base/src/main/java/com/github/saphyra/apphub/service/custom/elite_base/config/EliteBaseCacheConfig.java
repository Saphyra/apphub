package com.github.saphyra.apphub.service.custom.elite_base.config;

import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityCacheKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.Loadout;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutCacheKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class EliteBaseCacheConfig {
    private final EliteBaseProperties eliteBaseProperties;

    @Bean
    Cache<UUID, StarSystem> starSystemReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<CommodityCacheKey, List<Commodity>> commodityReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<LoadoutCacheKey, List<Loadout>> loadoutReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProperties.getCache().getExpireAfterAccess())
            .build();
    }
}
