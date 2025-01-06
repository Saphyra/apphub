package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.dao.SecurityLevel;
import com.github.saphyra.apphub.service.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.Power;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.PowerplayState;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.StarSystemData;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.StarSystemDataDao;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.StarSystemDataFactory;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.WarStatus;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.WarType;
import com.github.saphyra.apphub.service.elite_base.dao.star_system_data.conflict.minor_faction.ConflictingMinorFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ConflictFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.EdConflict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StarSystemDataSaver {
    private final StarSystemDataDao starSystemDataDao;
    private final StarSystemDataFactory starSystemDataFactory;
    private final IdGenerator idGenerator;
    private final MinorFactionSaver minorFactionSaver;

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
        EdConflict[] conflictsArr
    ) {
        List<MinorFactionConflict> conflicts = mapConflicts(timestamp, starSystemId, conflictsArr, minorFactions);

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
                    conflicts
                );
                log.debug("Created: {}", created);
                starSystemDataDao.save(created);
                return created;
            });

        updateFields(timestamp, starSystemData, population, allegiance, economy, secondaryEconomy, securityLevel, power, powerplayState, minorFactions, controllingFaction, powers, conflicts);
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
        List<MinorFactionConflict> conflicts
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
            .map(controllingFactionName -> getMinorFactionId(timestamp, controllingFactionName, minorFactions))
            .orElse(null);
        FactionStateEnum controllingFactionState = Optional.ofNullable(controllingFaction)
            .map(ControllingFaction::getEconomicState)
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
                new UpdateHelper(powerplayState, starSystemData::getPowerplayState, () -> starSystemData.setPowerplayState(powerplayState)),
                new UpdateHelper(minorFactionIds, starSystemData::getMinorFactions, () -> starSystemData.setMinorFactions(LazyLoadedField.loaded(minorFactionIds))),
                new UpdateHelper(controllingFactionId, starSystemData::getControllingFactionId, () -> starSystemData.setControllingFactionId(controllingFactionId)),
                new UpdateHelper(controllingFactionState, starSystemData::getControllingFactionState, () -> starSystemData.setControllingFactionState(controllingFactionState)),
                new UpdateHelper(powers, starSystemData::getPowers, () -> starSystemData.setPowers(LazyLoadedField.loaded(powers))),
                new UpdateHelper(conflicts, starSystemData::getConflicts, () -> starSystemData.setConflicts(LazyLoadedField.loaded(conflicts)))
            )
            .forEach(UpdateHelper::modify);

        starSystemDataDao.save(starSystemData);
    }

    private List<MinorFactionConflict> mapConflicts(LocalDateTime timestamp, UUID starSystemId, EdConflict[] conflictsArr, List<MinorFaction> minorFactions) {
        if (isNull(conflictsArr)) {
            return null;
        }

        return Arrays.stream(conflictsArr)
            .map(edConflict -> mapConflict(timestamp, starSystemId, edConflict, minorFactions))
            .toList();
    }

    private MinorFactionConflict mapConflict(LocalDateTime timestamp, UUID starSystemId, EdConflict edConflict, List<MinorFaction> minorFactions) {
        UUID conflictId = idGenerator.randomUuid();
        return MinorFactionConflict.builder()
            .id(conflictId)
            .starSystemId(starSystemId)
            .status(WarStatus.parse(edConflict.getStatus()))
            .warType(WarType.parse(edConflict.getWarType()))
            .conflictingMinorFactions(List.of(
                mapFaction(timestamp, conflictId, edConflict.getFaction1(), minorFactions),
                mapFaction(timestamp, conflictId, edConflict.getFaction2(), minorFactions)
            ))
            .build();
    }

    private ConflictingMinorFaction mapFaction(LocalDateTime timestamp, UUID conflictId, ConflictFaction faction, List<MinorFaction> minorFactions) {
        return ConflictingMinorFaction.builder()
            .conflictId(conflictId)
            .factionId(getMinorFactionId(timestamp, faction.getFactionName(), minorFactions))
            .wonDays(faction.getWonDays())
            .stake(faction.getStake())
            .build();
    }

    private UUID getMinorFactionId(LocalDateTime timestamp, String factionName, List<MinorFaction> minorFactions) {
        minorFactions = isNull(minorFactions) ? Collections.emptyList() : minorFactions;

        return minorFactions.stream()
            .filter(minorFaction -> minorFaction.getFactionName().equalsIgnoreCase(factionName))
            .findAny()
            .map(MinorFaction::getId)
            .orElseGet(() -> minorFactionSaver.save(timestamp, factionName, null).getId());
    }
}
