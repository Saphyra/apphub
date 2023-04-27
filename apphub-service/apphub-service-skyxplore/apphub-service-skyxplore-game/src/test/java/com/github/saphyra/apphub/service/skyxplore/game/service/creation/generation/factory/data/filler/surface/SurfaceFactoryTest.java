package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.surface;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SurfaceFactoryTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private SurfaceFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(SURFACE_ID);

        Surface result = underTest.create(PLANET_ID, SurfaceType.CONCRETE);

        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSurfaceType()).isEqualTo(SurfaceType.CONCRETE);
    }
}