package com.github.saphyra.apphub.service.elite_base.message_processing.saver;

import com.github.saphyra.apphub.service.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.elite_base.dao.body.BodyFactory;
import com.github.saphyra.apphub.service.elite_base.dao.body.BodyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BodySaver {
    private final BodyDao bodyDao;
    private final BodyFactory bodyFactory;

    public Body save(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName) {
        return save(timestamp, starSystemId, bodyType, bodyId, bodyName, null);
    }

    public synchronized Optional<Body> saveOptional(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName, Double distanceFromStar) {
        if ((isNull(starSystemId) || isNull(bodyId)) && isNull(bodyName)) {
            return Optional.empty();
        }

        return Optional.of(save(timestamp, starSystemId, bodyType, bodyId, bodyName, distanceFromStar));
    }

    public synchronized Body save(LocalDateTime timestamp, UUID starSystemId, BodyType bodyType, Long bodyId, String bodyName, Double distanceFromStar) {
        if ((isNull(starSystemId) || isNull(bodyId)) && isNull(bodyName)) {
            throw new IllegalArgumentException("StartSystemId or bodyId and bodyName are null. Cannot identify body location.");
        }

        Body body = Stream.of(
                new SearchHelper<>(() -> nonNull(starSystemId) && nonNull(bodyId), () -> bodyDao.findByStarSystemIdAndBodyId(starSystemId, bodyId)),
                new SearchHelper<>(bodyName, () -> bodyDao.findByBodyName(bodyName))
            )
            .map(SearchHelper::search)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .peek(ss -> log.debug("Found: {}", ss))
            .findAny()
            .orElseGet(() -> {
                Body created = bodyFactory.create(timestamp, starSystemId, bodyType, bodyId, bodyName, distanceFromStar);
                log.debug("Saving new {}", created);
                bodyDao.save(created);
                return created;
            });

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
