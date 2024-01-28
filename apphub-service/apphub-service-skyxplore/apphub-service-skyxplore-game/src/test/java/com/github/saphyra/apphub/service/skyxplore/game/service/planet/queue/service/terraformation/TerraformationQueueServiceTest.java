package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.terraform.CancelTerraformationService;
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
    private QueueItem queueItem;

    @Mock
    private GameData gameData;

    @Test
    public void getQueue() {
        given(terraformationQueueItemQueryService.getQueue(gameData, PLANET_ID)).willReturn(List.of(queueItem));

        List<QueueItem> result = underTest.getQueue(gameData, PLANET_ID);

        assertThat(result).containsExactly(queueItem);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(QueueItemType.TERRAFORMATION);
    }

    @Test
    public void setPriority() {
        underTest.setPriority(USER_ID, PLANET_ID, ITEM_ID, PRIORITY);

        verify(priorityUpdateService).updatePriority(USER_ID, ITEM_ID, PRIORITY);
    }

    @Test
    public void cancel() {
        underTest.cancel(USER_ID, PLANET_ID, ITEM_ID);

        verify(cancelTerraformationService).cancelTerraformationQueueItem(USER_ID, ITEM_ID);
    }
}