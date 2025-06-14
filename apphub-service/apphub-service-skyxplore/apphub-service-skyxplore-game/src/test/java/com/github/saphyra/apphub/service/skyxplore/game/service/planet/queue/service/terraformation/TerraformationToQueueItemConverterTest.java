package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.QueueItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueItem;
import com.github.saphyra.apphub.service.skyxplore.game.util.WorkPointsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TerraformationToQueueItemConverterTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 345;
    private static final Integer CURRENT_WORK_POINTS = 75;
    private static final Integer PRIORITY = 2;

    @Mock
    private WorkPointsUtil workPointsUtil;

    @InjectMocks
    private TerraformationToQueueItemConverter underTest;

    @Mock
    private Surface surface;

    @Mock
    private Construction construction;

    @Mock
    private GameData gameData;

    @Test
    public void convert() {
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(construction.getPriority()).willReturn(PRIORITY);
        given(surface.getSurfaceType()).willReturn(SurfaceType.CONCRETE);
        given(construction.getData()).willReturn(SurfaceType.DESERT.name());
        given(workPointsUtil.getCompletedWorkPoints(gameData, CONSTRUCTION_ID, ProcessType.TERRAFORMATION)).willReturn(CURRENT_WORK_POINTS);

        QueueItem result = underTest.convert(gameData, construction, surface);

        assertThat(result.getItemId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getType()).isEqualTo(QueueItemType.TERRAFORMATION);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(CURRENT_WORK_POINTS);
        assertThat(result.getPriority()).isEqualTo(PRIORITY);
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData()).containsEntry("currentSurfaceType", SurfaceType.CONCRETE.name());
        assertThat(result.getData()).containsEntry("targetSurfaceType", SurfaceType.DESERT.name());
    }
}