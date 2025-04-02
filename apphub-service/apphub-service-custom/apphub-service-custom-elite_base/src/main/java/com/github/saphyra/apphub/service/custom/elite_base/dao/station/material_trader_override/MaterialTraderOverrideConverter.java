package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MaterialTraderOverrideConverter extends ConverterBase<MaterialTraderOverrideEntity, MaterialTraderOverride> {
    private final UuidConverter uuidConverter;

    @Override
    protected MaterialTraderOverrideEntity processDomainConversion(MaterialTraderOverride domain) {
        return MaterialTraderOverrideEntity.builder()
            .stationId(uuidConverter.convertDomain(domain.getStationId()))
            .materialType(domain.getMaterialType())
            .verified(domain.isVerified())
            .build();
    }

    @Override
    protected MaterialTraderOverride processEntityConversion(MaterialTraderOverrideEntity entity) {
        return MaterialTraderOverride.builder()
            .stationId(uuidConverter.convertEntity(entity.getStationId()))
            .materialType(entity.getMaterialType())
            .verified(entity.getVerified())
            .build();
    }
}
