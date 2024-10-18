package com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.StorageDetailsResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PlanetStorageOverviewQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameDao gameDao;

    @Mock
    private PlanetStorageDetailQueryService planetStorageDetailQueryService;

    @InjectMocks
    private PlanetStorageOverviewQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private StorageDetailsResponse energyStorageDetailsResponse;

    @Mock
    private StorageDetailsResponse liquidStorageDetailsResponse;

    @Mock
    private StorageDetailsResponse bulkStorageDetailsResponse;

    @Test

    public void getStorage() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);

        given(planetStorageDetailQueryService.getStorageDetails(gameData, PLANET_ID, StorageType.CONTAINER)).willReturn(bulkStorageDetailsResponse);
        given(planetStorageDetailQueryService.getStorageDetails(gameData, PLANET_ID, StorageType.ENERGY)).willReturn(energyStorageDetailsResponse);
        given(planetStorageDetailQueryService.getStorageDetails(gameData, PLANET_ID, StorageType.LIQUID)).willReturn(liquidStorageDetailsResponse);

        PlanetStorageResponse result = underTest.getStorage(USER_ID, PLANET_ID);

        assertThat(result.getEnergy()).isEqualTo(energyStorageDetailsResponse);
        assertThat(result.getBulk()).isEqualTo(bulkStorageDetailsResponse);
        assertThat(result.getLiquid()).isEqualTo(liquidStorageDetailsResponse);
    }
}