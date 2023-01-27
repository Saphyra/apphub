package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction.CancelConstructionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ConstructionQueueServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 2354;
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private ConstructionQueueItemQueryService constructionQueueItemQueryService;

    @Mock
    private CancelConstructionService cancelConstructionService;

    @Mock
    private ConstructionQueueItemPriorityUpdateService constructionQueueItemPriorityUpdateService;

    @InjectMocks
    private ConstructionQueueService underTest;

    @Mock
    private QueueItem queueItem;

    @Mock
    private Planet planet;

    @Test
    public void getQueue() {
        given(constructionQueueItemQueryService.getQueue(planet)).willReturn(List.of(queueItem));

        List<QueueItem> result = underTest.getQueue(planet);

        assertThat(result).containsExactly(queueItem);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(QueueItemType.CONSTRUCTION);
    }

    @Test
    public void setPriority() {
        underTest.setPriority(USER_ID, PLANET_ID, ITEM_ID, PRIORITY);

        verify(constructionQueueItemPriorityUpdateService).updatePriority(USER_ID, PLANET_ID, ITEM_ID, PRIORITY);
    }

    @Test
    public void cancel() {
        underTest.cancel(USER_ID, PLANET_ID, ITEM_ID);

        verify(cancelConstructionService).cancelConstructionOfConstruction(USER_ID, PLANET_ID, ITEM_ID);
    }
}