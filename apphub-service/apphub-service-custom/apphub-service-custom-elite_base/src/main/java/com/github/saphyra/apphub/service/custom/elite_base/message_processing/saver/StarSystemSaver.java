package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemPosition;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSystemSaver {
    private final StarSystemDao starSystemDao;
    private final StarSystemFactory starSystemFactory;
    private final ErrorReporterService errorReporterService;

    public StarSystem save(LocalDateTime timestamp, String systemName) {
        return save(timestamp, null, systemName, null);
    }

    public  StarSystem save(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition) {
        return save(timestamp, starId, starName, starPosition, null);
    }

    public synchronized StarSystem save(LocalDateTime timestamp, Long starId, String starName, Double[] starPosition, StarType starType) {
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
            .peek(ss -> log.debug("Found: {}", ss))
            .findAny()
            .orElseGet(() -> {
                StarSystem created = starSystemFactory.create(timestamp, starId, starName, starPosition, starType);
                log.debug("Saving new {}", created);
                starSystemDao.save(created);
                return created;
            });

        updateMissingFields(timestamp, starSystem, starId, starName, starPosition, starType);

        return starSystem;
    }

    private void updateMissingFields(LocalDateTime timestamp, StarSystem starSystem, Long starId, String starName, Double[] starPosition, StarType starType) {
        if (timestamp.isBefore(starSystem.getLastUpdate())) {
            log.debug("StarSystem {} has newer data than {}", starSystem.getId(), timestamp);
            return;
        }

        StarSystemPosition starSystemPosition = StarSystemPosition.parse(starPosition);

        List.of(
                new UpdateHelper(new DefaultChecker(timestamp, starSystem::getLastUpdate), () -> starSystem.setLastUpdate(timestamp)),
                new UpdateHelper(new DefaultChecker(starId, starSystem::getStarId), () -> starSystem.setStarId(starId)),
                new UpdateHelper(
                    new DefaultChecker(starName, starSystem::getStarName),
                    () -> {
                        if(!isBlank(starSystem.getStarName())){
                            errorReporterService.report(
                                "Overwriting not-blank star name. New starName: %s. Object: %s".formatted(
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
