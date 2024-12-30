package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.elite_base.dao.minor_faction.FactionState;
import com.github.saphyra.apphub.service.elite_base.dao.minor_faction.MinorFaction;
import com.github.saphyra.apphub.service.elite_base.dao.minor_faction.MinorFactionDao;
import com.github.saphyra.apphub.service.elite_base.dao.minor_faction.MinorFactionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.micrometer.common.util.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MinorFactionSaver {
    private final MinorFactionDao minorFactionDao;
    private final MinorFactionFactory minorFactionFactory;

    public synchronized Optional<MinorFaction> save(LocalDateTime timestamp, String factionName, FactionState economicState) {
        if (isBlank(factionName)) {
            return Optional.empty();
        }

        MinorFaction minorFaction = minorFactionDao.findByFactionName(factionName)
            .stream()
            .peek(ss -> log.info("Found: {}", ss))
            .findAny()
            .orElseGet(() -> {
                MinorFaction created = minorFactionFactory.create(timestamp, factionName, economicState);
                log.info("Saving new {}", created);
                minorFactionDao.save(created);
                return created;
            });

        updateFields(timestamp, minorFaction, economicState);

        return Optional.of(minorFaction);
    }

    private void updateFields(LocalDateTime timestamp, MinorFaction minorFaction, FactionState economicState) {
        if (timestamp.isBefore(minorFaction.getLastUpdate())) {
            log.info("MinorFaction {} has newer data than {}", minorFaction.getId(), timestamp);
            return;
        }

        List.of(
                new UpdateHelper(timestamp, minorFaction::getLastUpdate, () -> minorFaction.setLastUpdate(timestamp)),
                new UpdateHelper(timestamp, minorFaction::getState, () -> minorFaction.setState(economicState))
            )
            .forEach(UpdateHelper::modify);

        minorFactionDao.save(minorFaction);
    }
}
