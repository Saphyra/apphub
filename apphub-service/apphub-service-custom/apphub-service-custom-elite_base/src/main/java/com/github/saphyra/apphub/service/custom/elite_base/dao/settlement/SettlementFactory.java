package com.github.saphyra.apphub.service.custom.elite_base.dao.settlement;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementFactory {
    private final IdGenerator idGenerator;

    public Settlement create(LocalDateTime timestamp, UUID starSystemId, UUID bodyId, String settlementName, Long marketId, Double longitude, Double latitude) {
        return Settlement.builder()
            .id(idGenerator.randomUuid())
            .lastUpdate(timestamp)
            .starSystemId(starSystemId)
            .bodyId(bodyId)
            .settlementName(settlementName)
            .marketId(Optional.ofNullable(marketId).orElse(EliteBaseConstants.NO_MARKET_ID))
            .longitude(longitude)
            .latitude(latitude)
            .build();
    }
}
