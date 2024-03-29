package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.construction;

import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BuildingConstructionToQueueItemConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 2354;
    private static final Integer CURRENT_WORK_POINTS = 365;
    private static final Integer PRIORITY = 3245;
    private static final String DATA_ID = "data-id";
    private static final Integer LEVEL = 46;
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @InjectMocks
    private BuildingConstructionToQueueItemConverter underTest;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private GameData gameData;

    @Mock
    private Buildings buildings;

    @Test
    public void convert() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(construction.getExternalReference()).willReturn(BUILDING_ID);

        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(construction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);
        given(construction.getPriority()).willReturn(PRIORITY);
        given(building.getDataId()).willReturn(DATA_ID);
        given(building.getLevel()).willReturn(LEVEL);

        QueueItem result = underTest.convert(gameData, construction);

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