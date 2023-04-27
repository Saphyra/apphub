package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.deconstruction;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
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
class BuildingDeconstructionToQueueItemConverterTest {
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 467;
    private static final Integer PRIORITY = 43;
    private static final int CURRENT_WORK_POINTS = 2;
    private static final String DATA_ID = "data-id";
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private BuildingDeconstructionToQueueItemConverter underTest;

    @Mock
    private Building building;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private DeconstructionProperties deconstructionProperties;

    @Mock
    private Buildings buildings;

    @Mock
    private GameData gameData;

    @Test
    void convert() {
        given(gameData.getBuildings()).willReturn(buildings);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_ID);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(building.getDataId()).willReturn(DATA_ID);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(gameProperties.getDeconstruction()).willReturn(deconstructionProperties);
        given(deconstructionProperties.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(deconstruction.getPriority()).willReturn(PRIORITY);
        given(deconstruction.getCurrentWorkPoints()).willReturn(CURRENT_WORK_POINTS);

        QueueItem result = underTest.convert(gameData, deconstruction);

        assertThat(result.getItemId()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getType()).isEqualTo(QueueItemType.DECONSTRUCTION);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getData()).containsEntry("dataId", DATA_ID);
    }
}