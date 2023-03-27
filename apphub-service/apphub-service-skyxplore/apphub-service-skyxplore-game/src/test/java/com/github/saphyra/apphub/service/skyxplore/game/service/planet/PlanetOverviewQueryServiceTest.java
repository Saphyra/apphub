package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetOverviewResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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
    private GameData gameData;

    @Mock
    private Planet planet;

    @BeforeEach
    public void setUp() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(PLANET_ID, planet, new Planets()));
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
        given(planet.getCustomNames()).willReturn(new OptionalHashMap<>(CollectionUtils.toMap(USER_ID, PLANET_NAME)));

        PlanetOverviewResponse result = underTest.getOverview(USER_ID, PLANET_ID);

        assertThat(result.getPlanetName()).isEqualTo(PLANET_NAME);
    }
}