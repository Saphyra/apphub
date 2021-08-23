package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetOverviewResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlanetOverviewQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String PLANET_NAME = "planet-name";

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private PlanetOverviewQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Before
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
    }

    @Test
    public void getDefaultName() {
        given(planet.getCustomNames()).willReturn(new OptionalHashMap<>());
        given(planet.getDefaultName()).willReturn(PLANET_NAME);

        PlanetOverviewResponse result = underTest.getOverview(USER_ID, PLANET_ID);

        assertThat(result.getPlanetName()).isEqualTo(PLANET_NAME);
    }

    @Test
    public void getCustomName() {
        given(planet.getCustomNames()).willReturn(new OptionalHashMap<>(CollectionUtils.singleValueMap(USER_ID, PLANET_NAME)));

        PlanetOverviewResponse result = underTest.getOverview(USER_ID, PLANET_ID);

        assertThat(result.getPlanetName()).isEqualTo(PLANET_NAME);
    }
}