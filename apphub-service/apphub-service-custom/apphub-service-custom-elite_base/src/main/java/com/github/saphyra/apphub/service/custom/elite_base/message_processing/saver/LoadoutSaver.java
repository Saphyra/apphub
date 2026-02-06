package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.LoadoutDaoSupport;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.type.ItemTypeDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.Loadout;
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
//TODO unit test
public class LoadoutSaver {
    private static final Striped<Lock> LOCKS = Striped.lock(8);

    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;
    private final PerformanceReporter performanceReporter;
    private final LoadoutDaoSupport loadoutDaoSupport;
    private final ItemTypeDao itemTypeDao;

    @SneakyThrows
    public void save(LocalDateTime timestamp, ItemType type, ItemLocationType locationType, UUID externalReference, Long marketId, List<String> items) {
        if (isNull(marketId)) {
            throw new IllegalArgumentException("marketId must not be null");
        }

        List<String> lowercaseItems = items.stream()
            .map(String::toLowerCase)
            .toList();

        LockKey key = new LockKey(externalReference, type);
        Lock lock = LOCKS.get(key);
        if (!lock.tryLock(30, TimeUnit.SECONDS)) {
            throw new MessageProcessingDelayedException("Lock acquisition failed in class " + getClass().getSimpleName());
        }

        log.debug("Saving Loadout for externalReference {} and type {}", externalReference, type);

        try {
            LocalDateTime lastUpdate = lastUpdateDao.findById(externalReference, type)
                .map(LastUpdate::getLastUpdate)
                .orElse(LocalDateTime.MIN);

            if (timestamp.isBefore(lastUpdate)) {
                log.debug("Received Loadout is outdated.");
                return;
            }

            lastUpdateDao.save(lastUpdateFactory.create(externalReference, type, timestamp));

            Map<String, Loadout> existingLoadouts = getExistingLoadouts(type, marketId)
                .stream()
                .collect(Collectors.toMap(Loadout::getItemName, Function.identity()));

            List<Loadout> newItems = lowercaseItems.stream()
                .filter(item -> !existingLoadouts.containsKey(item))
                .map(name -> loadoutDaoSupport.create(type, locationType, externalReference, marketId, name))
                .toList();

            List<Loadout> deletedItems = existingLoadouts.values()
                .stream()
                .filter(loadout -> !lowercaseItems.contains(loadout.getItemName()))
                .toList();

            itemTypeDao.saveAll(type, lowercaseItems);

            performanceReporter.wrap(
                () -> loadoutDaoSupport.deleteAll(type, deletedItems),
                PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.SAVE_LOADOUT_DELETE_ALL.name()
            );

            performanceReporter.wrap(
                () -> loadoutDaoSupport.saveAll(type, newItems),
                PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.SAVE_LOADOUT_SAVE_ALL.name()
            );

            log.debug("Saved Loadout for externalReference {} and type {}", externalReference, type);
        } finally {
            lock.unlock();
        }
    }

    private List<Loadout> getExistingLoadouts(ItemType type, Long marketId) {
        return performanceReporter.wrap(
            () -> loadoutDaoSupport.getByMarketId(type, marketId),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.SAVE_LOADOUT_QUERY_EXISTING.name()
        );
    }

    @Data
    private static class LockKey {
        private final UUID externalReference;
        private final ItemType itemType;
    }
}
