package com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.state;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MinorFactionStateConverter extends ConverterBase<MinorFactionStateEntity, MinorFactionState> {
    private final UuidConverter uuidConverter;

    @Override
    protected MinorFactionStateEntity processDomainConversion(MinorFactionState domain) {
        return MinorFactionStateEntity.builder()
            .minorFactionId(uuidConverter.convertDomain(domain.getMinorFactionId()))
            .status(domain.getStatus())
            .state(domain.getState())
            .trend(domain.getTrend())
            .build();
    }

    @Override
    protected MinorFactionState processEntityConversion(MinorFactionStateEntity entity) {
        return MinorFactionState.builder()
            .minorFactionId(uuidConverter.convertEntity(entity.getMinorFactionId()))
            .status(entity.getStatus())
            .state(entity.getState())
            .trend(entity.getTrend())
            .build();
    }
}
