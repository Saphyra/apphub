package com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
//TODO unit test
class CommodityLastUpdateConverter extends ConverterBase<CommodityLastUpdateEntity, CommodityLastUpdate> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;

    @Override
    protected CommodityLastUpdateEntity processDomainConversion(CommodityLastUpdate domain) {
        return CommodityLastUpdateEntity.builder()
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .build();
    }

    @Override
    protected CommodityLastUpdate processEntityConversion(CommodityLastUpdateEntity entity) {
        return CommodityLastUpdate.builder()
            .externalReference(uuidConverter.convertEntity(entity.getExternalReference()))
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .build();
    }
}
