package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFactionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.MinorFactionStateFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state.StateStatus;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.Faction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.FactionState;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinorFactionSaver {
    private final MinorFactionDao minorFactionDao;
    private final MinorFactionFactory minorFactionFactory;
    private final MinorFactionStateFactory minorFactionStateFactory;

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
        if (isBlank(factionName)) {
            return null;
        }

        return save(timestamp, factionName, economicState, null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    private synchronized MinorFaction save(
        LocalDateTime timestamp,
        String factionName,
        FactionStateEnum economicState,
        Double influence,
        Allegiance allegiance,
        List<FactionState> activeStates,
        List<FactionState> pendingStates,
        List<FactionState> recoveringStates
    ) {
        MinorFaction minorFaction = minorFactionDao.findByFactionName(factionName)
            .orElseGet(() -> {
                MinorFaction created = minorFactionFactory.create(
                    timestamp,
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
                return created;
            });

        updateFields(timestamp, minorFaction, economicState, influence, allegiance, activeStates, pendingStates, recoveringStates);

        return minorFaction;
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
        if (timestamp.isBefore(minorFaction.getLastUpdate())) {
            log.debug("MinorFaction {} has newer data than {}", minorFaction.getId(), timestamp);
            return;
        }

        List.of(
                new UpdateHelper(timestamp, minorFaction::getLastUpdate, () -> minorFaction.setLastUpdate(timestamp)),
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
}
