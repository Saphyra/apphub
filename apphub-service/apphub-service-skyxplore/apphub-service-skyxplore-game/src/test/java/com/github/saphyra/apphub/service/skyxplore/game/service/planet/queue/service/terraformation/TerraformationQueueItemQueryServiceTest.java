package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TerraformationQueueItemQueryServiceTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 345;
    private static final Integer CURRENT_WORK_POINTS = 75;
    private static final Integer PRIORITY = 2;

    @InjectMocks
    private TerraformationQueueItemQueryService underTest;

    @Mock
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Construction construction;

    @Test
    public void getQueue() {
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface)));
        given(surface.getTerraformation()).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(construction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(construction.getPriority()).willReturn(PRIORITY);
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(construction.getData()).willReturn(SurfaceType.DESERT.name());

        List<QueueItem> result = underTest.getQueue(planet);

        assertThat(result).hasSize(1);
        QueueItem queueItem = result.get(0);
        assertThat(queueItem.getItemId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(queueItem.getType()).isEqualTo(QueueItemType.TERRAFORMATION);
        assertThat(queueItem.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(queueItem.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(queueItem.getPriority()).isEqualTo(PRIORITY);
        assertThat(queueItem.getData()).hasSize(2);
        assertThat(queueItem.getData()).containsEntry("currentSurfaceType", SurfaceType.CONCRETE.name());
        assertThat(queueItem.getData()).containsEntry("targetSurfaceType", SurfaceType.DESERT.name());
    }
}