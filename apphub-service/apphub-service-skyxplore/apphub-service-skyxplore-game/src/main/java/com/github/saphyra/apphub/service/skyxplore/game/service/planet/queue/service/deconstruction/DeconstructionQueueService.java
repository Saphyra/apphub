package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeconstructionQueueService implements QueueService {
    @Override
    public List<QueueItem> getQueue(Planet planet) {
        return Collections.emptyList(); //TODO implement
    }

    @Override
    public QueueItemType getType() {
        return QueueItemType.DECONSTRUCTION;
    }

    @Override
    public void setPriority(UUID userId, UUID planetId, UUID itemId, Integer priority) {
        //TODO implement
    }

    @Override
    public void cancel(UUID userId, UUID planetId, UUID itemId) {
        //TODO implement
    }
}
