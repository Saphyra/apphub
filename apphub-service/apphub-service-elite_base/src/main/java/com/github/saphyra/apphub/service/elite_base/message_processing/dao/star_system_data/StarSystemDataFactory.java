package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.SecurityLevel;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system_data.conflict.MinorFactionConflict;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.MinorFactionSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemDataFactory {
    private final MinorFactionSaver minorFactionSaver;

    public StarSystemData create(
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
        List<MinorFactionConflict> conflicts
    ) {
        return StarSystemData.builder()
            .starSystemId(starSystemId)
            .lastUpdate(timestamp)
            .population(population)
            .allegiance(allegiance)
            .economy(economy)
            .secondaryEconomy(secondaryEconomy)
            .securityLevel(securityLevel)
            .controllingPower(power)
            .powerplayState(powerplayState)
            .controllingFactionId(getControllingFaction(timestamp, controllingFaction, minorFactions))
            .controllingFactionState(Optional.ofNullable(controllingFaction).map(cf -> FactionStateEnum.parse(cf.getState())).orElse(null))
            .minorFactions(LazyLoadedField.loaded(Optional.ofNullable(minorFactions).map(factions -> factions.stream().map(MinorFaction::getId).toList()).orElse(null)))
            .powers(LazyLoadedField.loaded(powers))
            .conflicts(LazyLoadedField.loaded(conflicts))
            .build();
    }

    private UUID getControllingFaction(LocalDateTime timestamp, ControllingFaction controllingFaction, List<MinorFaction> minorFactions) {
        if (isNull(controllingFaction)) {
            return null;
        }

        return findMinorFactionId(timestamp, controllingFaction, minorFactions);
    }

    private UUID findMinorFactionId(LocalDateTime timestamp, ControllingFaction controllingFaction, List<MinorFaction> minorFactions) {
        minorFactions = isNull(minorFactions) ? Collections.emptyList() : minorFactions;

        return minorFactions.stream()
            .filter(minorFaction -> minorFaction.getFactionName().equalsIgnoreCase(controllingFaction.getFactionName()))
            .findAny()
            .map(MinorFaction::getId)
            .orElseGet(() -> minorFactionSaver.save(timestamp, controllingFaction.getFactionName(), FactionStateEnum.parse(controllingFaction.getState())).getId());
    }
}
