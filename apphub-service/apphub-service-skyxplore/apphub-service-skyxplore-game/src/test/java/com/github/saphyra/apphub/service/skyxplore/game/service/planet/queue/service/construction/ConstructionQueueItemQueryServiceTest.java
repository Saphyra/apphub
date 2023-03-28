package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConstructionQueueItemQueryServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private BuildingConstructionToQueueItemConverter converter;

    @InjectMocks
    private ConstructionQueueItemQueryService underTest;

    @Mock
    private Construction construction;

    @Mock
    private QueueItem queueItem;

    @Mock
    private Constructions constructions;

    @Mock
    private GameData gameData;

    @Test
    public void getQueue() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.getByLocationAndType(LOCATION, ConstructionType.CONSTRUCTION)).willReturn(List.of(construction));

        given(converter.convert(gameData, construction)).willReturn(queueItem);

        List<QueueItem> result = underTest.getQueue(gameData, LOCATION);

        assertThat(result).containsExactly(queueItem);
    }
}