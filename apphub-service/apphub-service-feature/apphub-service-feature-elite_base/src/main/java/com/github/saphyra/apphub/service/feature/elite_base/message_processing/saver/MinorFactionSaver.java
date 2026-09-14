package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.feature.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.feature.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.feature.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.MinorFactionDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.MinorFactionFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.state.MinorFactionStateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.minor_faction.state.StateStatus;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.journal.Faction;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.journal.FactionState;
import com.google.common.util.concurrent.Striped;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinorFactionSaver {
    private static final Striped<Lock> MINOR_FACTION_NAME_LOCK = Striped.lock(8);

    private final MinorFactionDao minorFactionDao;
    private final MinorFactionFactory minorFactionFactory;
    private final MinorFactionStateFactory minorFactionStateFactory;
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;

    public List<MinorFaction> save(LocalDateTime timestamp, Faction[] factions) {
        if (isNull(factions)) {
            return null;
        }

        return Arrays.stream(factions)
            .map(faction -> save(
                timestamp,
                faction.getName(),
                FactionStateEnum.parse(faction.getState()),
                faction.getInfluence(),
                Allegiance.parse(faction.getAllegiance()),
                CollectionUtils.toList(faction.getActiveStates()),
                CollectionUtils.toList(faction.getPendingStates()),
                CollectionUtils.toList(faction.getRecoveringStates())
            ))
            .toList();
    }

    public MinorFaction save(LocalDateTime timestamp, String factionName, FactionStateEnum economicState) {
        return save(timestamp, factionName, economicState, null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    private MinorFaction save(
        LocalDateTime timestamp,
        String factionName,
        FactionStateEnum economicState,
        Double influence,
        Allegiance allegiance,
        List<FactionState> activeStates,
        List<FactionState> pendingStates,
        List<FactionState> recoveringStates
    ) {
        if (isBlank(factionName)) {
            return null;
        }

        log.debug("Saving minorFaction {}", factionName);

        Lock lock = MINOR_FACTION_NAME_LOCK.get(factionName);
        lock(lock);

        try {
            Optional<MinorFaction> maybeMinorFaction = minorFactionDao.findByFactionName(factionName);
            if (maybeMinorFaction.isPresent()) {
                MinorFaction minorFaction = maybeMinorFaction.get();

                updateFields(timestamp, minorFaction, economicState, influence, allegiance, activeStates, pendingStates, recoveringStates);

                return minorFaction;
            } else {
                MinorFaction created = minorFactionFactory.create(
                    factionName,
                    economicState,
                    influence,
                    allegiance,
                    activeStates,
                    pendingStates,
                    recoveringStates
                );
                log.debug("Saving new {}", created);
                minorFactionDao.save(created);

                saveLastUpdate(timestamp, created);

                return created;
            }
        } finally {
            lock.unlock();
        }
    }

    @SneakyThrows
    private void lock(Lock lock) {
        if (!lock.tryLock(30, TimeUnit.SECONDS)) {
            throw new MessageProcessingDelayedException("Lock acquisition failed in class " + getClass().getSimpleName());
        }
    }

    private void updateFields(
        LocalDateTime timestamp,
        MinorFaction minorFaction,
        FactionStateEnum economicState,
        Double influence,
        Allegiance allegiance,
        List<FactionState> activeStates,
        List<FactionState> pendingStates,
        List<FactionState> recoveringStates
    ) {
        LocalDateTime lastUpdated = lastUpdateDao.findByIdOrDefault(minorFaction.getId(), ObjectType.MINOR_FACTION)
            .getLastUpdate();
        if (timestamp.isBefore(lastUpdated)) {
            log.debug("MinorFaction {} has newer data than {}", minorFaction.getId(), timestamp);
            return;
        }

        saveLastUpdate(timestamp, minorFaction);

        List.of(
                new UpdateHelper(economicState, minorFaction::getState, () -> minorFaction.setState(economicState)),
                new UpdateHelper(influence, minorFaction::getInfluence, () -> minorFaction.setInfluence(influence)),
                new UpdateHelper(allegiance, minorFaction::getAllegiance, () -> minorFaction.setAllegiance(allegiance)),
                new UpdateHelper(() -> isNull(activeStates), () -> minorFaction.setActiveStates(LazyLoadedField.loaded(minorFactionStateFactory.create(minorFaction.getId(), StateStatus.ACTIVE, activeStates)))),
                new UpdateHelper(() -> isNull(pendingStates), () -> minorFaction.setPendingStates(LazyLoadedField.loaded(minorFactionStateFactory.create(minorFaction.getId(), StateStatus.PENDING, pendingStates)))),
                new UpdateHelper(() -> isNull(recoveringStates), () -> minorFaction.setRecoveringStates(LazyLoadedField.loaded(minorFactionStateFactory.create(minorFaction.getId(), StateStatus.RECOVERING, recoveringStates))))
            )
            .forEach(UpdateHelper::modify);

        minorFactionDao.save(minorFaction);
    }

    private void saveLastUpdate(LocalDateTime timestamp, MinorFaction minorFaction) {
        LastUpdate lastUpdate = lastUpdateFactory.create(minorFaction.getId(), ObjectType.MINOR_FACTION, timestamp);
        lastUpdateDao.save(lastUpdate);
    }
}
