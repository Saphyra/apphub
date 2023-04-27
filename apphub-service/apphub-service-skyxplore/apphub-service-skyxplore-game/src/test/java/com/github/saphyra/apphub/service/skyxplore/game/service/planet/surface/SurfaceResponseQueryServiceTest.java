package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.SurfaceConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
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
public class SurfaceResponseQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private SurfaceConverter surfaceConverter;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private SurfaceResponseQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surface surface;

    @Mock
    private SurfaceResponse surfaceResponse;

    @Mock
    private Surfaces surfaces;

    @Test
    public void getSurfaceOfPlanet() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.getByPlanetId(PLANET_ID)).willReturn(List.of(surface));

        given(surfaceConverter.toResponse(gameData, surface)).willReturn(surfaceResponse);

        List<SurfaceResponse> result = underTest.getSurfaceOfPlanet(USER_ID, PLANET_ID);

        assertThat(result).containsExactly(surfaceResponse);
    }
}