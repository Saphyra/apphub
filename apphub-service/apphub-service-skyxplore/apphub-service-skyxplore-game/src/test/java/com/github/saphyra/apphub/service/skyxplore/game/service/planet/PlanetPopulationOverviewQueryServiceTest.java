package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PlanetPopulationOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StorageCapacityService;
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
public class PlanetPopulationOverviewQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Integer CAPACITY = 123;

    @Mock
    private GameDao gameDao;

    @Mock
    private StorageCapacityService storageCapacityService;

    @InjectMocks
    private PlanetPopulationOverviewQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Citizen citizen;

    @Mock
    private Citizens citizens;

    @Test
    public void getPopulationOverview() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.getByLocation(PLANET_ID)).willReturn(List.of(citizen));

        given(storageCapacityService.calculateDwellingCapacity(gameData, PLANET_ID)).willReturn(CAPACITY);

        PlanetPopulationOverviewResponse result = underTest.getPopulationOverview(USER_ID, PLANET_ID);

        assertThat(result.getCapacity()).isEqualTo(CAPACITY);
        assertThat(result.getPopulation()).isEqualTo(1);
    }
}