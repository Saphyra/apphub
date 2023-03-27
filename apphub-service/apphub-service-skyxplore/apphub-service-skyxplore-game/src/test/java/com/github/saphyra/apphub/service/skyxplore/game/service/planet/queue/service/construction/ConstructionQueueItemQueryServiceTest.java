package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConstructionQueueItemQueryServiceTest {
    @Mock
    private BuildingConstructionToQueueItemConverter converter;

    @InjectMocks
    private ConstructionQueueItemQueryService underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private QueueItem queueItem;

    @Test
    public void getQueue() {
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.toMap(GameConstants.ORIGO, surface)));
        given(surface.getBuilding()).willReturn(building);
        given(building.getConstruction()).willReturn(construction);
        given(converter.convert(building)).willReturn(queueItem);

        List<QueueItem> result = underTest.getQueue(planet);

        assertThat(result).containsExactly(queueItem);
    }
}