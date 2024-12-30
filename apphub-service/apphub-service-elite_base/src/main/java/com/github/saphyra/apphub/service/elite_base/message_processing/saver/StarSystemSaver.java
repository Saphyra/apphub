package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystemFactory;
import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystemPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StarSystemSaver {
    private final StarSystemDao starSystemDao;
    private final StarSystemFactory starSystemFactory;

    public synchronized StarSystem save(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition) {
        if (isNull(starId) && isNull(starName)) {
            throw new IllegalArgumentException("Both starId and starName are null.");
        }

        StarSystem starSystem = Stream.of(
                new SearchHelper<>(starId, () -> starSystemDao.findByStarId(starId)),
                new SearchHelper<>(starName, () -> starSystemDao.findByStarName(starName))
            )
            .map(SearchHelper::search)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .peek(ss -> log.info("Found: {}", ss))
            .findAny()
            .orElseGet(() -> {
                StarSystem created = starSystemFactory.create(timestamp, starId, starName, starPosition);
                log.info("Saving new {}", created);
                starSystemDao.save(created);
                return created;
            });

        updateMissingFields(timestamp, starSystem, starId, starName, starPosition);

        return starSystem;
    }

    private void updateMissingFields(LocalDateTime timestamp, StarSystem starSystem, Long starId, String starName, Double[] starPosition) {
        if (timestamp.isBefore(starSystem.getLastUpdate())) {
            log.info("StarSystem {} has newer data than {}", starSystem.getId(), timestamp);
            return;
        }

        StarSystemPosition starSystemPosition = StarSystemPosition.parse(starPosition);

        List.of(
                new UpdateHelper(new Checker(timestamp, starSystem::getLastUpdate), () -> starSystem.setLastUpdate(timestamp)),
                new UpdateHelper(new Checker(starId, starSystem::getStarId), () -> starSystem.setStarId(starId)),
                new UpdateHelper(new Checker(starName, starSystem::getStarName), () -> starSystem.setStarName(starName)),
                new UpdateHelper(new Checker(starSystemPosition, starSystem::getPosition), () -> starSystem.setPosition(starSystemPosition))
            )
            .forEach(UpdateHelper::modify);

        starSystemDao.save(starSystem);
    }
}
