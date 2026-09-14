package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.feature.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import com.google.common.util.concurrent.Striped;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

import static java.util.Objects.isNull;


@Component
@RequiredArgsConstructor
@Slf4j
public class BodySaver {
    private static final Striped<Lock> BODY_NAME_LOCK = Striped.lock(8);

    private final BodyDao bodyDao;
    private final BodyFactory bodyFactory;
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;

    public Body save(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName) {
        return save(timestamp, starSystemId, bodyType, bodyId, bodyName, null);
    }

    public Optional<Body> saveOptional(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName, Double distanceFromStar) {
        if (isNull(bodyName)) {
            return Optional.empty();
        }

        return Optional.of(save(timestamp, starSystemId, bodyType, bodyId, bodyName, distanceFromStar));
    }

    public Body save(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName, Double distanceFromStar) {
        if (isNull(bodyName)) {
            throw new IllegalArgumentException("BodyName must not be null");
        }

        Lock bodyNameLock = BODY_NAME_LOCK.get(bodyName);
        lock(bodyNameLock);

        log.debug("Saving body {}", bodyName);

        try {
            Optional<Body> maybeBody = bodyDao.findByBodyName(bodyName);
            if (maybeBody.isPresent()) {
                Body body = maybeBody.get();

                updateFields(timestamp, body, starSystemId, bodyType, bodyId, bodyName, distanceFromStar);

                return body;
            } else {
                Body created = bodyFactory.create(starSystemId, bodyType, bodyId, bodyName, distanceFromStar);
                log.debug("Saving new {}", created);

                bodyDao.save(created);

                saveLastUpdate(timestamp, created);

                return created;
            }
        } finally {
            bodyNameLock.unlock();
        }
    }

    @SneakyThrows
    private void lock(Lock lock) {
        if (!lock.tryLock(30, TimeUnit.SECONDS)) {
            throw new MessageProcessingDelayedException("Lock acquisition failed in class " + getClass().getSimpleName());
        }
    }

    private void updateFields(LocalDateTime timestamp, Body body, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName, Double distanceFromStar) {
        Optional<LocalDateTime> lastUpdated = lastUpdateDao.findById(body.getId(), ObjectType.BODY)
            .map(LastUpdate::getLastUpdate);
        if (lastUpdated.isPresent() && timestamp.isBefore(lastUpdated.get())) {
            log.debug("Body {} has newer data than {}", body.getId(), timestamp);
            return;
        }

        saveLastUpdate(timestamp, body);

        List<Boolean> results = Stream.of(
                new UpdateHelper(starSystemId, body::getStarSystemId, () -> body.setStarSystemId(starSystemId)),
                new UpdateHelper(bodyType, body::getType, () -> body.setType(bodyType)),
                new UpdateHelper(bodyId, body::getBodyId, () -> body.setBodyId(bodyId)),
                new UpdateHelper(bodyName, body::getBodyName, () -> body.setBodyName(bodyName)),
                new UpdateHelper(distanceFromStar, body::getDistanceFromStar, () -> body.setDistanceFromStar(distanceFromStar))
            )
            .map(UpdateHelper::modify)
            .toList();

        if (results.stream().anyMatch(Boolean::booleanValue)) {
            bodyDao.save(body);
        }
    }

    private void saveLastUpdate(LocalDateTime timestamp, Body body) {
        lastUpdateDao.save(lastUpdateFactory.create(body.getId(), ObjectType.BODY, timestamp));
    }
}
