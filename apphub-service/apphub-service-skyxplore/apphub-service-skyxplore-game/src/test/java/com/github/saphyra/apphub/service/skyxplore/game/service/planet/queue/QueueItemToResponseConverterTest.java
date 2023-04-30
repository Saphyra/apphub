package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class QueueItemToResponseConverterTest {
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 452;
    private static final Integer CURRENT_WORK_POINTS = 467;
    private static final Integer PRIORITY = 578;
    private static final Integer GLOBAL_PRIORITY = 36;
    private static final UUID LOCATION = UUID.randomUUID();

    @InjectMocks
    private QueueItemToResponseConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Test
    public void convertNull() {
        assertThat(underTest.convert(null, gameData, LOCATION)).isNull();
    }

    @Test
    public void convert() {
        Map<String, Object> data = CollectionUtils.singleValueMap("asd", "dsa");

        QueueItem queueItem = QueueItem.builder()
            .itemId(ITEM_ID)
            .type(QueueItemType.CONSTRUCTION)
            .requiredWorkPoints(REQUIRED_WORK_POINTS)
            .currentWorkPoints(CURRENT_WORK_POINTS)
            .priority(PRIORITY)
            .data(data)
            .build();

        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.CONSTRUCTION)).willReturn(priority);
        given(priority.getValue()).willReturn(GLOBAL_PRIORITY);

        QueueResponse result = underTest.convert(queueItem, gameData, LOCATION);

        assertThat(result.getItemId()).isEqualTo(ITEM_ID);
        assertThat(result.getType()).isEqualTo(QueueItemType.CONSTRUCTION.name());
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getOwnPriority()).isEqualTo(PRIORITY);
        assertThat(result.getTotalPriority()).isEqualTo(PRIORITY * GLOBAL_PRIORITY);
        assertThat(result.getData()).isEqualTo(data);
    }
}