package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.custom.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.custom.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.custom.elite_base.dao.SecurityLevel;
import com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.*;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system_data.powerplay_conflict.PowerplayConflict;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.ControllingFaction;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.EdConflict;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.ConflictMapper;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.MinorFactionIdResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemDataSaver {
    private final StarSystemDataDao starSystemDataDao;
    private final StarSystemDataFactory starSystemDataFactory;
    private final MinorFactionIdResolver minorFactionIdResolver;
    private final ConflictMapper conflictMapper;

    public synchronized void save(
        UUID starSystemId,
        LocalDateTime timestamp,
        Long population,
        Allegiance allegiance,
        EconomyEnum economy,
        EconomyEnum secondaryEconomy,
        SecurityLevel securityLevel,
        Power power,
        PowerplayState powerplayState,
        List<MinorFaction> minorFactions,
        ControllingFaction controllingFaction,
        List<Power> powers,
        EdConflict[] conflictsArr,
        Double powerplayStateControlProgress,
        Double powerplayStateReinforcement,
        Double powerplayStateUndermining,
        List<PowerplayConflict> powerplayConflicts
    ) {
        List<MinorFactionConflict> conflicts = conflictMapper.mapConflicts(timestamp, starSystemId, conflictsArr, minorFactions);

        StarSystemData starSystemData = starSystemDataDao.findById(starSystemId)
            .orElseGet(() -> {
                StarSystemData created = starSystemDataFactory.create(
                    starSystemId,
                    timestamp,
                    population,
                    allegiance,
                    economy,
                    secondaryEconomy,
                    securityLevel,
                    power,
                    powerplayState,
                    minorFactions,
                    controllingFaction,
                    powers,
                    conflicts,
                    powerplayStateControlProgress,
                    powerplayStateReinforcement,
                    powerplayStateUndermining,
                    powerplayConflicts
                );
                log.debug("Created: {}", created);
                starSystemDataDao.save(created);
                return created;
            });

        updateFields(
            timestamp,
            starSystemData,
            population,
            allegiance,
            economy,
            secondaryEconomy,
            securityLevel,
            power,
            powerplayState,
            minorFactions,
            controllingFaction,
            powers,
            conflicts,
            powerplayStateControlProgress,
            powerplayStateReinforcement,
            powerplayStateUndermining,
            powerplayConflicts
        );
    }

    private void updateFields(
        LocalDateTime timestamp,
        StarSystemData starSystemData,
        Long population,
        Allegiance allegiance,
        EconomyEnum economy,
        EconomyEnum secondaryEconomy,
        SecurityLevel securityLevel,
        Power power,
        PowerplayState powerplayState,
        List<MinorFaction> minorFactions,
        ControllingFaction controllingFaction,
        List<Power> powers,
        List<MinorFactionConflict> conflicts,
        Double powerplayStateControlProgress,
        Double powerplayStateReinforcement,
        Double powerplayStateUndermining,
        List<PowerplayConflict> powerplayConflicts
    ) {
        if (timestamp.isBefore(starSystemData.getLastUpdate())) {
            log.debug("StarSystemData {} has newer data than {}", starSystemData.getStarSystemId(), timestamp);
            return;
        }

        List<UUID> minorFactionIds = Optional.ofNullable(minorFactions)
            .map(factions -> factions.stream().map(MinorFaction::getId).toList())
            .orElse(null);
        UUID controllingFactionId = Optional.ofNullable(controllingFaction)
            .map(ControllingFaction::getFactionName)
            .map(controllingFactionName -> minorFactionIdResolver.getMinorFactionId(timestamp, controllingFactionName, minorFactions))
            .orElse(null);
        FactionStateEnum controllingFactionState = Optional.ofNullable(controllingFaction)
            .map(ControllingFaction::getState)
            .map(FactionStateEnum::parse)
            .orElse(null);

        List.of(
                new UpdateHelper(timestamp, starSystemData::getLastUpdate, () -> starSystemData.setLastUpdate(timestamp)),
                new UpdateHelper(population, starSystemData::getPopulation, () -> starSystemData.setPopulation(population)),
                new UpdateHelper(allegiance, starSystemData::getAllegiance, () -> starSystemData.setAllegiance(allegiance)),
                new UpdateHelper(economy, starSystemData::getEconomy, () -> starSystemData.setEconomy(economy)),
                new UpdateHelper(secondaryEconomy, starSystemData::getSecondaryEconomy, () -> starSystemData.setSecondaryEconomy(secondaryEconomy)),
                new UpdateHelper(securityLevel, starSystemData::getSecurityLevel, () -> starSystemData.setSecurityLevel(securityLevel)),
                new UpdateHelper(power, starSystemData::getControllingPower, () -> starSystemData.setControllingPower(power)),
                new UpdateHelper(powerplayState, starSystemData::getPowerplayState, () -> starSystemData.setPowerplayState(powerplayState)),
                new UpdateHelper(minorFactionIds, starSystemData::getMinorFactions, () -> starSystemData.setMinorFactions(LazyLoadedField.loaded(minorFactionIds))),
                new UpdateHelper(controllingFactionId, starSystemData::getControllingFactionId, () -> starSystemData.setControllingFactionId(controllingFactionId)),
                new UpdateHelper(controllingFactionState, starSystemData::getControllingFactionState, () -> starSystemData.setControllingFactionState(controllingFactionState)),
                new UpdateHelper(powers, starSystemData::getPowers, () -> starSystemData.setPowers(LazyLoadedField.loaded(powers))),
                new UpdateHelper(conflicts, starSystemData::getConflicts, () -> starSystemData.setConflicts(LazyLoadedField.loaded(conflicts))),
                new UpdateHelper(powerplayStateControlProgress, starSystemData::getPowerplayStateControlProgress, () -> starSystemData.setPowerplayStateControlProgress(powerplayStateControlProgress)),
                new UpdateHelper(powerplayStateReinforcement, starSystemData::getPowerplayStateReinforcement, () -> starSystemData.setPowerplayStateReinforcement(powerplayStateReinforcement)),
                new UpdateHelper(powerplayStateUndermining, starSystemData::getPowerplayStateUndermining, () -> starSystemData.setPowerplayStateUndermining(powerplayStateUndermining)),
                new UpdateHelper(powerplayConflicts, starSystemData::getPowerplayConflicts, () -> starSystemData.setPowerplayConflicts(LazyLoadedField.loaded(powerplayConflicts)))
            )
            .forEach(UpdateHelper::modify);

        starSystemDataDao.save(starSystemData);
    }
}
