package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BuildingConstructionToQueueItemConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 2354;
    private static final Integer CURRENT_WORK_POINTS = 365;
    private static final Integer PRIORITY = 3245;
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 46;

    @InjectMocks
    private BuildingConstructionToQueueItemConverter underTest;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Test
    public void convert() {
        given(building.getConstruction()).willReturn(construction);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(construction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(construction.getPriority()).willReturn(PRIORITY);
        given(building.getDataId()).willReturn(DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);

        QueueItem result = underTest.convert(building);

        assertThat(result.getItemId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getType()).isEqualTo(QueueItemType.CONSTRUCTION);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData()).containsEntry("dataId", DATA_ID);
        assertThat(result.getData()).containsEntry("currentLevel", LEVEL);
    }
}