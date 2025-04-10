package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body.BodyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@Component
@RequiredArgsConstructor
@Slf4j
public class BodySaver {
    private final BodyDao bodyDao;
    private final BodyFactory bodyFactory;
    private final ErrorReporterService errorReporterService;

    public Body save(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName) {
        return save(timestamp, starSystemId, bodyType, bodyId, bodyName, null);
    }

    public Optional<Body> saveOptional(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName, Double distanceFromStar) {
        if ((isNull(starSystemId) || isNull(bodyId)) && isNull(bodyName)) {
            return Optional.empty();
        }

        return Optional.of(save(timestamp, starSystemId, bodyType, bodyId, bodyName, distanceFromStar));
    }

    public synchronized Body save(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName, Double distanceFromStar) {
        if (isNull(bodyName)) {
            throw new IllegalArgumentException("BodyName must not be null");
        }

        Body body = bodyDao.findByBodyName(bodyName)
            .orElseGet(() -> {
                Body created = bodyFactory.create(timestamp, starSystemId, bodyType, bodyId, bodyName, distanceFromStar);
                log.debug("Saving new {}", created);
                bodyDao.save(created);
                return created;
            });

        if (nonNull(starSystemId) && nonNull(body.getStarSystemId()) && !Objects.equals(starSystemId, body.getStarSystemId())) {
            errorReporterService.report("StarSystemId mismatch. New starSystemId: %s, %s".formatted(starSystemId, body));
        }

        if (nonNull(bodyId) && nonNull(body.getBodyId()) && !Objects.equals(bodyId, body.getBodyId())) {
            errorReporterService.report("BodyId mismatch. New bodyId: %s, %s".formatted(bodyId, body));
        }

        updateFields(timestamp, body, starSystemId, bodyType, bodyId, bodyName, distanceFromStar);

        return body;
    }

    private void updateFields(LocalDateTime timestamp, Body body, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName, Double distanceFromStar) {
        if (timestamp.isBefore(body.getLastUpdate())) {
            log.debug("Body {} has newer data than {}", body.getId(), timestamp);
            return;
        }

        List.of(
                new UpdateHelper(timestamp, body::getLastUpdate, () -> body.setLastUpdate(timestamp)),
                new UpdateHelper(starSystemId, body::getStarSystemId, () -> body.setStarSystemId(starSystemId)),
                new UpdateHelper(bodyType, body::getType, () -> body.setType(bodyType)),
                new UpdateHelper(bodyId, body::getBodyId, () -> body.setBodyId(bodyId)),
                new UpdateHelper(bodyName, body::getBodyName, () -> body.setBodyName(bodyName)),
                new UpdateHelper(distanceFromStar, body::getDistanceFromStar, () -> body.setDistanceFromStar(distanceFromStar))
            )
            .forEach(UpdateHelper::modify);

        bodyDao.save(body);
    }
}
