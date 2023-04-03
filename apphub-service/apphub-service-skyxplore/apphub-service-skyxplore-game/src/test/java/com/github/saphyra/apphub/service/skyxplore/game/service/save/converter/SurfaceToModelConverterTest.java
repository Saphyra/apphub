package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SurfaceToModelConverterTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    private final SurfaceToModelConverter underTest = new SurfaceToModelConverter();

    @Test
    void convert() {
        Surface surface = Surface.builder()
            .surfaceId(SURFACE_ID)
            .planetId(PLANET_ID)
            .surfaceType(SurfaceType.CONCRETE)
            .build();

        SurfaceModel result = underTest.convert(GAME_ID, surface);

        assertThat(result.getId()).isEqualTo(SURFACE_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SURFACE);
        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSurfaceType()).isEqualTo(SurfaceType.CONCRETE.name());
    }
}