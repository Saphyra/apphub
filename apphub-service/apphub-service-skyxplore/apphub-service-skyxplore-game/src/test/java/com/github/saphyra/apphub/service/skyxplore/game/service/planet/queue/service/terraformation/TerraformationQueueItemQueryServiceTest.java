package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
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
class TerraformationQueueItemQueryServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private TerraformationToQueueItemConverter terraformationToQueueItemConverter;

    @InjectMocks
    private TerraformationQueueItemQueryService underTest;

    @Mock
    private Surface surface;

    @Mock
    private Construction construction;

    @Mock
    private QueueItem queueItem;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Surfaces surfaces;

    @Test
    void getQueue() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.getByLocationAndType(LOCATION, ConstructionType.TERRAFORMATION)).willReturn(List.of(construction));
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(construction.getExternalReference()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(terraformationToQueueItemConverter.convert(construction, surface)).willReturn(queueItem);

        List<QueueItem> result = underTest.getQueue(gameData, LOCATION);

        assertThat(result).containsExactly(queueItem);
    }
}