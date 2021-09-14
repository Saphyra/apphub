package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system.SolarSystemGeneratorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UniverseFactoryTest {
    private static final int PLAYER_COUNT = 234;
    private static final Integer UNIVERSE_SIZE = 2435;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SolarSystemGeneratorService solarSystemGeneratorService;

    @Mock
    private UniverseSizeCalculator universeSizeCalculator;

    @InjectMocks
    private UniverseFactory underTest;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private CoordinateModel coordinateModel;

    @Test
    public void create() {
        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .universeSize(UniverseSize.SMALL)
            .build();

        given(solarSystemGeneratorService.generateSolarSystems(GAME_ID, PLAYER_COUNT, settings)).willReturn(Arrays.asList(solarSystem));
        given(universeSizeCalculator.calculateUniverseSize(Arrays.asList(solarSystem))).willReturn(UNIVERSE_SIZE);
        given(solarSystem.getCoordinate()).willReturn(coordinateModel);
        given(coordinateModel.getCoordinate()).willReturn(coordinate);

        Universe result = underTest.create(GAME_ID, PLAYER_COUNT, settings);

        assertThat(result.getSize()).isEqualTo(UNIVERSE_SIZE);
        assertThat(result.getSystems()).containsEntry(coordinate, solarSystem);
        assertThat(result.getConnections()).isEmpty();
    }
}