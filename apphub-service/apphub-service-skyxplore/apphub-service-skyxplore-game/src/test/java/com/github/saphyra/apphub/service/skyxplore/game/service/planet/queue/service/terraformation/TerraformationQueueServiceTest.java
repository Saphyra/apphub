package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform.CancelTerraformationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TerraformationQueueServiceTest {
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 2345;

    @Mock
    private TerraformationQueueItemQueryService terraformationQueueItemQueryService;

    @Mock
    private CancelTerraformationService cancelTerraformationService;

    @Mock
    private TerraformationQueueItemPriorityUpdateService priorityUpdateService;

    @InjectMocks
    private TerraformationQueueService underTest;

    @Mock
    private Planet planet;

    @Mock
    private QueueItem queueItem;

    @Test
    public void getQueue() {
        given(terraformationQueueItemQueryService.getQueue(planet)).willReturn(List.of(queueItem));

        List<QueueItem> result = underTest.getQueue(planet);

        assertThat(result).containsExactly(queueItem);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(QueueItemType.TERRAFORMATION);
    }

    @Test
    public void setPriority() {
        underTest.setPriority(USER_ID, PLANET_ID, ITEM_ID, PRIORITY);

        verify(priorityUpdateService).updatePriority(USER_ID, PLANET_ID, ITEM_ID, PRIORITY);
    }

    @Test
    public void cancel() {
        underTest.cancel(USER_ID, PLANET_ID, ITEM_ID);

        verify(cancelTerraformationService).cancelTerraformationOfConstruction(USER_ID, PLANET_ID, ITEM_ID);
    }
}