package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SurfaceLoaderTest {
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private SurfaceLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private SurfaceModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.SURFACE);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(SurfaceModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getSurfaces()).willReturn(surfaces);

        underTest.addToGameData(gameData, List.of(surface));

        verify(surfaces).addAll(List.of(surface));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(SURFACE_ID);
        given(model.getPlanetId()).willReturn(PLANET_ID);
        given(model.getSurfaceType()).willReturn(SurfaceType.CONCRETE.name());

        Surface result = underTest.convert(model);

        assertThat(result.getSurfaceId()).isEqualTo(SURFACE_ID);
        assertThat(result.getPlanetId()).isEqualTo(PLANET_ID);
        assertThat(result.getSurfaceType()).isEqualTo(SurfaceType.CONCRETE);
    }
}