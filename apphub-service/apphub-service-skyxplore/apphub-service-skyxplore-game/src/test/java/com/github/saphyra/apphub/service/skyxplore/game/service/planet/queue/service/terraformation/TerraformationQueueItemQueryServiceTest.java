package com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.service.terraformation;

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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TerraformationQueueItemQueryServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private TerraformationToQueueItemConverter converter;

    @InjectMocks
    private TerraformationQueueItemQueryService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private QueueItem queueItem;

    @Test
    void getQueue() {
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.getByPlanetId(LOCATION)).willReturn(List.of(surface));
        given(gameData.getConstructions()).willReturn(constructions);
        given(surface.getSurfaceId()).willReturn(SURFACE_ID);
        given(constructions.findByExternalReference(SURFACE_ID)).willReturn(Optional.of(construction));
        given(converter.convert(gameData, construction, surface)).willReturn(queueItem);

        assertThat(underTest.getQueue(gameData, LOCATION)).containsExactly(queueItem);
    }
}