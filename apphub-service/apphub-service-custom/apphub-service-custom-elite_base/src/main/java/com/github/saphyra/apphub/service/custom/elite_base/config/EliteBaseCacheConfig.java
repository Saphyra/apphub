package com.github.saphyra.apphub.service.custom.elite_base.config;

import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship.Spaceship;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.equipment.Equipment;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material.FcMaterial;
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
    Cache<Long, List<Commodity>> commodityReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<Long, List<FcMaterial>> fcMaterialReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<Long, List<Equipment>> equipmentReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<Long, List<Spaceship>> spaceshipReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProperties.getCache().getExpireAfterAccess())
            .build();
    }
}
