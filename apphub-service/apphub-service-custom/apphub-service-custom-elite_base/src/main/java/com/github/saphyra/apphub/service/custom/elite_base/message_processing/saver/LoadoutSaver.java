package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.Loadout;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.loadout.LoadoutType;
import com.google.common.util.concurrent.Striped;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadoutSaver {
    private static final Striped<Lock> LOCKS = Striped.lock(8);

    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;
    private final LoadoutDao loadoutDao;
    private final LoadoutFactory loadoutFactory;
    private final PerformanceReporter performanceReporter;

    @SneakyThrows
    public void save(LocalDateTime timestamp, LoadoutType type, CommodityLocation commodityLocation, UUID externalReference, Long marketId, List<String> items) {
        if ((isNull(commodityLocation) || isNull(externalReference)) && isNull(marketId)) {
            throw new IllegalArgumentException("Both commodityLocation or externalReference and marketId is null");
        }

        LockKey key = new LockKey(externalReference, type);
        Lock lock = LOCKS.get(key);
        if (!lock.tryLock(30, TimeUnit.SECONDS)) {
            throw new MessageProcessingDelayedException("Lock acquisition failed in class " + getClass().getSimpleName());
        }

        log.debug("Saving Loadout for externalReference {} and type {}", externalReference, type);

        try {
            lastUpdateDao.save(lastUpdateFactory.create(externalReference, type, timestamp));

            Map<String, Loadout> existingLoadouts = getExistingLoadouts(type, externalReference, marketId)
                .stream()
                .collect(Collectors.toMap(Loadout::getName, Function.identity()));

            List<Loadout> newItems = items.stream()
                .filter(item -> !existingLoadouts.containsKey(item))
                .map(name -> loadoutFactory.create(timestamp, type, commodityLocation, externalReference, marketId, name))
                .toList();

            List<String> deletedItems = existingLoadouts.values()
                .stream()
                .map(Loadout::getName)
                .filter(name -> !items.contains(name))
                .toList();

            performanceReporter.wrap(
                () -> loadoutDao.deleteByExternalReferenceAndLoadoutTypeAndNameIn(externalReference, type, deletedItems),
                PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.SAVE_LOADOUT_DELETE_ALL.name()
            );

            performanceReporter.wrap(
                () -> loadoutDao.saveAll(newItems),
                PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.SAVE_LOADOUT_SAVE_ALL.name()
            );

            log.debug("Saved Loadout for externalReference {} and type {}", externalReference, type);
        } finally {
            lock.unlock();
        }
    }

    private List<Loadout> getExistingLoadouts(LoadoutType type, UUID externalReference, Long marketId) {
        return performanceReporter.wrap(
            () -> loadoutDao.getByExternalReferenceOrMarketIdAndLoadoutType(externalReference, marketId, type),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.SAVE_LOADOUT_QUERY_EXISTING.name()
        );
    }


    @Data
    private static class LockKey {
        private final UUID externalReference;
        private final LoadoutType loadoutType;
    }
}
