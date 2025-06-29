package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarType;
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

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemSaver {
    private static final Striped<Lock> STAR_NAME_LOCK = Striped.lock(8);

    private final StarSystemDao starSystemDao;
    private final StarSystemFactory starSystemFactory;
    private final ErrorReporterService errorReporterService;

    public StarSystem save(LocalDateTime timestamp, String starName) {
        return save(timestamp, null, starName, null);
    }

    public StarSystem save(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition) {
        return save(timestamp, starId, starName, starPosition, null);
    }

    public StarSystem save(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition, StarType starType) {
        if (isNull(starId) && isNull(starName)) {
            throw new IllegalArgumentException("Both starId and starName are null.");
        }

        Optional<Lock> starNameLock = Optional.ofNullable(starName)
            .map(STAR_NAME_LOCK::get);

        starNameLock.ifPresent(this::lock);

        log.debug("Saving starSystem {}", starName);

        try {
            StarSystem starSystem = starSystemDao.findByStarIdOrStarName(starId, starName)
                .orElseGet(() -> {
                    StarSystem created = starSystemFactory.create(timestamp, starId, starName, starPosition, starType);
                    log.debug("Saving new {}", created);
                    starSystemDao.save(created);
                    return created;
                });

            updateMissingFields(timestamp, starSystem, starId, starName, starPosition, starType);

            log.debug("Saved starSystem {}", starName);

            return starSystem;
        } finally {
            starNameLock.ifPresent(Lock::unlock);
        }
    }

    @SneakyThrows
    private void lock(Lock lock) {
        if (!lock.tryLock(30, TimeUnit.SECONDS)) {
            throw new MessageProcessingDelayedException("Lock acquisition failed in class " + getClass().getSimpleName());
        }
    }

    private void updateMissingFields(LocalDateTime timestamp, StarSystem starSystem, Long starId, String starName, Double[] starPosition, StarType starType) {
        if (timestamp.isBefore(starSystem.getLastUpdate())) {
            log.debug("StarSystem {} has newer data than {}", starSystem.getId(), timestamp);
            return;
        }

        StarSystemPosition starSystemPosition = StarSystemPosition.parse(starPosition);

        List.of(
                new UpdateHelper(new DefaultChecker(timestamp, starSystem::getLastUpdate), () -> starSystem.setLastUpdate(timestamp)),
                new UpdateHelper(
                    new DefaultChecker(starId, starSystem::getStarId),
                    () -> {
                        if (nonNull(starSystem.getStarId())) {
                            errorReporterService.report(
                                "Overwriting non-null starId. New starId: %s Original object: %s".formatted(
                                    starId,
                                    starSystem
                                )
                            );
                        }
                        starSystem.setStarId(starId);
                    }
                ),
                new UpdateHelper(
                    new DefaultChecker(starName, starSystem::getStarName),
                    () -> {
                        if (!isBlank(starSystem.getStarName())) {
                            errorReporterService.report(
                                "Overwriting not-blank star name. New starName: %s. Original object: %s".formatted(
                                    starName,
                                    starSystem
                                )
                            );
                        }
                        starSystem.setStarName(starName);
                    }
                ),
                new UpdateHelper(new DefaultChecker(starSystemPosition, starSystem::getPosition), () -> starSystem.setPosition(starSystemPosition)),
                new UpdateHelper(new DefaultChecker(starType, starSystem::getStarType), () -> starSystem.setStarType(starType))
            )
            .forEach(UpdateHelper::modify);

        starSystemDao.save(starSystem);
    }
}
