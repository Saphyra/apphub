package com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class LoadoutConverter extends ConverterBase<LoadoutEntity, Loadout> {
    private final UuidConverter uuidConverter;
    private final LastUpdateDao lastUpdateDao;

    @Override
    protected LoadoutEntity processDomainConversion(Loadout domain) {
        return LoadoutEntity.builder()
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .type(domain.getType())
            .name(domain.getName())
            .commodityLocation(domain.getCommodityLocation())
            .marketId(domain.getMarketId())
            .build();
    }

    @Override
    protected Loadout processEntityConversion(LoadoutEntity entity) {
        UUID externalReference = uuidConverter.convertEntity(entity.getExternalReference());
        return Loadout.builder()
            .externalReference(externalReference)
            .type(entity.getType())
            .name(entity.getName())
            .commodityLocation(entity.getCommodityLocation())
            .marketId(entity.getMarketId())
            .lastUpdate(lastUpdateDao.findById(LastUpdateId.builder()
                    .externalReference(entity.getExternalReference())
                    .type(entity.getType().get())
                    .build())
                .map(LastUpdate::getLastUpdate)
                .orElse(null))
            .build();
    }
}
