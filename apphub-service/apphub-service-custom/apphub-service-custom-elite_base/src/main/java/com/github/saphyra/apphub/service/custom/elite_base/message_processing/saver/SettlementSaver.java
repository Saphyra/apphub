package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement.Settlement;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement.SettlementDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement.SettlementFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementSaver {
    private final SettlementDao settlementDao;
    private final SettlementFactory settlementFactory;

    public synchronized void save(LocalDateTime timestamp, UUID starSystemId, UUID bodyId, String settlementName, Long marketId, Double longitude, Double latitude) {
        if (isNull(starSystemId) || isBlank(settlementName)) {
            throw new IllegalArgumentException("starSystemId and settlementName must not be null");
        }

        Settlement settlement = settlementDao.findByStarSystemIdAndSettlementName(starSystemId, settlementName)
            .orElseGet(() -> {
                Settlement created = settlementFactory.create(timestamp, starSystemId, bodyId, settlementName, marketId, longitude, latitude);
                log.debug("Saving new {}", created);
                settlementDao.save(created);
                return created;
            });

        updateFields(timestamp, settlement, bodyId, marketId, longitude, latitude);
    }

    private void updateFields(LocalDateTime timestamp, Settlement settlement, UUID bodyId, Long marketId, Double longitude, Double latitude) {
        if (timestamp.isBefore(settlement.getLastUpdate())) {
            log.debug("Settlement {} has newer data than {}", settlement.getId(), timestamp);
            return;
        }

        List.of(
                new UpdateHelper(timestamp, settlement::getLastUpdate, () -> settlement.setLastUpdate(timestamp)),
                new UpdateHelper(bodyId, settlement::getBodyId, () -> settlement.setBodyId(bodyId)),
                new UpdateHelper(marketId, settlement::getMarketId, () -> settlement.setMarketId(marketId)),
                new UpdateHelper(longitude, settlement::getLongitude, () -> settlement.setLongitude(longitude)),
                new UpdateHelper(latitude, settlement::getLatitude, () -> settlement.setLatitude(latitude))
            )
            .forEach(UpdateHelper::modify);

        settlementDao.save(settlement);
    }
}
