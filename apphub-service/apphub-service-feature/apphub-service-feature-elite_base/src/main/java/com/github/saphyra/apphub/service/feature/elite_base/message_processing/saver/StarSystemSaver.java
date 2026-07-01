package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarType;
import com.google.common.util.concurrent.Striped;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemSaver {
    private static final Striped<Lock> STAR_NAME_LOCK = Striped.lock(8);

    private final StarSystemDao starSystemDao;
    private final StarSystemFactory starSystemFactory;
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;

    public StarSystem save(LocalDateTime timestamp, String starName) {
        return save(timestamp, null, starName, null);
    }

    public StarSystem save(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition) {
        return save(timestamp, starId, starName, starPosition, null);
    }

    public StarSystem save(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition, StarType starType) {
        if (isNull(starName)) {
            throw new IllegalArgumentException("starName must not be null.");
        }

        Lock starNameLock = STAR_NAME_LOCK.get(starName);
        lock(starNameLock);

        log.debug("Saving starSystem {}", starName);

        try {
            Optional<StarSystem> maybeStarSystem = starSystemDao.findByStarName(starName);
            if (maybeStarSystem.isPresent()) {
                StarSystem starSystem = maybeStarSystem.get();

                updateFields(timestamp, starSystem, starId, starName, starPosition, starType);

                return starSystem;
            } else {
                StarSystem created = starSystemFactory.create(starId, starName, starPosition, starType);
                log.debug("Saving new {}", created);

                starSystemDao.save(created);

                saveLastUpdate(timestamp, created);

                return created;
            }
        } finally {
            starNameLock.unlock();
        }
    }

    @SneakyThrows
    private void lock(Lock lock) {
        if (!lock.tryLock(30, TimeUnit.SECONDS)) {
            throw new MessageProcessingDelayedException("Lock acquisition failed in class " + getClass().getSimpleName());
        }
    }

    private void updateFields(LocalDateTime timestamp, StarSystem starSystem, Long starId, String starName, Double[] starPosition, StarType starType) {
        Optional<LocalDateTime> lastUpdated = lastUpdateDao.findById(starSystem.getId(), ObjectType.STAR_SYSTEM)
            .map(LastUpdate::getLastUpdate);
        if (lastUpdated.isPresent() && timestamp.isBefore(lastUpdated.get())) {
            log.debug("StarSystem {} has newer data than {}", starSystem.getId(), timestamp);
            return;
        }

        saveLastUpdate(timestamp, starSystem);

        StarSystemPosition starSystemPosition = StarSystemPosition.parse(starPosition);

        List<Boolean> results = Stream.of(
                new UpdateHelper(new DefaultChecker(starId, starSystem::getStarId), () -> starSystem.setStarId(starId)),
                new UpdateHelper(new DefaultChecker(starName, starSystem::getStarName), () -> starSystem.setStarName(starName)),
                new UpdateHelper(new DefaultChecker(starSystemPosition, starSystem::getPosition), () -> starSystem.setPosition(starSystemPosition)),
                new UpdateHelper(new DefaultChecker(starType, starSystem::getStarType), () -> starSystem.setStarType(starType))
            )
            .map(UpdateHelper::modify)
            .toList();

        if (results.stream().anyMatch(Boolean::booleanValue)) {
            starSystemDao.save(starSystem);
        }
    }

    private void saveLastUpdate(LocalDateTime timestamp, StarSystem starSystem) {
        lastUpdateDao.save(lastUpdateFactory.create(starSystem.getId(), ObjectType.STAR_SYSTEM, timestamp));
    }
}
