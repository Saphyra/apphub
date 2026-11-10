package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.settlement.Settlement;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.settlement.SettlementDao;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.settlement.SettlementFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementSaver {
    private final SettlementDao settlementDao;
    private final SettlementFactory settlementFactory;
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;

    public synchronized void save(LocalDateTime timestamp, UUID starSystemId, UUID bodyId, String settlementName, Long marketId, Double longitude, Double latitude) {
        if (isNull(starSystemId) || isBlank(settlementName)) {
            throw new IllegalArgumentException("starSystemId and settlementName must not be null");
        }

        log.debug("Saving settlement {}", settlementName);

        Optional<Settlement> maybeSettlement = settlementDao.findByStarSystemIdAndSettlementName(starSystemId, settlementName);
        if (maybeSettlement.isPresent()) {
            updateFields(timestamp, maybeSettlement.get(), bodyId, marketId, longitude, latitude);
        } else {
            Settlement created = settlementFactory.create(starSystemId, bodyId, settlementName, marketId, longitude, latitude);
            log.info("New {} discovered: {}", created.getClass().getSimpleName(), created);
            settlementDao.save(created);
            saveLastUpdate(timestamp, created);
        }

        log.debug("Saved settlement {}", settlementName);
    }

    private void updateFields(LocalDateTime timestamp, Settlement settlement, UUID bodyId, Long marketId, Double longitude, Double latitude) {
        LocalDateTime lastUpdated = lastUpdateDao.findByIdOrDefault(settlement.getId(), ObjectType.SETTLEMENT).getLastUpdate();
        if (timestamp.isBefore(lastUpdated)) {
            log.debug("Settlement {} has newer data than {}", settlement.getId(), timestamp);
            return;
        }

        saveLastUpdate(timestamp, settlement);

        List<Boolean> results = Stream.of(
                new UpdateHelper(bodyId, settlement::getBodyId, () -> settlement.setBodyId(bodyId)),
                new UpdateHelper(marketId, settlement::getMarketId, () -> settlement.setMarketId(marketId)),
                new UpdateHelper(longitude, settlement::getLongitude, () -> settlement.setLongitude(longitude)),
                new UpdateHelper(latitude, settlement::getLatitude, () -> settlement.setLatitude(latitude))
            )
            .map(UpdateHelper::modify)
            .toList();


        if (results.stream().anyMatch(Boolean::booleanValue)) {
            settlementDao.save(settlement);
        }
    }

    private void saveLastUpdate(LocalDateTime timestamp, Settlement settlement) {
        lastUpdateDao.save(lastUpdateFactory.create(settlement.getId(), ObjectType.SETTLEMENT, timestamp));
    }
}
