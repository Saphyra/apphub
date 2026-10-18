package com.github.saphyra.apphub.service.feature.elite_base.processor.config;

import com.github.saphyra.apphub.service.feature.elite_base.processor.common.EliteBaseProcessorProperties;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.equipment.Equipment;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.loadout.spaceship.Spaceship;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.commodity.Commodity;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.fc_material.FcMaterial;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.last_update.LastUpdateId;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.star_system.StarSystem;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class EliteBaseProcessorCacheConfig {
    private final EliteBaseProcessorProperties eliteBaseProcessorProperties;

    @Bean
    Cache<String, CommodityAveragePrice> commodityAveragePriceCache() {
        return CacheBuilder.newBuilder()
            .build();
    }

    @Bean
    Cache<LastUpdateId, LastUpdate> lastUpdateCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProcessorProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<String, StarSystem> starSystemReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProcessorProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<Long, List<Commodity>> commodityReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProcessorProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<Long, List<FcMaterial>> fcMaterialReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProcessorProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<Long, List<Equipment>> equipmentReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProcessorProperties.getCache().getExpireAfterAccess())
            .build();
    }

    @Bean
    Cache<Long, List<Spaceship>> spaceshipReadCache() {
        return CacheBuilder.newBuilder()
            .expireAfterAccess(eliteBaseProcessorProperties.getCache().getExpireAfterAccess())
            .build();
    }
}
